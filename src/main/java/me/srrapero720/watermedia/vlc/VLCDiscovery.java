package me.srrapero720.watermedia.vlc;

import com.mojang.logging.LogUtils;
import me.srrapero720.watermedia.vlc.strategy.LinuxNativeFixed;
import me.srrapero720.watermedia.vlc.strategy.MacOsNativeFixed;
import me.srrapero720.watermedia.vlc.strategy.WindowsNativeFixed;
import org.slf4j.Logger;
import uk.co.caprica.vlcj.factory.MediaPlayerFactory;
import uk.co.caprica.vlcj.factory.discovery.NativeDiscovery;

import java.lang.reflect.Field;

public class VLCDiscovery {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static volatile boolean loaded = false;
    private static volatile boolean startedLoading = false;
    private static volatile boolean successful = false;
    private static volatile NativeDiscovery discovery;
    public static volatile MediaPlayerFactory factory;
    private static Field searchPaths;
    private static Field libraries;
    
    public static boolean isLoaded() {
        return loaded;
    }
    
    public static boolean isLoadedOrRequest() {
        if (loaded)
            return true;
        if (!startedLoading) {
            startedLoading = true;
            new Thread(() -> load()).start();
        }
        return false;
    }
    
    public static boolean isAvailable() {
        return successful;
    }
    
    public static synchronized boolean load() {
        if (loaded) return successful;

        try {
            WindowsNativeFixed windows = new WindowsNativeFixed();
            discovery = new BetterNativeDiscovery(new LinuxNativeFixed(), new MacOsNativeFixed(), windows);
            successful = discovery.discover();
            loaded = true;
            if (successful) {
                factory = new MediaPlayerFactory("--quiet");
                LOGGER.info("Loaded VLC in '{}'", discovery.discoveredPath());
                Runtime.getRuntime().addShutdownHook(new Thread(() -> factory.release()));
            } else
                LOGGER.info("Failed to load VLC");
        } catch (Exception e) {
            e.printStackTrace();
            loaded = true;
            successful = false;
            LOGGER.error("Failed to load VLC");
        }
        return successful;
    }
    
}