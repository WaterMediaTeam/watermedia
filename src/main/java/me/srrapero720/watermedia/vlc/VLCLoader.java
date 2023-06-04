package me.srrapero720.watermedia.vlc;

import me.srrapero720.watermedia.vlc.strategy.provider.LocalFileProvider;
import me.srrapero720.watermedia.vlc.strategy.LinuxNativeFixed;
import me.srrapero720.watermedia.vlc.strategy.MacOsNativeFixed;
import me.srrapero720.watermedia.vlc.strategy.WindowsNativeFixed;
import me.srrapero720.watermedia.watercore_supplier.ThreadUtil;
import me.lib720.caprica.vlcj.factory.MediaPlayerFactory;
import me.lib720.caprica.vlcj.factory.discovery.NativeDiscovery;

import java.nio.file.Path;

import static me.srrapero720.watermedia.WaterMedia.LOGGER;

public class VLCLoader {
    private static VLCState state = VLCState.UNLOADED;
    private static MediaPlayerFactory FACTORY;
    private static Path GAME_DIR;
    private static boolean DEV_MODE;

    // SEMAPHORE
    public static VLCState getLightState() { return state; }
    private static void setLightState(VLCState state) { VLCLoader.state = state; }

    public static boolean load(Path gameDir, boolean devMode) {
        GAME_DIR = gameDir;
        DEV_MODE = devMode;
        return load();
    }

    public static MediaPlayerFactory getFactory() { return FACTORY; }
    public static boolean isDevMode() { return DEV_MODE; }
    public static LocalFileProvider getLocalFileProvider() { return new LocalFileProvider(GAME_DIR); }

    // Comes from: https://artifacts.videolan.org/vlc/nightly-win64-llvm/
    public static String getVersion() { return "20230604"; }

    public static boolean load() {
        if (state.equals(VLCState.READY)) return true;

        return ThreadUtil.tryAndReturn((defaultVar) -> {
            var discovery = new NativeDiscovery(new WindowsNativeFixed(), new MacOsNativeFixed(), new LinuxNativeFixed());

            if (discovery.discover()) {
                FACTORY = new MediaPlayerFactory(discovery, "--aout", "directsound", "--file-caching", "6000", "--file-logging", "--logfile", "logs/vlc/lastest.log", "--logmode", "text", "--verbose", "2", "--no-quiet");

                Runtime.getRuntime().addShutdownHook(new VLCShutdown());
                LOGGER.info("VLC Loaded on path '{}'", discovery.discoveredPath());

                setLightState(VLCState.READY);
                return true;
            } else LOGGER.info("Failed to load VLC");
            return defaultVar;
        }, (e) -> {
            setLightState(VLCState.FAILED);
            LOGGER.error("VLC failed to load", e);
        }, false);
    }
}