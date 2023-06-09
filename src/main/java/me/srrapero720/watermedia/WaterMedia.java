package me.srrapero720.watermedia;

import com.mojang.logging.LogUtils;
import me.srrapero720.watermedia.api.media.compat.CompatVideoUrl;
import me.srrapero720.watermedia.lavaplayer.LPManager;
import me.srrapero720.watermedia.vlc.VLCManager;
import org.slf4j.Logger;

import java.nio.file.Path;

@SuppressWarnings("BooleanMethodIsAlwaysInverted")
public class WaterMedia {
	public static final String MOD_ID = "watermedia";
	public static final Logger LOGGER = LogUtils.getLogger();

	public static boolean load(Path gameDir, boolean inDev) {
		// PRE-API LOADERS
		if (!CompatVideoUrl.init()) return false;

		// BINARIES LOADERS
		if (!VLCManager.init(gameDir, inDev)) return false;
        if (!LPManager.init()) return false;

		// POST-API LOADERS
		return true;
	}

	public static boolean load(Path gameDir, MediaConfig config, boolean inDev) {
		throw new UnsupportedOperationException("This method isn't supported yet");
	}
}