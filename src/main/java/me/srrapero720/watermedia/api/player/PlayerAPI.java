package me.srrapero720.watermedia.api.player;

import me.lib720.caprica.vlcj.VideoLan4J;
import me.lib720.caprica.vlcj.factory.MediaPlayerFactory;
import me.lib720.caprica.vlcj.factory.discovery.NativeDiscovery;
import me.srrapero720.watermedia.OperativeSystem;
import me.srrapero720.watermedia.WaterMedia;
import me.srrapero720.watermedia.api.WaterMediaAPI;
import me.srrapero720.watermedia.api.player.vlc.SimplePlayer;
import me.srrapero720.watermedia.core.tools.IOTool;
import me.srrapero720.watermedia.core.tools.JarTool;
import me.srrapero720.watermedia.loaders.ILoader;
import org.apache.commons.io.file.PathUtils;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static me.srrapero720.watermedia.WaterMedia.LOGGER;

public class PlayerAPI extends WaterMediaAPI {
    private static final NativeDiscovery DISCOVERY = new NativeDiscovery();
    private static final Marker IT = MarkerManager.getMarker(PlayerAPI.class.getSimpleName());
    private static final String VIDEOLAN_BIN_ASSET = "videolan/" + OperativeSystem.getFile();
    private static final String VIDEOLAN_VER_ASSET = "videolan/version.cfg";

    private static final Map<String, String> ARGVARS = new HashMap<>();

    private static MediaPlayerFactory DEFAULT_FACTORY;

    /**
     * Check if PlayerAPI and/or VLC is loaded and ready to be used.
     * Some modules cannot be loaded in some OS, in that case WATERMeDIA can address it and
     * keep still working
     * @return if PlayerAPI and/or VLC was loaded
     */
    public static boolean isReady() {
        return NativeDiscovery.alreadyFound;
    }

    /**
     * Returns default WATERMeDIA's MediaPlayer factory instance with audio output variant
     * uses DirectSound by default, witch provides an individual volume for each player
     * by default uses on video output "mem"
     * @return default factory
     */
    public static MediaPlayerFactory getFactory() {
        return DEFAULT_FACTORY;
    }

    /**
     * Use your own VLCArgs at your own risk
     * By default this method makes a ReleaseHook to release factory on process shutdown
     * Suggestion: Use the same VLC arguments for logging but with another filename
     * Example: <pre> "--logfile", "logs/vlc/mymod-latest.log",</pre>
     * check <a href="https://wiki.videolan.org/VLC_command-line_help/">VideoLAN wiki</a>
     * @param vlcArgs arguments to make another VLC instance
     * @return a PlayerFactory to create custom VLC players. {@link SimplePlayer} can accept factory for new instances
     */
    public static MediaPlayerFactory customFactory(String[] vlcArgs) {
        MediaPlayerFactory factory = null;
        if (DISCOVERY.discover()) {
            factory = new MediaPlayerFactory(DISCOVERY, vlcArgs);
            Runtime.getRuntime().addShutdownHook(new Thread(factory::release));
            LOGGER.info(IT, "Created new VLC instance from '{}' with args: '{}'", DISCOVERY.discoveredPath(), Arrays.toString(vlcArgs));
        } else {
            LOGGER.fatal(IT, "Missing VLC - Cannot create MediaPlayerFactory instance");
        }

        return factory;
    }

    // LOADING
    private final Path dir;
    private final Path logs;

    private final File zipOutputFile;
    private final File configOutputFile;

    private boolean extract;
    public PlayerAPI() {
        super();
        ILoader bootstrap = WaterMedia.getLoader();
        this.dir = bootstrap.tempDir();
        this.logs = dir.toAbsolutePath().resolve("logs/videolan.log");

        this.zipOutputFile = bootstrap.tempDir().resolve(VIDEOLAN_BIN_ASSET).toFile();
        this.configOutputFile = bootstrap.tempDir().resolve(VIDEOLAN_VER_ASSET).toFile();
    }

    @Override
    public Priority priority() {
        return Priority.HIGH;
    }

    @Override
    public boolean prepare(ILoader bootCore) throws Exception {
        String versionInJar = JarTool.readString(VIDEOLAN_VER_ASSET);
        String versionInFile = IOTool.readString(configOutputFile.toPath());
        boolean wrapped = OperativeSystem.isWrapped();
        boolean versionMatch = versionInFile != null && versionInFile.equalsIgnoreCase(versionInJar);
        extract = wrapped && !versionMatch;

        if (!extract)
            LOGGER.warn(IT, "VLC binaries extraction skipped, {}", !wrapped ? "binaries for '" + OperativeSystem.OS + "' are not wrapped" : "extracted binaries version '" + versionInFile + "' match");

        return true;
    }

    @Override
    public void start(ILoader bootCore) throws Exception {
        if (extract) {
            LOGGER.info(IT, "Extracting VideoLAN binaries...");
            if ((!zipOutputFile.exists() && JarTool.copyAsset(VIDEOLAN_BIN_ASSET, zipOutputFile.toPath())) || zipOutputFile.exists()) {
                IOTool.unzip(IT, zipOutputFile.toPath());
                zipOutputFile.deleteOnExit();

                JarTool.copyAsset(VIDEOLAN_VER_ASSET, configOutputFile.toPath());

                LOGGER.info(IT, "VideoLAN binaries extracted successfully");
            }
        }

        // LOGGER INIT
        LOGGER.info(IT, "Processing VideoLAN log files...");
        if (Files.exists(logs)) {
            Path parent = logs.getParent();
            try {
                PathUtils.deleteDirectory(parent);
            } catch (IOException e) {
                LOGGER.warn(IT, "Failed to delete VLC logs directory", e);
            }
        }

        // VLCJ INIT
        VideoLan4J.init(dir.toAbsolutePath().resolve("videolan/"));

        // VLC INIT, this need to be soft-crashed because api and game can still work without VLC
        try {
            String[] args = JarTool.readArrayAndParse("/videolan/arguments.json", ARGVARS);
            DEFAULT_FACTORY = customFactory(args);
        } catch (Exception e) {
            LOGGER.error(IT, "Failed to load VLC", e);
        }
    }

    @Override
    public void release() {
        DEFAULT_FACTORY.release();
    }
}