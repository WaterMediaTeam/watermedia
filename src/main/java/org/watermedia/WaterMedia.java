package org.watermedia;

import com.sun.jna.Platform;
import org.watermedia.api.WaterMediaAPI;
import org.watermedia.core.exceptions.UnsupportedArchitechtureException;
import org.watermedia.core.tools.ArgTool;
import org.watermedia.core.tools.DataTool;
import org.watermedia.core.tools.JarTool;
import org.watermedia.loaders.ILoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.nio.file.Path;
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

	public static final ArgTool NO_BOOTING = new ArgTool("watermedia.disableBoot");
	public static final ArgTool NO_VLC = new ArgTool("watermedia.disableVLC");

    private static ILoader bootstrap;
	private static WaterMedia instance;

	private WaterMedia() {}

	public static WaterMedia prepare(ILoader boot) {
		if (boot == null) throw new NullPointerException("Bootstrap is null");
		if (instance != null) throw new NullPointerException("WaterMedia is already prepared");
		LOGGER.info(IT, "Preparing '{}' on '{}'", NAME, boot.name());
		LOGGER.info(IT, "WaterMedia version '{}'", VERSION);
		LOGGER.info(IT, "OS Detected: {} ({})", System.getProperty("os.name"), Platform.ARCH);
		LOGGER.info(IT, "Runtime memory Usage: {}MB/{}MB", (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1048576, Runtime.getRuntime().maxMemory() / 1048576);

		WaterMedia.bootstrap = boot;
		return instance = new WaterMedia();
	}

	public void start() throws Exception {
		if (NO_BOOTING.getAsBoolean()) {
			LOGGER.error(IT, "Refusing to bootstrap WATERMeDIA, detected {}", NO_BOOTING);
			return;
		}

		List<WaterMediaAPI> modules = DataTool.toList(ServiceLoader.load(WaterMediaAPI.class));
		modules.sort(Comparator.comparingInt(e -> e.priority().ordinal()));

		if (!Platform.is64Bit())
			throw new UnsupportedArchitechtureException();

		for (WaterMediaAPI m: modules) {
			LOGGER.info(IT, "Starting {}", m.getClass().getSimpleName());
			if (!m.prepare(bootstrap)) {
				LOGGER.warn(IT, "Module {} refuses to be loaded, skipping", m.getClass().getSimpleName());
				continue;
			}
			m.start(bootstrap);
			LOGGER.info(IT, "Module {} loaded successfully", m.getClass().getSimpleName());
		}
        LOGGER.info(IT, "Setting up shutdown hook");
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            LOGGER.info(IT, "Shutting down...");
            for (WaterMediaAPI m : modules) {
                try {
                    LOGGER.info(IT, "Releasing {}", m.getClass().getSimpleName());
                    m.release();
                } catch (Exception e) {
                    LOGGER.error(IT, "Failed to release module {}: {}", m.getClass().getSimpleName(), e.getMessage(), e);
                }
            }
            LOGGER.info(IT, "Shutdown complete");
        }));

		LOGGER.info(IT, "Startup finished");
		LOGGER.info(IT, "Are you ready for trouble?");
	}

	public static ILoader getLoader() { return bootstrap; }

	public static Path getConfigDir() {
		return bootstrap.processDir().resolve("config/watermedia");
	}

    /**
     * Enables or disables slavism mode, which enables some extra features
     * @deprecated This method is deprecated and will be removed in future versions. It has no effect.
     * @param mode true to enable slavism mode, false to disable it
     */
    @Deprecated(forRemoval = true)
	public static void setSlavismMode(boolean mode) {
	}


	public static String asResource(String path) {
		return WaterMedia.ID + ":" + path;
	}
}