package me.srrapero720.watermedia;

import me.srrapero720.watermedia.api.picture.cache.CacheStorage;
import me.srrapero720.watermedia.api.url.URLPatch;
import me.srrapero720.watermedia.core.lavaplayer.LavaCore;
import me.srrapero720.watermedia.core.videolan.VideoLAN;
import org.apache.commons.lang3.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

@SuppressWarnings("BooleanMethodIsAlwaysInverted")
public class WaterMedia {
	public static final String ID = "watermedia";
	public static final Logger LOGGER = LoggerFactory.getLogger(ID);

	public static boolean load(Path gameDir, boolean async) {
		throw new NotImplementedException("Still in progress");
	}

	public static boolean load(Path gameDir) {
		// PREPARE API
		if (!URLPatch.init()) return false;
		if (!CacheStorage.init(gameDir)) return false;

		// API LOADERS
		if (!VideoLAN.init(gameDir)) return false;
        if (!LavaCore.init()) return false;

		// API VERIFY
		return true;
	}
}