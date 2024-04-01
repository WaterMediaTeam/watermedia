package me.srrapero720.watermedia.core;

import me.lib720.caprica.vlcj.VideoLan4J;
import me.lib720.caprica.vlcj.factory.MediaPlayerFactory;
import me.lib720.caprica.vlcj.factory.discovery.NativeDiscovery;
import me.srrapero720.watermedia.api.loader.IMediaLoader;
import me.srrapero720.watermedia.api.player.SyncBasePlayer;
import me.srrapero720.watermedia.core.tools.JarTool;
import me.srrapero720.watermedia.core.tools.exceptions.ReInitException;
import org.apache.commons.io.file.PathUtils;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import static me.srrapero720.watermedia.WaterMedia.LOGGER;

public class VideoLanCore {
    public static final Marker IT = MarkerManager.getMarker(VideoLanCore.class.getSimpleName());
    private static MediaPlayerFactory FACTORY;
    private static final NativeDiscovery DISCOVERY = new NativeDiscovery();
    public static MediaPlayerFactory factory() { return FACTORY; }

    public static void init(IMediaLoader loader) throws Exception {
        if (FACTORY != null) throw new ReInitException(VideoLanCore.class.getSimpleName());

        // SETUP PATHS
        Path dir = loader.tmpPath();
        Path logs = dir.toAbsolutePath().resolve("logs/videolan.log");

        // LOGGER INIT
        if (Files.exists(logs)) {
            init$clearLogsFile(logs);
        }

        // VLCJ INIT
        VideoLan4J.init(dir.toAbsolutePath().resolve("videolan/"));

        try {
            FACTORY = init$createFactory(init$readArguments());
        } catch (Exception e) {
            LOGGER.error(IT, "Failed to load VLC", e);
        }
    }

    private static String[] init$readArguments() {
        return JarTool.readStringList(VideoLanCore.class.getClassLoader(), "/videolan/arguments.json").toArray(new String[0]);
    }

    private static void init$clearLogsFile(Path logFilePath) {
        Path parent = logFilePath.getParent();
        try {
            PathUtils.deleteDirectory(parent);
        } catch (IOException e) {
            LOGGER.warn(IT, "Failed to delete VLC logs directory", e);
        }
    }

    /**
     * Use your own VLCArgs at your own risk
     * By default this method makes a ReleaseHook to release everything after close Minecraft
     * Suggestion: Use the same VLC arguments for logging but with other filename
     * Example: <pre> "--logfile", "logs/vlc/mymod-latest.log",</pre>
     * @param vlcArgs arguments to make another VLC instance
     * @return a PlayerFactory to create custom VLC players. {@link SyncBasePlayer} can accept factory for new instances
     */
    public static MediaPlayerFactory init$createFactory(String[] vlcArgs) {
        if (DISCOVERY.discover()) {
            MediaPlayerFactory factory = new MediaPlayerFactory(DISCOVERY, vlcArgs);
            LOGGER.info(IT, "Created new VLC instance from '{}' with args: '{}'", DISCOVERY.discoveredPath(), Arrays.toString(vlcArgs));
            Runtime.getRuntime().addShutdownHook(new Thread(factory::release));
            return factory;
        } else {
            LOGGER.error(IT, "VLC was not found on your system");
        }

        LOGGER.fatal(IT, "Cannot create MediaPlayerFactory instance");
        return null;
    }
}