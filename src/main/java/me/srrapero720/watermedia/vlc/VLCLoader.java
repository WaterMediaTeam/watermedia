package me.srrapero720.watermedia.vlc;

import me.srrapero720.watermedia.WaterMedia;
import me.srrapero720.watermedia.vlc.provider.LocalFileProvider;
import me.srrapero720.watermedia.vlc.strategy.DirsDiscoveryFixed;
import me.srrapero720.watermedia.vlc.strategy.LinuxNativeFixed;
import me.srrapero720.watermedia.vlc.strategy.MacOsNativeFixed;
import me.srrapero720.watermedia.vlc.strategy.WindowsNativeFixed;
import me.srrapero720.watermedia.watercore_supplier.ThreadUtil;
import uk.co.caprica.vlcj.factory.MediaPlayerFactory;
import uk.co.caprica.vlcj.factory.discovery.NativeDiscovery;

import java.nio.file.Path;

import static me.srrapero720.watermedia.WaterMedia.LOGGER;

public class VLCLoader {
    public enum Semaphore { FAILED, UNLOADED, LOADING, READY }
    private static Semaphore state = Semaphore.UNLOADED;
    private static NativeDiscovery discovery;
    public static MediaPlayerFactory factory;

    // LOCAL FILE (esto no deberia estar aqui, pero no se como enviar el DIR de forma no tan agresiva.
    public static LocalFileProvider LFP;

    // SEMAPHORE
    public static Semaphore getLightState() { return state; }
    private static void onLightUpdate(Semaphore state) { VLCLoader.state = state; }

    public static boolean load(Path gameDir, boolean devMode) {
        LFP = new LocalFileProvider(gameDir);
        DirsDiscoveryFixed.devSort = devMode;
        return load();
    }

    public static boolean load() {
        if (state.equals(Semaphore.READY)) return true;

        return ThreadUtil.tryAndReturn((defaultVar) -> {
            discovery = new NativeDiscovery(new WindowsNativeFixed(), new MacOsNativeFixed(), new LinuxNativeFixed());
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