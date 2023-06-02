package me.srrapero720.watermedia;

import com.mojang.logging.LogUtils;
import me.srrapero720.watermedia.vlc.VLCLoader;
import me.srrapero720.watermedia.vlc.hooks.VLCShutdown;
import org.slf4j.Logger;

import java.nio.file.Path;


/**
 * Here goes all required methods to load WATERMeDIA
 * This is loader-safe, because each mod-loader is a IWaterMedia.
 */
@SuppressWarnings("BooleanMethodIsAlwaysInverted")
public class WaterMedia {
	public static final String MOD_ID = "watermedia";
	public static final Logger LOGGER = LogUtils.getLogger();

	/**
	 * Loads all the libraries. if someting is wrong throws an error and keeps game in safe-mode
	 * @return Library load state (true if is loaded)
	 */
	public static boolean load(Path gameDir, boolean envDevMode) {
		if (!VLCLoader.load(gameDir, envDevMode)) return false;
//        if (!LavaPlayer.load()) return false;

		// SHUTDOWN HOOKS
		Runtime.getRuntime().addShutdownHook(new VLCShutdown());
		return true;
	}
}