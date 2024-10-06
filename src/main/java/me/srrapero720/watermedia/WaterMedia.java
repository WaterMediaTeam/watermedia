package me.srrapero720.watermedia;

import com.sun.jna.Platform;
import me.srrapero720.watermedia.api.WaterMediaAPI;
import me.srrapero720.watermedia.tools.DataTool;
import me.srrapero720.watermedia.tools.JarTool;
import me.srrapero720.watermedia.loader.ILoader;
import me.srrapero720.watermedia.loader.IModule;
import me.srrapero720.watermedia.tools.PairTool;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.util.*;

public final class WaterMedia {
	private static final Marker IT = MarkerManager.getMarker("Bootstrap");

	public static final String ID = "watermedia";
	public static final String NAME = "WATERMeDIA";
	public static final String VERSION = JarTool.readString("/watermedia/version.cfg");
	public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) Chrome/112.0.0.0 Edg/112.0.1722.68 WaterMedia/" + VERSION;

	public static final Logger LOGGER = LogManager.getLogger(ID);
	private static final Set<IModule> MODULES = new HashSet<>();

	private static final PairTool<String, Boolean> NO_BOOT = getArgument("watermedia.disableBoot");
	private static final PairTool<String, Boolean> HARDFAIL = getArgument("watermedia.hardFail");
	private static ILoader bootstrap;
	private static WaterMedia instance;

	private WaterMedia() {}

	public static WaterMedia prepare(ILoader boot) {
		if (boot == null) throw new NullPointerException("Bootstrap is null");
		if (instance != null) throw new NullPointerException(NAME + " is already prepared");
		LOGGER.info(IT, "Preparing '{}' for '{}'", NAME, boot.name());
		LOGGER.info(IT, "Loading {} version '{}'", NAME, VERSION);
		LOGGER.info(IT, "Detected OS: {} ({})", System.getProperty("os.name"), Platform.ARCH);

		if (NO_BOOT.value())
			LOGGER.warn(IT, "disableBoot argument detected, skipping booting");

		if (HARDFAIL.value())
			LOGGER.warn(IT, "HardFail argument detected, {} will crash at the minimum exception", NAME);

		if (!boot.client() && !boot.name().contains("Fabric"))
			throw new UnsupportedSideException();

		WaterMedia.bootstrap = boot;
		return instance = new WaterMedia();
	}

	public static void register(IModule module) {
		MODULES.add(module);
	}

	public static Set<IModule> modules() {
		return MODULES;
	}

	public void start() throws Exception {
		if (NO_BOOT.right()) return;
		if (!bootstrap.client()) return;

		List<WaterMediaAPI> modules = DataTool.toList(ServiceLoader.load(WaterMediaAPI.class));
		modules.sort(Comparator.comparingInt(e -> e.priority().ordinal()));

		for (WaterMediaAPI m: modules) {
			LOGGER.info(IT, "Starting {}", m.getClass().getSimpleName());
			if (!m.prepare(bootstrap)) {
				LOGGER.warn(IT, "Module {} refuses to be loaded, skipping", m.getClass().getSimpleName());
				continue;
			}
			m.start(bootstrap);
			LOGGER.info(IT, "Module {} loaded successfully", m.getClass().getSimpleName());
		}
		LOGGER.info(IT, "Startup finished");
	}

	public static ILoader getLoader() { return bootstrap; }


	public static String asResource(String path) {
		return WaterMedia.ID + ":" + path;
	}

	public static PairTool<String, Boolean> getArgument(String argument) {
		return new PairTool<>(argument, Boolean.parseBoolean(System.getProperty(argument)));
	}

	public static class UnsupportedSideException extends RuntimeException {
		public UnsupportedSideException() {
			super(NAME + " CANNOT be installed on SERVER-SIDE. Please remove " + NAME + " from the server, and keep it on client");
			LOGGER.fatal(IT, "##############################  ILLEGAL ENVIRONMENT  ######################################");
			LOGGER.fatal(IT, "{} is not designed to work on server-side, please remove it from server and keep it on client", NAME);
			LOGGER.fatal(IT, "Dependent mods can work without {} ON SERVERS, remember keep the mod ONLY ON CLIENT-SIDE", NAME);
			LOGGER.fatal(IT, "if dependent mods throws exceptions ON SERVER asking for WATERMeDIA, report it to the creators");
			LOGGER.fatal(IT, "##############################  ILLEGAL ENVIRONMENT  ######################################");
		}
	}

	public static class UnsupportedTLException extends Exception {
		public UnsupportedTLException() {
			super("TLauncher is NOT supported by " + NAME + ", please stop using it (and consider safe alternatives like SKLauncher or MultiMC)");
			LOGGER.fatal(IT, "##############################  ILLEGAL LAUNCHER DETECTED ######################################");
			LOGGER.fatal(IT, "{} refuses to load sensitive modules in a INFECTED launcher, please stop using TLauncher dammit", NAME);
			LOGGER.fatal(IT, "Because TLauncher infects sensitive files (which {} includes) and we prefer avoid any risk", NAME);
			LOGGER.fatal(IT, "Consider use safe alternative like SKLauncher or BUY the game and use the CurseForge Launcher");
			LOGGER.fatal(IT, "And please avoid Feather Launcher, TLauncher Legacy or any CRACKED LAUNCHER WITH A BAD REPUTATION");
			LOGGER.fatal(IT, "##############################  ILLEGAL LAUNCHER DETECTED  ######################################");
		}
	}
}