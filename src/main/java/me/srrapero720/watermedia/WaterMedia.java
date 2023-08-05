package me.srrapero720.watermedia;

import me.srrapero720.watermedia.api.WaterMediaAPI;
import me.srrapero720.watermedia.core.*;
import me.srrapero720.watermedia.core.exceptions.IllegalReloadException;
import me.srrapero720.watermedia.util.ThreadUtil;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.io.InputStream;
import java.util.concurrent.CompletableFuture;

@SuppressWarnings("BooleanMethodIsAlwaysInverted")
public class WaterMedia {
	public static final String ID = "watermedia";
	public static final Logger LOGGER = LoggerFactory.getLogger(ID);
	public static final Marker IT = MarkerFactory.getMarker("Bootstrap");

	// EXCEPTION RETAINER
	private volatile Exception exception;

	private final IMediaLoader loader;
	public WaterMedia(IMediaLoader modLoader) {
		this.loader = modLoader;
		LOGGER.info(IT, "Running WATERMeDIA on {}", loader.getLoaderName());

		// ENSURE WATERMeDIA IS NOT RUNNING ON SERVERS (except FABRIC)
		if (!this.loader.isClient() && !this.loader.isDev()) {
			exception = new IllegalStateException("WATERMeDIA is running on a invalid DIST (dedicated_server)");

			LOGGER.error(IT, "###########################  ILLEGAL ENVIRONMENT  ###################################");
			LOGGER.error(IT, "WATERMeDIA is not designed to run on SERVERS. remove this mod from server to stop crashes");
			LOGGER.error(IT, "If dependant mods throws error loading WATERMeDIA classes report it to the creator");
			LOGGER.error(IT, "###########################  ILLEGAL ENVIRONMENT  ###################################");
		}

		// ENSURE FANCYVIDEO_API IS NOT INSTALLED (to prevent more bugreports about it)
		if (this.loader.isInstalled("fancyvideo_api"))
			exception = new IllegalStateException("FancyVideo-API is explicit incompatible with WATERMeDIA, please remove it");

		// ENSURE IS NOT RUNNING BY TLAUNCHER
		if (this.loader.isTLauncher())
			exception = new IllegalStateException("[CRITICAL] TLauncher is a virus launcher and not supported by WATERMeDIA - Suggested: SKLauncher, MultiMC");
	}

	public void init() {
		synchronized (loader) {
			if (!this.loader.isClient()) return;
			LOGGER.info(IT, "Starting WaterMedia");

			// RESOURCE EXTRACTOR
			LOGGER.info(IT, "Loading {}", ResourceManager.class.getSimpleName());
			ThreadUtil.trySimple(() -> ResourceManager.init(this.loader), e -> exception(ResourceManager.class.getSimpleName(), e));

			// PREPARE API
			LOGGER.info(IT, "Loading {}", WaterMediaAPI.class.getSimpleName());
			ThreadUtil.trySimple(() -> WaterMediaAPI.init(this.loader), e -> exception(WaterMediaAPI.class.getSimpleName(), e));

			// PREPARE STORAGES
			LOGGER.info(IT, "Loading {}", MediaStorage.class.getSimpleName());
			ThreadUtil.trySimple(() -> MediaStorage.init(this.loader), e -> exception(MediaStorage.class.getSimpleName(), e));

			// PREPARE VLC
			LOGGER.info(IT, "Loading {}", VideoLAN.class.getSimpleName());
			ThreadUtil.trySimple(() -> VideoLAN.init(this.loader), e -> exception(VideoLAN.class.getSimpleName(), e));

			// PREPARE LAVAPLAYER
			LOGGER.info(IT, "Loading {}", LavaPlayer.class.getSimpleName());
			ThreadUtil.trySimple(() -> LavaPlayer.init(this.loader), e -> exception(LavaPlayer.class.getSimpleName(), e));

			LOGGER.info(IT, "Finished WaterMedia startup");
			if (exception != null) {
				LOGGER.warn(IT, "Detected some critical exceptions after startup");
				ThreadUtil.unsafeThread(() -> {
					ThreadUtil.trySimple(() -> Thread.sleep(60000));
					synchronized (loader) {
						if (exception != null) LOGGER.error("WATERMeDIA DETECTS REGISTERED EXCEPTIONS NOT THROW");
					}
					exceptionThrow();
				});
			}
		}
	}

	public void exceptionThrow() { synchronized (loader) { if (exception != null) throw new RuntimeException(exception); } }
	private void exception(String module, Exception e) {
		LOGGER.error(IT, "Exception loading {}", module, e);
		if (exception != null && !(e instanceof IllegalReloadException)) exception = e;
	}

	// TESTERS
	public boolean test$classLoader(ClassLoader loader) {
		try(InputStream dummy = loader.getResourceAsStream("/videolan/commandline.json"); InputStream maybe = loader.getResourceAsStream("videolan/win-x64.zip")) {
			return dummy != null || maybe != null;
		} catch (Exception e) {
			LOGGER.warn(IT, "ClassLoader test failed", e);
		}
		return false;
	}
}