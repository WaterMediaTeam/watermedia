package org.watermedia;

import com.sun.jna.Platform;
import org.watermedia.api.WaterMediaAPI;
import org.watermedia.core.tools.DataTool;
import org.watermedia.core.tools.JarTool;
import org.watermedia.loaders.ILoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.util.Comparator;
import java.util.List;
import java.util.ServiceLoader;

public class WaterMedia {
	public static final String ID = "watermedia";
	public static final String NAME = "WATERMeDIA";
	public static final Logger LOGGER = LogManager.getLogger(ID);
	public static final Marker IT = MarkerManager.getMarker("Bootstrap");
	public static final String VERSION = JarTool.readString("/watermedia/version.cfg");
	public static final String USER_AGENT = "WaterMedia/" + VERSION;

    private static final String NO_BOOT_NAME = "watermedia.disableBoot";
	private static final String SLAVIST_NAME = "watermedia.slavist";
	private static final boolean NO_BOOT = Boolean.parseBoolean(System.getProperty(NO_BOOT_NAME));
	private static boolean SLAVIST = Boolean.parseBoolean(System.getProperty(SLAVIST_NAME));
	private static ILoader bootstrap;
	private static WaterMedia instance;

	private WaterMedia() {}

	public static WaterMedia prepare(ILoader boot) {
		if (boot == null) throw new NullPointerException("Bootstrap is null");
		if (instance != null) throw new NullPointerException("WaterMedia is already prepared");
		LOGGER.info(IT, "Preparing '{}' on '{}'", NAME, boot.name());
		LOGGER.info(IT, "WaterMedia version '{}'", VERSION);
		LOGGER.info(IT, "OS Detected: {} ({})", System.getProperty("os.name"), Platform.ARCH);

		WaterMedia.bootstrap = boot;
		return instance = new WaterMedia();
	}

	public void start() throws Exception {
		if (NO_BOOT) {
			LOGGER.error(IT, "Refusing to bootstrap WATERMeDIA, detected D{}=true", NO_BOOT_NAME);
			return;
		}

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

	public static boolean isSlavist() {
		return SLAVIST;
	}

	public static void slavist(boolean beOne) {
		SLAVIST = beOne;
	}


	public static String asResource(String path) {
		return WaterMedia.ID + ":" + path;
	}
}