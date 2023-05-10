package me.srrapero720.watermedia.vlc;

import com.mojang.logging.LogUtils;
import me.srrapero720.watermedia.vlc.strategy.LinuxNativeFixed;
import me.srrapero720.watermedia.vlc.strategy.MacOsNativeFixed;
import me.srrapero720.watermedia.vlc.strategy.WindowsNativeFixed;
import me.srrapero720.watermedia.watercore_supplier.ThreadUtil;
import org.slf4j.Logger;
import uk.co.caprica.vlcj.factory.MediaPlayerFactory;
import uk.co.caprica.vlcj.factory.discovery.NativeDiscovery;

import java.lang.reflect.Field;

public class VLCDiscovery extends Thread {
    public enum Semaphore { FAILED, UNLOADED, LOADING, READY }
    private static final Logger LOGGER = LogUtils.getLogger();
    private static volatile Semaphore state = Semaphore.UNLOADED;
    private static volatile NativeDiscovery discovery;
    public static volatile MediaPlayerFactory factory;

    // MISSING
    private static Field searchPaths;
    private static Field libraries;

    public static void setState(Semaphore state) { VLCDiscovery.state = state; }
    public static boolean isLoading() { return state.equals(Semaphore.LOADING); }
    public static boolean isReady() { return state.equals(Semaphore.READY); }

    public static boolean isReadyOrRequest() {
        if (isReady()) return true;
        if (!isLoading()) {
            setState(Semaphore.LOADING);
            new VLCDiscovery();
        }
        return false;
    }

    public VLCDiscovery() {
        this.setName("WMDiscover-VLC");
        this.setDaemon(true);
        this.start();
    }

    @Override
    public synchronized void run() {
        super.run();
        if (isReady()) return;

        ThreadUtil.trySimple(() -> {
            discovery = new BetterNativeDiscovery(new WindowsNativeFixed(), new MacOsNativeFixed(), new LinuxNativeFixed());
            if (discovery.discover()) {
                setState(Semaphore.READY);
                factory = new MediaPlayerFactory("--quiet");
                Runtime.getRuntime().addShutdownHook(new Thread(() -> factory.release()));
                LOGGER.info("VLC Loaded on path '{}'", discovery.discoveredPath());
            } else LOGGER.info("Failed to load VLC");

        }, (e) -> {
            setState(Semaphore.FAILED);
            LOGGER.error("VLC failed to load", e);
        });
    }
}