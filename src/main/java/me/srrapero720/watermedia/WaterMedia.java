package me.srrapero720.watermedia;

import me.srrapero720.watermedia.api.video.patch.AbstractURLPatch;
import me.srrapero720.watermedia.lavaplayer.LavaManager;
import me.srrapero720.watermedia.vlc.VLCManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

@SuppressWarnings("BooleanMethodIsAlwaysInverted")
public class WaterMedia {
	public static final String ID = "watermedia";
	public static final Logger LOGGER = LoggerFactory.getLogger(ID);

	public static boolean load(Path gameDir) {
		// PREPARE API
		if (!AbstractURLPatch.init()) return false;

		// API LOADERS
		if (!VLCManager.init(gameDir)) return false;
        if (!LavaManager.init()) return false;

		// API VERIFY
		return true;
	}
}