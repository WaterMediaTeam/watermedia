package me.srrapero720.watermedia.api.player;

import me.srrapero720.watermedia.WaterMedia;
import me.srrapero720.watermedia.loaders.IBootCore;
import me.srrapero720.watermedia.api.WaterMediaAPI;
import me.srrapero720.watermedia.tools.JarTool;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import uk.co.caprica.vlcj.VideoLan4J;
import uk.co.caprica.vlcj.factory.MediaPlayerFactory;
import uk.co.caprica.vlcj.factory.discovery.NativeDiscovery;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Date;
import java.util.Arrays;
import java.util.zip.GZIPOutputStream;

import static me.srrapero720.watermedia.WaterMedia.LOGGER;

public class PlayerAPI extends WaterMediaAPI {
    private static final NativeDiscovery DISCOVERY = new NativeDiscovery();
    private static final Marker IT = MarkerManager.getMarker(PlayerAPI.class.getSimpleName());
    private static MediaPlayerFactory FACTORY;

    private static MediaPlayerFactory FACTORY_VMEM_AWAVEOUT;
    private static MediaPlayerFactory FACTORY_VMEM_ADIRECTSOUND;
    private static MediaPlayerFactory FACTORY_VMEM_AMEM;

    public static MediaPlayerFactory factory() { return FACTORY; }

    /**
     * Check if PlayerAPI and/or VLC is loaded and ready to be used.
     * Some modules cannot be loaded in some OS, in that case WATERMeDIA can address it and
     * keep still working
     * @return if PlayerAPI and/or VLC was loaded
     */
    public static boolean isReady() { return factory() != null; }

    /**
     * Gives you the default VLC MediaPlayerFactory created by API
     * @return WATERMeDIA's default MediaPlayerFactory
     */
    public static MediaPlayerFactory getVLCFactory() { return factory(); }

    private static String[] init$readArguments(Path loggerPath) throws IOException {
        String[] args = JarTool.readArray("/videolan/arguments.json");
        for (int i =0; i < args.length; i++) args[i] = args[i].replace("{logfile}", loggerPath.toString());
        return  args;
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

    private final Path dir;
    private final Path logs;
    public PlayerAPI() {
        super();
        IBootCore bootstrap = WaterMedia.getInstance().getBootCore();
        this.dir = bootstrap.tempDir();
        this.logs = dir.toAbsolutePath().resolve("logs/videolan.log");
    }

    @Override
    public Priority priority() {
        return Priority.HIGH;
    }

    @Override
    public boolean prepare() {
        return FACTORY == null;
    }

    @Override
    public void start() throws Exception {
        // LOGGER INIT
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

            try (GZIPOutputStream gzipOutputStream = new GZIPOutputStream(new FileOutputStream(compressedFilePath)); InputStream inputStream = new FileInputStream(logFile)) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) gzipOutputStream.write(buffer, 0, bytesRead);
            } catch (Exception e) {
                LOGGER.error(IT, "Failed to compress {}", logs, e);
            }

            if (!logFile.delete()) LOGGER.error(IT, "Cannot delete logfile");
        }

        // VLCJ INIT
        VideoLan4J.init(dir.toAbsolutePath().resolve("videolan/"));

        try {
            FACTORY = init$createFactory(init$readArguments(logs));
        } catch (Exception e) {
            LOGGER.error(IT, "Failed to load VLC", e);
        }
    }

    @Override
    public void release() {
        throw new NoSuchMethodError("No executed");
    }
}