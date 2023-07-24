package me.srrapero720.watermedia.core;


import me.lib720.caprica.vlcj.factory.MediaPlayerFactory;
import me.lib720.caprica.vlcj.factory.discovery.provider.CustomDirectoryProvider;
import me.srrapero720.watermedia.IMediaLoader;
import me.srrapero720.watermedia.api.WaterMediaAPI;
import me.srrapero720.watermedia.util.ThreadUtil;
import me.srrapero720.watermedia.util.ResourceUtil;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Date;
import java.util.zip.GZIPOutputStream;

import static me.srrapero720.watermedia.WaterMedia.LOGGER;

public class VideoLANCore {
    public static final Marker IT = MarkerFactory.getMarker("VideoLAN");
    private static MediaPlayerFactory FACTORY;
    public static MediaPlayerFactory factory() { return FACTORY; }

    public static void init(IMediaLoader loader) {
        Path workingDir = loader.getTempDir();
        if (FACTORY != null) throw new IllegalStateException("Rejected attempt to reload VideoLAN");

        // SETUP PATHS
        Path logs = workingDir.resolve("logs/latest.log");
        Path path = workingDir.resolve("vlc/");

        // LOGGER INIT
        if (!Files.exists(logs.toAbsolutePath())) {
            if (logs.getParent().toFile().mkdirs()) LOGGER.info(IT, "Logger dir created");
            else compressAndDeleteLogFile(logs);
        }

        // INIT
        CustomDirectoryProvider.init(path);

        FACTORY = ThreadUtil.tryAndReturnNull(defaultVar -> {
            String[] args = ResourceUtil.getJsonListFromRes(loader.getClassLoader(), "vlc/args.json").toArray(new String[0]);
            for (int i = 0; i < args.length; i++) {
                args[i] = args[i].replace("%logfile%", logs.toAbsolutePath().toString());
            }

            return WaterMediaAPI.vlc_createFactory(args);
        }, e -> LOGGER.error(IT, "Failed to load VLC", e));
    }


    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static void compressAndDeleteLogFile(Path logFilePath) {
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
        logFile.delete();
    }
}