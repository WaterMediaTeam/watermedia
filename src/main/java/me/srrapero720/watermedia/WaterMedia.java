package me.srrapero720.watermedia;

import me.lib720.watermod.ThreadCore;
import me.srrapero720.watermedia.api.WaterMediaAPI;
import me.srrapero720.watermedia.api.loader.IEnvLoader;
import me.srrapero720.watermedia.api.loader.IMediaLoader;
import me.srrapero720.watermedia.core.JarAssets;
import me.srrapero720.watermedia.core.CacheStorage;
import me.srrapero720.watermedia.core.VideoLAN;
import me.srrapero720.watermedia.core.tools.exceptions.ReloadingException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.util.concurrent.locks.ReentrantLock;

@SuppressWarnings("BooleanMethodIsAlwaysInverted")
public class WaterMedia {
	public static final String ID = "watermedia";
	public static final String NAME = "WATERMeDIA";
	public static final Logger LOGGER = LogManager.getLogger(ID);
	public static final Marker IT = MarkerManager.getMarker("Bootstrap");
	private static final ReentrantLock LOCK = new ReentrantLock();

	// RETAINERS
	private static WaterMedia instance;
	private static volatile Exception exception;
	private final IMediaLoader loader;
	private IEnvLoader envLoader;

	public static WaterMedia getInstance() {
		if (instance == null) throw new IllegalStateException("not initialized");
		return instance;
	}

	public static WaterMedia getInstance(IMediaLoader loader) {
		if (instance == null && loader == null) throw new IllegalArgumentException("IMediaLoader must not be null with non instances");
		if (instance == null) return instance = new WaterMedia(loader);
		return instance;
	}

	private WaterMedia(IMediaLoader loader) {
		this.loader = loader;
		LOGGER.info(IT, "Running {} on {}", NAME, this.loader.getName());

        if (loader instanceof IEnvLoader) onEnvironmentInit((IEnvLoader) loader);
        else LOGGER.warn(IT, "Environment not detected");
    }

	public IEnvLoader getEnvLoader() { return envLoader; }
	public void onEnvironmentInit(IEnvLoader loader) {
		this.envLoader = loader;
		// ENSURE WATERMeDIA IS NOT RUNNING ON SERVERS (except FABRIC)
		if (!this.loader.getName().equalsIgnoreCase("fabric") && !loader.client() && !loader.development()) {
			exception = new IllegalAccessException("Environment is a server");

			LOGGER.error(IT, "###########################  ILLEGAL ENVIRONMENT  ###################################");
			LOGGER.error(IT, "Mod is not designed to run on SERVERS. remove this mod from server to stop crashes");
			LOGGER.error(IT, "If dependant mods throws error loading our classes then report it to the creator");
			LOGGER.error(IT, "###########################  ILLEGAL ENVIRONMENT  ###################################");
		}

		// ENSURE FANCYVIDEO_API IS NOT INSTALLED (to prevent more bugreports about it)
		if (loader.installed("fancyvideo_api"))
			exception = new IllegalStateException("FancyVideo-API detected, please remove it");

		// ENSURE IS NOT RUNNING BY TLAUNCHER
		if (loader.tlauncher())
			exception = new IllegalStateException("TLauncher is VIRUS and not supported. Use instead: SKLauncher or MultiMC");
	}

	public void init() {
		LOGGER.info(IT, "Starting modules");
		LOCK.lock();
		if (envLoader == null) LOGGER.warn(IT, "{} is starting without Environment, may cause problems", NAME);

		// RESOURCE EXTRACTOR
		LOGGER.info(IT, "Loading {}", JarAssets.class.getSimpleName());
		ThreadCore.trySimple(() -> JarAssets.init(this.loader), e -> onLoadFailed(JarAssets.class.getSimpleName(), e));

		// PREPARE API
		LOGGER.info(IT, "Loading {}", WaterMediaAPI.class.getSimpleName());
		ThreadCore.trySimple(() -> WaterMediaAPI.init(this.loader), e -> onLoadFailed(WaterMediaAPI.class.getSimpleName(), e));

		// PREPARE STORAGES
		LOGGER.info(IT, "Loading {}", CacheStorage.class.getSimpleName());
		ThreadCore.trySimple(() -> CacheStorage.init(this.loader), e -> onLoadFailed(CacheStorage.class.getSimpleName(), e));

		// PREPARE VLC
		LOGGER.info(IT, "Loading {}", VideoLAN.class.getSimpleName());
		ThreadCore.trySimple(() -> VideoLAN.init(this.loader), e -> onLoadFailed(VideoLAN.class.getSimpleName(), e));

		LOCK.unlock();
		LOGGER.info(IT, "Startup finished");
	}

	public void crash() {
		LOCK.lock();
		if (exception != null) throw new RuntimeException(exception);
		LOCK.unlock();
	}
	private void onLoadFailed(String module, Exception e) {
		LOGGER.error(IT, "Exception loading {}", module, e);
		if (exception != null && !(e instanceof ReloadingException)) exception = e;
	}
}