package me.srrapero720.watermedia.vlc;


import me.lib720.caprica.vlcj4.factory.MediaPlayerFactory;
import me.lib720.caprica.vlcj4.factory.discovery.NativeDiscovery;
import me.lib720.caprica.vlcj4.factory.discovery.strategy.LinuxNativeDiscoveryStrategy;
import me.lib720.caprica.vlcj4.factory.discovery.strategy.OsxNativeDiscoveryStrategy;
import me.lib720.caprica.vlcj4.factory.discovery.strategy.WindowsNativeDiscoveryStrategy;

import java.nio.file.Path;
import java.util.Arrays;

import static me.srrapero720.watermedia.WaterMedia.LOGGER;

public class VLCManager {
    private static final String version = "20230604";

    private static boolean devMode;
    private static Path rootPath;
    private static MediaPlayerFactory defaultFactory;

    // Comes from: https://artifacts.videolan.org/vlc/nightly-win64-llvm/
    public static String getVersion() { return version; }

    public static boolean isDevMode() { return devMode; }
    public static boolean isLoaded() { return defaultFactory != null; }
    public static Path getRootPath() { return rootPath; }
    public static MediaPlayerFactory getDefaultFactory() { return defaultFactory; }

    public static boolean init(Path gameDir, boolean devMode) {
        rootPath = gameDir;
        VLCManager.devMode = devMode;

        if (isLoaded()) return true;

        try {
            defaultFactory = createVLCPlayerFactory();
        } catch (Exception e) {
            LOGGER.error("Something failed while loading VLC binaries", e);
            defaultFactory = null;
        }

        return isLoaded();
    }

    public static MediaPlayerFactory createVLCPlayerFactory() { return createVLCPlayerFactory(DEFAULT_VLC_ARGS); }
    public static MediaPlayerFactory createVLCPlayerFactory(String[] vlcArgs) {
        var discovery = new NativeDiscovery(new WindowsNativeDiscoveryStrategy(), new OsxNativeDiscoveryStrategy(), new LinuxNativeDiscoveryStrategy());
        if (discovery.discover()) {
            var factory = new MediaPlayerFactory(discovery, vlcArgs);
            Runtime.getRuntime().addShutdownHook(new VLCReleaseHook(factory));
            LOGGER.info("New instance of VLC loaded from '{}'", discovery.discoveredPath());
            LOGGER.info("VLC loaded with args '{}'", Arrays.toString(vlcArgs));
            return factory;
        }

        LOGGER.error("VLC was not discovered in your system.");
        return null;
    }

    public static final String[] DEFAULT_VLC_ARGS = new String[] {
            "--file-caching",
            "6000",

            "--file-logging",
            "--logfile",
            "logs/vlc/latest.log",
            "--logmode", "text",
            "--verbose", "2",

            "--no-quiet"
    };
}