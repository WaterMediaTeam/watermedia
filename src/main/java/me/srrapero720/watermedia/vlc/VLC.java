package me.srrapero720.watermedia.vlc;

import com.mojang.logging.LogUtils;
import me.srrapero720.watermedia.vlc.strategy.LinuxNativeFixed;
import me.srrapero720.watermedia.vlc.strategy.MacOsNativeFixed;
import me.srrapero720.watermedia.vlc.strategy.WindowsNativeFixed;
import me.srrapero720.watermedia.watercore_supplier.ThreadUtil;
import org.slf4j.Logger;
import uk.co.caprica.vlcj.factory.MediaPlayerFactory;
import uk.co.caprica.vlcj.factory.discovery.NativeDiscovery;

import java.io.File;

public class VLC {
    public enum Semaphore { FAILED, UNLOADED, LOADING, READY }
    private static final Logger LOGGER = LogUtils.getLogger();
    private static Semaphore state = Semaphore.UNLOADED;
    private static NativeDiscovery discovery;
    public static MediaPlayerFactory factory;

    public static Semaphore getLightState() { return state; }
    private static void onLightUpdate(Semaphore state) { VLC.state = state; }

    public static boolean load(File gameDir) {
        ResourceExtractor.extract(gameDir);
        return load();
    }

    public static boolean load() {
        if (state.equals(Semaphore.READY)) return true;

        return ThreadUtil.tryAndReturn((defaultVar) -> {
            discovery = new BetterNativeDiscovery(new WindowsNativeFixed(), new MacOsNativeFixed(), new LinuxNativeFixed());
            if (discovery.discover()) {
                onLightUpdate(Semaphore.READY);
                factory = new MediaPlayerFactory("--no-metadata-network-access", "--file-logging", "--logfile", "logs/vlc.log", "--logmode", "text", "--verbose", "2", "--no-quiet");
                Runtime.getRuntime().addShutdownHook(new Thread(() -> factory.release()));
                LOGGER.info("VLC Loaded on path '{}'", discovery.discoveredPath());
                return true;
            } else LOGGER.info("Failed to load VLC");
            return defaultVar;
        }, (e) -> {
            onLightUpdate(Semaphore.FAILED);
            LOGGER.error("VLC failed to load", e);
        }, false);
    }
}