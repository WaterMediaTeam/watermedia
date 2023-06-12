package me.srrapero720.watermedia.vlc;


import me.lib720.caprica.vlcj4.factory.MediaPlayerFactory;
import me.lib720.caprica.vlcj4.factory.discovery.NativeDiscovery;
import me.lib720.caprica.vlcj4.factory.discovery.strategy.LinuxNativeDiscoveryStrategy;
import me.lib720.caprica.vlcj4.factory.discovery.strategy.OsxNativeDiscoveryStrategy;
import me.lib720.caprica.vlcj4.factory.discovery.strategy.WindowsNativeDiscoveryStrategy;
import me.srrapero720.watermedia.internal.util.WaterUtil;

import java.nio.file.Path;
import java.util.Arrays;

import static me.srrapero720.watermedia.WaterMedia.LOGGER;

public class VLCManager {
    private static final String version = "20230608";

    private static boolean firstSystem;
    private static Path rootPath;
    private static MediaPlayerFactory defaultFactory;

    // Comes from: https://artifacts.videolan.org/vlc-3.0/nightly-win64/
    public static String getVersion() { return version; }

    public static boolean isFirstSystem() { return firstSystem; }
    public static Path getRootPath() { return rootPath; }
    public static boolean isLoaded() { return defaultFactory != null; }
    public static MediaPlayerFactory getDefaultFactory() { return defaultFactory; }

    public static boolean init(Path gameDir, boolean checkFirstSystem) {
        rootPath = gameDir;
        firstSystem = checkFirstSystem;
        if (isLoaded()) return true;

        try {
            defaultFactory = createVLCPlayerFactory();
        } catch (Exception e) {
            LOGGER.error("Something failed while loading VLC binaries", e);
            defaultFactory = null;
        }

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