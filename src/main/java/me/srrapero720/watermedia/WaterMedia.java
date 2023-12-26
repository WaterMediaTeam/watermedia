package me.srrapero720.watermedia;

import me.lib720.watermod.safety.TryCore;
import me.srrapero720.watermedia.api.WaterMediaAPI;
import me.srrapero720.watermedia.api.image.ImageAPI;
import me.srrapero720.watermedia.api.loader.IEnvLoader;
import me.srrapero720.watermedia.api.loader.IMediaLoader;
import me.srrapero720.watermedia.api.url.UrlAPI;
import me.srrapero720.watermedia.core.AssetsCore;
import me.srrapero720.watermedia.core.CacheCore;
import me.srrapero720.watermedia.core.VideoLanCore;
import me.srrapero720.watermedia.core.tools.JarTool;
import me.srrapero720.watermedia.core.tools.exceptions.ReInitException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.util.concurrent.locks.ReentrantLock;

public class WaterMedia {
	private static final Marker IT = MarkerManager.getMarker("Bootstrap");
	private static final ReentrantLock LOCK = new ReentrantLock();
	private static final String NBP_NAME = "watermedia.disableBoot";
	private static final boolean NBP = Boolean.parseBoolean(System.getProperty(NBP_NAME));

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
		if (instance == null) throw new IllegalStateException("Instance wasn't created");
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
		LOGGER.info(IT, "Running '{}' on '{}'", NAME, this.loader.name());
		LOGGER.info(IT, "WaterMedia version '{}'", JarTool.readString(WaterMedia.class.getClassLoader(), "/watermedia/version.cfg"));

		if (loader instanceof IEnvLoader) envInit((IEnvLoader) loader);
        else LOGGER.warn(IT, "Environment not detected, be careful about it");
	}

	public IEnvLoader env() { return env; }
	public IMediaLoader loader() { return loader; }


	public void envInit(IEnvLoader loader) {
		if (this.env != null) LOGGER.warn(IT, "Override environment is a deprecated feature");
		if (NBP) {
			LOGGER.error(IT, "Cowardly refusing to load WATERMeDIA environment, detected {}=true", NBP_NAME);
			return;
		}

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
		if (NBP) {
			LOGGER.error(IT, "Cowardly refusing to bootstrap WATERMeDIA, detected {}=true", NBP_NAME);
			return;
		}

		LOCK.lock();
		LOGGER.info(IT, "Starting modules");
		if (env == null) LOGGER.warn(IT, "{} is starting without Environment, may cause problems", NAME);

		// JAR ASSETS
		LOGGER.info(IT, "Loading {}", AssetsCore.class.getSimpleName());
		TryCore.simple(() -> AssetsCore.init(this.loader), e -> onFailed(AssetsCore.class.getSimpleName(), e));

		// PREPARE STORAGES
		LOGGER.info(IT, "Loading {}", CacheCore.class.getSimpleName());
		TryCore.simple(() -> CacheCore.init(this.loader), e -> onFailed(CacheCore.class.getSimpleName(), e));

		// IMAGE API
		LOGGER.info(IT, "Loading {}", ImageAPI.class.getSimpleName());
		TryCore.simple(() -> ImageAPI.init(this.loader), e -> onFailed(ImageAPI.class.getSimpleName(), e));

		// URL API
		LOGGER.info(IT, "Loading {}", UrlAPI.class.getSimpleName());
		TryCore.simple(() -> UrlAPI.init(this.loader), e -> onFailed(UrlAPI.class.getSimpleName(), e));

		// PREPARE API
		LOGGER.info(IT, "Loading {}", WaterMediaAPI.class.getSimpleName());
		LOGGER.info(IT, "Warning: {} is deprecated for removal", WaterMediaAPI.class.getSimpleName());

		// PREPARE VLC
		LOGGER.info(IT, "Loading {}", VideoLanCore.class.getSimpleName());
		TryCore.simple(() -> VideoLanCore.init(this.loader), e -> onFailed(VideoLanCore.class.getSimpleName(), e));

		LOGGER.info(IT, "Startup finished");
		LOCK.unlock();
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