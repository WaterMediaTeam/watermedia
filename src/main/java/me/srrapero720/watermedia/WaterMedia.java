package me.srrapero720.watermedia;

import me.srrapero720.watermedia.api.WaterMediaAPI;
import me.srrapero720.watermedia.api.external.ThreadUtil;
import me.srrapero720.watermedia.core.LavaCore;
import me.srrapero720.watermedia.core.MediaCacheCore;
import me.srrapero720.watermedia.core.VideoLANCore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

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
		if (!LOADER.isClient() && !LOADER.isDevEnv()) {
			LOGGER.error(IT, "###########################  ILLEGAL ENVIRONMENT  ###################################");
			LOGGER.error(IT, "WATERMeDIA is not designed to run on SERVERS. remove this mod from server to stop crashes");
			LOGGER.error(IT, "If dependant mods throws error loading WATERMeDIA classes report it to the creator");
			LOGGER.error(IT, "###########################  ILLEGAL ENVIRONMENT  ###################################");

			SERVER_EXCEPTION = new IllegalStateException("WATERMeDIA is running on a invalid DIST (dedicated_server)");
		} else LOGGER.info("Special environment detected, avoiding forced server crash");

		// ENSURE FANCYVIDEO_API IS NOT INSTALLED (to prevent more bugreports about it)
		if (LOADER.isThisModPresent("fancyvideo_api"))
			CLIENT_EXCEPTION = new IllegalStateException("FancyVideo-API is explicit incompatible with WATERMeDIA, please remove it");
	}

	public void init() {
		LOGGER.info(IT, "Starting WaterMedia");
		if (!LOADER.isClient()) {
			LOGGER.info(IT, "WATERMeDIA is refusing cowardly to start in a illegal environment");
			return;
		}

		// PREPARE API
		LOGGER.info(IT, "Loading WaterMediaAPI");
		ThreadUtil.trySimple(() -> WaterMediaAPI.init(LOADER), e -> registerException("WaterMediaAPI", (RuntimeException) e));

		// PREPARE STORAGES
		LOGGER.info(IT, "Loading PictureStorage");
		ThreadUtil.trySimple(() -> MediaCacheCore.init(LOADER), e -> registerException("PictureStorage", (RuntimeException) e));

		// PREPARE VLC
		LOGGER.info(IT, "Loading VideoLAN");
		ThreadUtil.trySimple(() -> VideoLANCore.init(LOADER), e -> registerException("VideoLAN", (RuntimeException) e));

		// PREPARE LAVAPLAYER
		LOGGER.info(IT, "Loading LavaPlayer");
		ThreadUtil.trySimple(() -> LavaCore.init(LOADER), e -> registerException("LavaPlayer", (RuntimeException) e));

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
}