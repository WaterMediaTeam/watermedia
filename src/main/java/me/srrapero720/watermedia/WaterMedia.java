package me.srrapero720.watermedia;

import me.srrapero720.watermedia.api.external.ThreadUtil;
import me.srrapero720.watermedia.api.images.LocalStorage;
import me.srrapero720.watermedia.core.lavaplayer.LavaCore;
import me.srrapero720.watermedia.core.videolan.VideoLAN;
import org.jetbrains.annotations.ApiStatus.Experimental;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

@SuppressWarnings("BooleanMethodIsAlwaysInverted")
public class WaterMedia {
	public static final String ID = "watermedia";
	public static final Logger LOGGER = LoggerFactory.getLogger(ID);

	@Experimental
	public static boolean load(Path gameDir, boolean async) {
		if (async) {
			ThreadUtil.thread(() -> load(gameDir));
			return true;
		}
		else return load(gameDir);
	}

	public static boolean load(Path gameDir) {
		LOGGER.info("Loading WaterMedia on directory {}", gameDir);

		// PREPARE API
		if (!LocalStorage.init(gameDir)) return false;

		// API LOADERS
		if (!VideoLAN.init(gameDir)) return false;
        if (!LavaCore.init()) return false;

		LOGGER.info("WaterMedia loaded successfully");
		// API VERIFY
		return true;
	}
}