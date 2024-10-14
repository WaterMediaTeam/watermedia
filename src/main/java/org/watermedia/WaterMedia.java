package org.watermedia;

import com.sun.jna.Platform;
import org.watermedia.api.WaterMediaAPI;
import org.watermedia.tools.DataTool;
import org.watermedia.tools.JarTool;
import me.srrapero720.watermedia.loader.ILoader;
import org.watermedia.tools.PairTool;
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
	public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/129.0.0.0 Safari/537.36 Edg/129.0.0.0";
	public static final Logger LOGGER = LogManager.getLogger(ID);

	private static final PairTool<String, Boolean> NO_BOOT = DataTool.getArgument("watermedia.no_boot");
	private static final PairTool<String, Boolean> FAIL_HARD = DataTool.getArgument("watermedia.fail_hard");
	private static ILoader bootstrap;
	private static WaterMedia instance;

	private WaterMedia() {}

	public static WaterMedia prepare(ILoader boot) {
		if (boot == null) throw new NullPointerException("Bootstrap is null");
		if (instance != null) throw new IllegalStateException(NAME + " is already prepared");
		LOGGER.info(IT, "Preparing '{}' for '{}'", NAME, boot.name());
		LOGGER.info(IT, "Loading {} version '{}'", NAME, VERSION);
		LOGGER.info(IT, "Detected OS: {} ({})", System.getProperty("os.name"), Platform.ARCH);

		if (NO_BOOT.value())
			LOGGER.warn(IT, "{} argument detected, API booting is disabled", NO_BOOT.key());

		if (FAIL_HARD.value())
			LOGGER.warn(IT, "{} argument detected, crashes will be threw at the minimal exception", NO_BOOT.key());

		if (!boot.client() && !boot.name().contains("Fabric"))
			throw new UnsupportedSideException();

		WaterMedia.bootstrap = boot;
		return instance = new WaterMedia();
	}

	public void start() throws Exception {
		if (NO_BOOT.right()) return;
		if (!bootstrap.client()) return;

		final var modules = DataTool.toList(ServiceLoader.load(WaterMediaAPI.class));
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

	public static String asResource(String path) { return WaterMedia.ID + ":" + path; }

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