package me.srrapero720.watermedia;

import me.srrapero720.watermedia.api.video.patch.BaseVideoPatch;
import me.srrapero720.watermedia.lavaplayer.LavaManager;
import me.srrapero720.watermedia.vlc.VLCManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

@SuppressWarnings("BooleanMethodIsAlwaysInverted")
public class WaterMedia {
	public static final String ID = "watermedia";
	public static final Logger LOGGER = LoggerFactory.getLogger(ID);

	public static boolean load(Path gameDir, boolean inDev) {
		// PREPARE API
		if (!BaseVideoPatch.init()) return false;

		// API LOADERS
		if (!VLCManager.init(gameDir, true)) return false;
        if (!LavaManager.init()) return false;

		// API VERIFY
		return true;
	}

	public static boolean load(Path gameDir, WMConfig config, boolean inDev) {
		throw new UnsupportedOperationException("This method isn't supported yet");
	}
}