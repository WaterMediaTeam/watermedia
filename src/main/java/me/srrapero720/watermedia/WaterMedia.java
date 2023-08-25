package me.srrapero720.watermedia;

import me.lib720.watermod.safety.TryCore;
import me.srrapero720.watermedia.api.WaterMediaAPI;
import me.srrapero720.watermedia.api.loader.IEnvLoader;
import me.srrapero720.watermedia.api.loader.IMediaLoader;
import me.srrapero720.watermedia.core.JarAssets;
import me.srrapero720.watermedia.core.CacheStorage;
import me.srrapero720.watermedia.core.VideoLAN;
import me.srrapero720.watermedia.core.tools.exceptions.ReInitException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.util.concurrent.locks.ReentrantLock;

public class WaterMedia {
	private static final Marker IT = MarkerManager.getMarker("Bootstrap");
	private static final ReentrantLock LOCK = new ReentrantLock();

	// INFO
	public static final String ID = "watermedia";
	public static final String NAME = "WATERMeDIA";
	public static final Logger LOGGER = LogManager.getLogger(ID);

	// RETAINERS
	private static WaterMedia instance;
	private final IMediaLoader loader;
	private IEnvLoader env;
	private static volatile Exception exception;

	public static WaterMedia getInstance() {
		if (instance == null) throw new IllegalStateException("No instance found");
		return instance;
	}

	public static WaterMedia getInstance(IMediaLoader loader) {
		if (instance == null && loader == null) throw new IllegalArgumentException("Loader cannot be null at the first instance");
		if (instance == null) return instance = new WaterMedia(loader);
		return instance;
	}

	private WaterMedia(IMediaLoader loader) {
		if (instance != null) throw new IllegalStateException("Already exists another WATERMeDIA instance");
		instance = this;

		this.loader = loader;
		LOGGER.info(IT, "Running {} on {}", NAME, this.loader.name());

		if (loader instanceof IEnvLoader) envInit((IEnvLoader) loader);
        else LOGGER.warn(IT, "Environment not detected, be careful about it");
	}

	public IEnvLoader getEnv() { return env; }
	public IMediaLoader getLoader() { return loader; }


	public void envInit(IEnvLoader loader) {
		this.env = loader;
		// ENSURE WATERMeDIA IS NOT RUNNING ON SERVERS (except FABRIC)
		if (!this.loader.name().equalsIgnoreCase("fabric") && !loader.client() && !loader.development()) {
			exception = new IllegalStateException("Cannot run WATERMeDIA on a server");

			LOGGER.error(IT, "###########################  ILLEGAL ENVIRONMENT  ###################################");
			LOGGER.error(IT, "Mod is not designed to run on SERVERS. remove this mod from server to stop crashes");
			LOGGER.error(IT, "If dependant mods throws error loading our classes then report it to the creator");
			LOGGER.error(IT, "###########################  ILLEGAL ENVIRONMENT  ###################################");
		}

		// ENSURE FANCYVIDEO_API IS NOT INSTALLED (to prevent more bugreports about it)
		if (loader.installed("fancyvideo_api")) exception = new IllegalStateException("FancyVideo-API is a incompatible mod. You have to remove it");

		// ENSURE IS NOT RUNNING BY TLAUNCHER
		if (loader.tlauncher()) exception = new IllegalStateException("TLauncher is UNSUPPORTED. Use instead SKLauncher or MultiMC");
		LOGGER.warn(IT, "Environment was init, don't need to worry about anymore");
	}

	public void init() {
		LOCK.lock();
		LOGGER.info(IT, "Starting modules");
		if (env == null) LOGGER.warn(IT, "{} is starting without Environment, may cause problems", NAME);

		// JAR ASSETS
		LOGGER.info(IT, "Loading {}", JarAssets.class.getSimpleName());
		TryCore.simple(() -> JarAssets.init(this.loader), e -> onFailed(JarAssets.class.getSimpleName(), e));

		// PREPARE API
		LOGGER.info(IT, "Loading {}", WaterMediaAPI.class.getSimpleName());
		TryCore.simple(() -> WaterMediaAPI.init(this.loader), e -> onFailed(WaterMediaAPI.class.getSimpleName(), e));

		// PREPARE STORAGES
		LOGGER.info(IT, "Loading {}", CacheStorage.class.getSimpleName());
		TryCore.simple(() -> CacheStorage.init(this.loader), e -> onFailed(CacheStorage.class.getSimpleName(), e));

		// PREPARE VLC
		LOGGER.info(IT, "Loading {}", VideoLAN.class.getSimpleName());
		TryCore.simple(() -> VideoLAN.init(this.loader), e -> onFailed(VideoLAN.class.getSimpleName(), e));

		LOCK.unlock();
		LOGGER.info(IT, "Startup finished");
	}

	public void crash() {
		LOCK.lock();
		if (exception != null) throw new RuntimeException(exception);
		LOCK.unlock();
	}

	private void onFailed(String module, Exception e) {
		LOGGER.error(IT, "Exception loading {}", module, e);
		if (exception != null && !(e instanceof ReInitException)) exception = e;
	}
}