package me.srrapero720.watermedia;

import me.srrapero720.watermedia.api.WaterMediaAPI;
import me.srrapero720.watermedia.util.ThreadUtil;
import me.srrapero720.watermedia.core.LavaPlayer;
import me.srrapero720.watermedia.core.MediaStorage;
import me.srrapero720.watermedia.core.VideoLANBin;
import me.srrapero720.watermedia.core.VideoLAN;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.io.InputStream;

@SuppressWarnings("BooleanMethodIsAlwaysInverted")
public class WaterMedia {
	public static final String ID = "watermedia";
	public static final Logger LOGGER = LoggerFactory.getLogger(ID);
	public static final Marker IT = MarkerFactory.getMarker("Bootstrap");

	// EXCEPTION RETAINER
	private RuntimeException CLIENT_EXCEPTION;
	private RuntimeException SERVER_EXCEPTION;

	private final IMediaLoader LOADER;
	public WaterMedia(IMediaLoader modLoader) {
		LOADER = modLoader;
		LOGGER.info(IT, "Running WATERMeDIA on {}", LOADER.getLoaderName());

		// ENSURE WATERMeDIA IS NOT RUNNING ON SERVERS (except FABRIC)
		if (!LOADER.isClient() && !LOADER.isDev()) {
			LOGGER.error(IT, "###########################  ILLEGAL ENVIRONMENT  ###################################");
			LOGGER.error(IT, "WATERMeDIA is not designed to run on SERVERS. remove this mod from server to stop crashes");
			LOGGER.error(IT, "If dependant mods throws error loading WATERMeDIA classes report it to the creator");
			LOGGER.error(IT, "###########################  ILLEGAL ENVIRONMENT  ###################################");

			SERVER_EXCEPTION = new IllegalStateException("WATERMeDIA is running on a invalid DIST (dedicated_server)");
		} else LOGGER.info(IT, "Special environment detected, avoiding forced server crash");

		// ENSURE FANCYVIDEO_API IS NOT INSTALLED (to prevent more bugreports about it)
		if (LOADER.isInstalled("fancyvideo_api"))
			CLIENT_EXCEPTION = new IllegalStateException("FancyVideo-API is explicit incompatible with WATERMeDIA, please remove it");

		// ENSURE IS NOT RUNNING BY TLAUNCHER
		if (LOADER.isTLauncher())
			CLIENT_EXCEPTION = new IllegalStateException("[CRITICAL] TLauncher is a virus launcher and not supported by WATERMeDIA - Suggested: SKLauncher, MultiMC");
	}

	public void init() {
		LOGGER.info(IT, "Starting WaterMedia");
		if (!LOADER.isClient()) {

			LOGGER.info(IT, "WATERMeDIA is refusing cowardly to start in a illegal environment");
			return;
		}

		// PREPARE API
		LOGGER.info(IT, "Loading {}", WaterMediaAPI.class.getSimpleName());
		ThreadUtil.trySimple(() -> WaterMediaAPI.init(LOADER), e -> registerException(WaterMediaAPI.class.getSimpleName(), (RuntimeException) e));

		// PREPARE STORAGES
		LOGGER.info(IT, "Loading {}", MediaStorage.class.getSimpleName());
		ThreadUtil.trySimple(() -> MediaStorage.init(LOADER), e -> registerException(MediaStorage.class.getSimpleName(), (RuntimeException) e));

		// PREPARE VLC BINARIES
		LOGGER.info(IT, "Loading {}", VideoLANBin.class.getSimpleName());
		ThreadUtil.trySimple(() -> VideoLANBin.init(LOADER), e -> registerException(VideoLANBin.class.getSimpleName(), (RuntimeException) e));

		// PREPARE VLC
		LOGGER.info(IT, "Loading {}", VideoLAN.class.getSimpleName());
		ThreadUtil.trySimple(() -> VideoLAN.init(LOADER), e -> registerException(VideoLAN.class.getSimpleName(), (RuntimeException) e));

		// PREPARE LAVAPLAYER
		LOGGER.info(IT, "Loading {}", LavaPlayer.class.getSimpleName());
		ThreadUtil.trySimple(() -> LavaPlayer.init(LOADER), e -> registerException(LavaPlayer.class.getSimpleName(), (RuntimeException) e));

		LOGGER.info(IT, "Finished WaterMedia startup");
		if (existsExceptions()) LOGGER.warn(IT, "Detected some critical exceptions after startup");
	}

	private void registerException(String module, RuntimeException e) {
		LOGGER.error(IT, "Exception loading {}", module, e);
		if (CLIENT_EXCEPTION != null) CLIENT_EXCEPTION = e;
	}

	public boolean existsExceptions() { return CLIENT_EXCEPTION != null || SERVER_EXCEPTION != null; }
	public void throwClientException() { if (CLIENT_EXCEPTION != null) throw CLIENT_EXCEPTION; }
	public void throwServerException() { if (SERVER_EXCEPTION != null) throw SERVER_EXCEPTION; }
	public boolean workingClassLoader(ClassLoader loader) {
		InputStream dummy = loader.getResourceAsStream("/vlc/args.json");
		IOUtils.closeQuietly(dummy);
		return dummy != null;
	}
}