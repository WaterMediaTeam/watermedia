package org.watermedia;

import com.sun.jna.Platform;
import org.watermedia.api.WaterMediaAPI;
import org.watermedia.tools.DataTool;
import org.watermedia.tools.JarTool;
import org.watermedia.tools.ArgTool;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.io.File;
import java.nio.file.Path;
import java.util.*;

public final class WaterMedia {
	private static final Marker IT = MarkerManager.getMarker("Bootstrap");
	public static final String ID = "watermedia";
	public static final String NAME = "WATERMeDIA";
	public static final String VERSION = JarTool.readString("/watermedia/version.cfg");
	public static final String USER_AGENT = "WaterMedia/" + VERSION;
	public static final Logger LOGGER = LogManager.getLogger(ID);

	private static final Path DEFAULT_TEMP = new File(System.getProperty("java.io.tmpdir")).toPath().toAbsolutePath().resolve("watermedia");
	private static final Path DEFAULT_CWD = new File("run").toPath().toAbsolutePath();
	public static final ILoader DEFAULT_LOADER = new ILoader() {
		@Override public String name() { return "Default"; }
		@Override public Path tmp() { return DEFAULT_TEMP; }
		@Override public Path cwd() { return DEFAULT_CWD; }
		@Override public boolean client() { return true; }
	};

	private static final ArgTool NO_BOOT = DataTool.getArgument("watermedia.no_boot");
	private static final ArgTool FAIL_HARD = DataTool.getArgument("watermedia.fail_hard");
	private static ILoader bootstrap;
	private static WaterMedia instance;

	private WaterMedia() {}

	public static WaterMedia prepare(ILoader boot) {
		if (boot == null) throw new NullPointerException("Bootstrap is null");
		if (instance != null) throw new IllegalStateException(NAME + " is already prepared");
		LOGGER.info(IT, "Preparing '{}' for '{}'", NAME, boot.name());
		LOGGER.info(IT, "Loading {} version '{}'", NAME, VERSION);
		LOGGER.info(IT, "Detected OS: {} ({})", System.getProperty("os.name"), Platform.ARCH);

		if (NO_BOOT.getAsBoolean())
			LOGGER.warn(IT, "{} argument detected, API booting is disabled", NO_BOOT.key());

		if (FAIL_HARD.getAsBoolean())
			LOGGER.warn(IT, "{} argument detected, crashes will be threw at the minimal exception", NO_BOOT.key());

		if (!boot.client())
			LOGGER.warn(IT, "{} is installed on server-side, please report issues to the dependent mod author instead of {} author", NAME, NAME);

		WaterMedia.bootstrap = boot;
		return instance = new WaterMedia();
	}

	public void start() throws Exception {
		if (NO_BOOT.getAsBoolean()) return;
		if (!bootstrap.client()) return;

		final var modules = DataTool.toList(ServiceLoader.load(WaterMediaAPI.class));
		modules.sort(Comparator.comparingInt(e -> e.priority().ordinal()));

		for (WaterMediaAPI m: modules) {
			LOGGER.info(IT, "Starting '{}'", m.getClass().getSimpleName());
			if (!m.prepare(bootstrap)) {
				LOGGER.warn(IT, "Module '{}' refuses to be loaded, skipping", m.getClass().getSimpleName());
				continue;
			}
			m.start(bootstrap);
			LOGGER.info(IT, "Module '{}' loaded successfully", m.getClass().getSimpleName());
		}
		LOGGER.info(IT, "Startup finished");
	}

	public static ILoader getLoader() { return bootstrap; }

	public static String asResource(String path) { return WaterMedia.ID + ":" + path; }

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

	public interface ILoader {
		String name();
		Path tmp();
		Path cwd();
		boolean client();
	}
}