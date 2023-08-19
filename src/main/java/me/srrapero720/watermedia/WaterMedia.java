package me.srrapero720.watermedia;

import me.lib720.watermod.ThreadCore;
import me.srrapero720.watermedia.api.WaterMediaAPI;
import me.srrapero720.watermedia.api.loader.IEnvLoader;
import me.srrapero720.watermedia.api.loader.IMediaLoader;
import me.srrapero720.watermedia.core.AssetsExtractor;
import me.srrapero720.watermedia.core.CacheStorage;
import me.srrapero720.watermedia.core.VideoLAN;
import me.srrapero720.watermedia.core.tools.exceptions.ReloadingException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

@SuppressWarnings("BooleanMethodIsAlwaysInverted")
public class WaterMedia {
	public static final String ID = "watermedia";
	public static final Logger LOGGER = LogManager.getLogger(ID);
	public static final Marker IT = MarkerManager.getMarker("Bootstrap");

	// RETAINERS
	private static WaterMedia instance;
	private static volatile Exception exception;
	private final IMediaLoader loader;
	private IEnvLoader envLoader;

	public static WaterMedia getInstance() {
		if (instance == null) throw new IllegalStateException("WATERMeDIA is not initialized");
		return instance;
	}

	public static WaterMedia getInstance(IMediaLoader loader) {
		if (instance == null && loader == null) throw new IllegalArgumentException("IMediaLoader must not be null with non instances");
		if (instance == null) return instance = new WaterMedia(loader);
		return instance;
	}

	private WaterMedia(IMediaLoader loader) {
		this.loader = loader;
		LOGGER.info(IT, "Running WATERMeDIA on {}", this.loader.getName());

        if (loader instanceof IEnvLoader) onEnvironmentInit((IEnvLoader) loader);
        else LOGGER.warn(IT, "Environment not detected on instance");
    }

	public IEnvLoader getEnvLoader() { return envLoader; }
	public void onEnvironmentInit(IEnvLoader loader) {
		this.envLoader = loader;
		// ENSURE WATERMeDIA IS NOT RUNNING ON SERVERS (except FABRIC)
		if (!this.loader.getName().equalsIgnoreCase("fabric") && !loader.client() && !loader.development()) {
			exception = new IllegalStateException("WATERMeDIA is running on SERVER");

			LOGGER.error(IT, "###########################  ILLEGAL ENVIRONMENT  ###################################");
			LOGGER.error(IT, "WATERMeDIA is not designed to run on SERVERS. remove this mod from server to stop crashes");
			LOGGER.error(IT, "If dependant mods throws error loading WATERMeDIA classes report it to the creator");
			LOGGER.error(IT, "###########################  ILLEGAL ENVIRONMENT  ###################################");
		}

		// ENSURE FANCYVIDEO_API IS NOT INSTALLED (to prevent more bugreports about it)
		if (loader.installed("fancyvideo_api"))
			exception = new IllegalStateException("FancyVideo-API is explicit incompatible with WATERMeDIA, please remove it");

		// ENSURE IS NOT RUNNING BY TLAUNCHER
		if (loader.tlauncher())
			exception = new IllegalStateException("[CRITICAL] TLauncher is a virus launcher and not supported by WATERMeDIA - Suggested: SKLauncher, MultiMC");
	}

	public void init() {
		LOGGER.info(IT, "Starting WaterMedia");
		if (envLoader == null) LOGGER.warn(IT, "WATERMeDIA is starting without Environment, may cause problems");

		// RESOURCE EXTRACTOR
		LOGGER.info(IT, "Loading {}", AssetsExtractor.class.getSimpleName());
		ThreadCore.trySimple(() -> AssetsExtractor.init(this.loader), e -> onLoadFailed(AssetsExtractor.class.getSimpleName(), e));

		// PREPARE API
		LOGGER.info(IT, "Loading {}", WaterMediaAPI.class.getSimpleName());
		ThreadCore.trySimple(() -> WaterMediaAPI.init(this.loader), e -> onLoadFailed(WaterMediaAPI.class.getSimpleName(), e));

		// PREPARE STORAGES
		LOGGER.info(IT, "Loading {}", CacheStorage.class.getSimpleName());
		ThreadCore.trySimple(() -> CacheStorage.init(this.loader), e -> onLoadFailed(CacheStorage.class.getSimpleName(), e));

		// PREPARE VLC
		LOGGER.info(IT, "Loading {}", VideoLAN.class.getSimpleName());
		ThreadCore.trySimple(() -> VideoLAN.init(this.loader), e -> onLoadFailed(VideoLAN.class.getSimpleName(), e));

		LOGGER.info(IT, "Finished WaterMedia startup");
	}

	public void crash() { if (exception != null) throw new RuntimeException(exception); }
	private void onLoadFailed(String module, Exception e) {
		LOGGER.error(IT, "Exception loading {}", module, e);
		if (exception != null && !(e instanceof ReloadingException)) exception = e;
	}
}