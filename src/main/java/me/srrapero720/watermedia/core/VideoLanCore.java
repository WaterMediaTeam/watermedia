package me.srrapero720.watermedia.core;

import me.lib720.caprica.vlcj.VideoLan4J;
import me.lib720.caprica.vlcj.factory.MediaPlayerFactory;
import me.srrapero720.watermedia.api.WaterMediaAPI;
import me.srrapero720.watermedia.api.loader.IMediaLoader;
import me.srrapero720.watermedia.core.tools.JarTool;
import me.srrapero720.watermedia.core.tools.exceptions.ReInitException;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Date;
import java.util.zip.GZIPOutputStream;

import static me.srrapero720.watermedia.WaterMedia.LOGGER;

public class VideoLanCore {
    public static final Marker IT = MarkerManager.getMarker(VideoLanCore.class.getSimpleName());
    private static MediaPlayerFactory FACTORY;
    public static MediaPlayerFactory factory() { return FACTORY; }

    public static void init(IMediaLoader loader) throws Exception {
        if (FACTORY != null) throw new ReInitException(VideoLanCore.class.getSimpleName());

        // SETUP PATHS
        Path dir = loader.tmpPath();
        Path logs = dir.toAbsolutePath().resolve("logs/videolan.log");

        // LOGGER INIT
        if (!Files.exists(logs)) {
            if (logs.getParent().toFile().mkdirs()) LOGGER.info(IT, "Logger dir created");
            else LOGGER.error(IT, "Failed to create logger dir");
        } else init$clearLogsFile(logs);

        // VLCJ INIT
        VideoLan4J.init(dir.toAbsolutePath().resolve("videolan/"));

        try {
            FACTORY = WaterMediaAPI.vlc_createFactory(init$readArguments(loader, logs));
        } catch (Exception e) {
            LOGGER.error(IT, "Failed to load VLC", e);
        }
    }

    private static String[] init$readArguments(IMediaLoader loader, Path loggerPath) {
        String[] args = JarTool.readStringList(loader.classLoader(), "/videolan/arguments.json").toArray(new String[0]);
        args[2] = loggerPath.toString();
        return  args;
    }

    private static void init$clearLogsFile(Path logFilePath) {
        File logFile = logFilePath.toFile();
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
            LOGGER.error(IT, "Failed to compress {}", logFilePath, e);
        }

        if (!logFile.delete()) LOGGER.error(IT, "Cannot delete logfile");
    }
}