package me.srrapero720.watermedia.vlc;


import me.lib720.caprica.vlcj4.factory.MediaPlayerFactory;
import me.lib720.caprica.vlcj4.factory.discovery.NativeDiscovery;
import me.lib720.caprica.vlcj4.factory.discovery.strategy.LinuxNativeDiscoveryStrategy;
import me.lib720.caprica.vlcj4.factory.discovery.strategy.OsxNativeDiscoveryStrategy;
import me.lib720.caprica.vlcj4.factory.discovery.strategy.WindowsNativeDiscoveryStrategy;
import me.srrapero720.watermedia.internal.util.ThreadUtil;
import me.srrapero720.watermedia.internal.util.WaterUtil;

import java.nio.file.Path;
import java.util.Arrays;

import static me.srrapero720.watermedia.WaterMedia.LOGGER;

public class VLCManager {
    // Comes from: https://artifacts.videolan.org/vlc-3.0/nightly-win64/
    public static final String version = "20230608-0224";

    private static boolean forceFirstLocal;
    private static Path rootPath;

    private static MediaPlayerFactory defaultFactory;

    public static boolean forceFirstLocal() { return forceFirstLocal; }
    public static Path getRootPath() { return rootPath; }
    public static boolean isLoaded() { return defaultFactory != null; }
    public static MediaPlayerFactory getDefaultFactory() { return defaultFactory; }

    public static boolean init(Path gameDir, boolean forceLocal) {
        rootPath = gameDir;
        forceFirstLocal = forceLocal;
        if (isLoaded()) return true;

        defaultFactory = ThreadUtil.tryAndReturn(
                defaultVar -> createVLCPlayerFactory(),
                e -> LOGGER.error("Something failed while loading VLC binaries", e),
                null
        );

        return isLoaded();
    }

    public static MediaPlayerFactory createVLCPlayerFactory() { return createVLCPlayerFactory(WaterUtil.getArrayStringFromRes("vlc/command-line.json")); }
    public static MediaPlayerFactory createVLCPlayerFactory(String[] vlcArgs) {
        var discovery = new NativeDiscovery(new WindowsNativeDiscoveryStrategy(), new OsxNativeDiscoveryStrategy(), new LinuxNativeDiscoveryStrategy());
        if (discovery.discover()) {
            var factory = new MediaPlayerFactory(discovery, vlcArgs);
            Runtime.getRuntime().addShutdownHook(new VLCReleaseHook(factory));
            LOGGER.info("New instance of VLC loaded from '{}'", discovery.discoveredPath());
            LOGGER.info("VLC loaded with args '{}'", Arrays.toString(vlcArgs));
            return factory;
        }

        LOGGER.error("VLC was not found on your system.");
        return null;
    }

    private static class VLCReleaseHook extends Thread {
        public final MediaPlayerFactory factory;
        VLCReleaseHook(MediaPlayerFactory factory) {
            super();
            this.setDaemon(true);
            this.setName("VLC-ReleaseHook");
            this.factory = factory;
        }

        @Override
        public void run() {
            LOGGER.info("Shutdown VLC");
            if (factory != null) factory.release();
            LOGGER.info("Shutdown finished");
        }
    }
}