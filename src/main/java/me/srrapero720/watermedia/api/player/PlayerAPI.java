package me.srrapero720.watermedia.api.player;

import me.lib720.watermod.safety.TryCore;
import me.srrapero720.watermedia.OperativeSystem;
import me.srrapero720.watermedia.WaterMedia;
import me.srrapero720.watermedia.api.WaterMediaAPI;
import me.srrapero720.watermedia.api.player.vlc.SimplePlayer;
import me.srrapero720.watermedia.loaders.ILoader;
import me.srrapero720.watermedia.tools.IOTool;
import me.srrapero720.watermedia.tools.JarTool;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import uk.co.caprica.vlcj.VideoLan4J;
import uk.co.caprica.vlcj.factory.MediaPlayerFactory;
import uk.co.caprica.vlcj.factory.discovery.NativeDiscovery;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Date;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

import static me.srrapero720.watermedia.WaterMedia.LOGGER;

public class PlayerAPI extends WaterMediaAPI {
    private static final NativeDiscovery DISCOVERY = new NativeDiscovery();
    private static final Marker IT = MarkerManager.getMarker(PlayerAPI.class.getSimpleName());
    private static final String VIDEOLAN_CFG_NAME = "version.cfg";
    private static final String VIDEOLAN_BIN_ASSET = "videolan/" + OperativeSystem.getFile();
    private static final String VIDEOLAN_VER_ASSET = "/videolan/" + VIDEOLAN_CFG_NAME;

    private static final Map<String, String> ARGVARS = new HashMap<>();

    private static MediaPlayerFactory FACTORY_VMEM_AWAVEOUT;
    private static MediaPlayerFactory FACTORY_VMEM_ADIRECTSOUND;
    private static MediaPlayerFactory FACTORY_VMEM_AMEM;

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
    public static MediaPlayerFactory getFactoryDirectsound() {
        return FACTORY_VMEM_ADIRECTSOUND;
    }

    /**
     * Returns default WATERMeDIA's MediaPlayer factory instance with audio output variant
     * uses WaveOut by default, this has an issue and is audio output always being bound to process volume.
     * So if you need, for example, use individual players with individual volume, this is not the right option
     * by default uses on video output "mem"
     * @return default factory
     */
    public static MediaPlayerFactory getFactoryWaveout() {
        return FACTORY_VMEM_AWAVEOUT;
    }

    /**
     * Returns default WATERMeDIA's MediaPlayer factory instance with audio output variant
     * uses output on mem by default, you need to handle audio output by your own adding a callback for it
     * by default uses on video output "mem"
     * @see uk.co.caprica.vlcj.player.base.AudioApi AudioAPI from VLCJ
     * @return default factory
     */
    public static MediaPlayerFactory getFactoryMem() {
        return FACTORY_VMEM_AMEM;
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
        this.configOutputFile = zipOutputFile.toPath().getParent().resolve(VIDEOLAN_CFG_NAME).toFile();
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
            LOGGER.warn(IT, "VLC binaries extraction skipped, {}", !wrapped ? "binaries are not wrapped" : "extracted binaries version match");

        return true;
    }

    @Override
    public void start(ILoader bootCore) throws Exception {
        if (extract) {
            LOGGER.info(IT, "Extracting VideoLAN binaries...");
            if ((!zipOutputFile.exists() && JarTool.copyAsset(VIDEOLAN_BIN_ASSET, zipOutputFile.toPath())) || zipOutputFile.exists()) {
                IOTool.unzip(IT, zipOutputFile.toPath());
                zipOutputFile.deleteOnExit();

                TryCore.simple(() -> JarTool.copyAsset(VIDEOLAN_VER_ASSET, configOutputFile.toPath()), LOGGER::error);

                LOGGER.info(IT, "VideoLAN binaries extracted successfully");
            }
        }

        // LOGGER INIT
        LOGGER.info(IT, "Processing VideoLAN log files...");
        if (!Files.exists(logs)) {
            if (logs.getParent().toFile().mkdirs()) LOGGER.info(IT, "Logger dir created");
            else LOGGER.error(IT, "Failed to create logger dir");
        } else {
            File logFile = logs.toFile();
            if (!logFile.exists() || !logFile.isFile()) return;

            // Output for new gZIP
            String date = new Date(System.currentTimeMillis()).toLocalDate().toString();
            String compressedFilePath = logFile.getParent() + "/" + date + ".log.gz";

            int count = 0;
            while (new File(compressedFilePath).exists()) compressedFilePath = logFile.getParent() + "/" + date + "-" + (++count) + ".log.gz";

            try (GZIPOutputStream gzipOutputStream = new GZIPOutputStream(Files.newOutputStream(Paths.get(compressedFilePath))); InputStream inputStream = new FileInputStream(logFile)) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) gzipOutputStream.write(buffer, 0, bytesRead);
            } catch (Exception e) {
                LOGGER.error(IT, "Failed to compress log file from '{}'", logs, e);
            }

            if (!logFile.delete()) LOGGER.error(IT, "Cannot delete log file");
        }

        // VLCJ INIT
        VideoLan4J.init(dir.toAbsolutePath().resolve("videolan/"));

        // VLC INIT, this need to be soft-crashed because api and game can still work without VLC
        try {
            if (ARGVARS.isEmpty()) {
                ARGVARS.put("logfile", logs.toString());
            }

            String[] args = JarTool.readArrayAndParse("/videolan/args-vmem-adirect.json", ARGVARS);
            FACTORY_VMEM_ADIRECTSOUND = customFactory(args);

            args = JarTool.readArrayAndParse("/videolan/args-vmem-amem.json", ARGVARS);
            FACTORY_VMEM_AMEM = customFactory(args);

            args = JarTool.readArrayAndParse("/videolan/args-vmem-awaveout.json", ARGVARS);
            FACTORY_VMEM_AWAVEOUT = customFactory(args);
        } catch (Exception e) {
            LOGGER.error(IT, "Failed to load VLC", e);
        }
    }

    @Override
    public void release() {
        FACTORY_VMEM_ADIRECTSOUND.release();
        FACTORY_VMEM_AMEM.release();
        FACTORY_VMEM_AWAVEOUT.release();
    }
}