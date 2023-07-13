package me.srrapero720.watermedia;

import me.srrapero720.watermedia.api.external.ThreadUtil;
import me.srrapero720.watermedia.core.lavaplayer.LavaCore;
import me.srrapero720.watermedia.core.storage.PictureStorage;
import me.srrapero720.watermedia.core.util.IModLoader;
import me.srrapero720.watermedia.core.videolan.VideoLAN;
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

	private final IModLoader LOADER;
	public WaterMedia(IModLoader modLoader) {
		LOADER = modLoader;
		LOGGER.info(IT, "Running WATERMeDIA on {}", LOADER.getLoaderName());

		// ENSURE WATERMeDIA IS NOT RUNNING ON SERVERS (except FABRIC)
		if (!LOADER.isClient() && !LOADER.getLoaderName().equalsIgnoreCase("fabric") && !LOADER.isDevEnv()) {
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
		LOGGER.info(IT, "Loading PictureStorage");
		ThreadUtil.trySimple(() -> PictureStorage.init(LOADER), (e) -> LOGGER.error("Exception loading PictureStorage", e));

		// PREPARE VLC
		LOGGER.info(IT, "Loading VideoLAN");
		ThreadUtil.trySimple(() -> VideoLAN.init(LOADER), (e) -> LOGGER.error("Exception loading VideoLAN", e));

		// PREPARE LAVAPLAYER
		LOGGER.info(IT, "Loading LavaPlayer");
		ThreadUtil.trySimple(() -> LavaCore.init(LOADER), (e) -> LOGGER.error("Exception loading LavaPlayer", e));

		LOGGER.info(IT, "WaterMedia started successfully");
	}

	public void throwClientException() {
		if (CLIENT_EXCEPTION != null) throw CLIENT_EXCEPTION;
	}

	public void throwServerException() {
		if (SERVER_EXCEPTION != null) throw SERVER_EXCEPTION;
	}
}