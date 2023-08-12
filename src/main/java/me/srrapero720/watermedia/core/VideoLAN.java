package me.srrapero720.watermedia.core;


import me.lib720.caprica.vlcj.factory.MediaPlayerFactory;
import me.lib720.caprica.vlcj.factory.discovery.provider.CustomDirectoryProvider;
import me.lib720.watermod.ThreadCore;
import me.srrapero720.watermedia.api.loader.IMediaLoader;
import me.srrapero720.watermedia.api.WaterMediaAPI;
import me.srrapero720.watermedia.core.tools.exceptions.ReloadingException;
import me.srrapero720.watermedia.core.tools.JarTool;
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

public class VideoLAN {
    public static final Marker IT = MarkerFactory.getMarker(VideoLAN.class.getSimpleName());
    private static MediaPlayerFactory FACTORY;
    public static MediaPlayerFactory getFactory() { return FACTORY; }

    public static void init(IMediaLoader loader) throws Exception {
        Path dir = loader.getTmpDirectory();
        if (FACTORY != null) throw new ReloadingException(VideoLAN.class.getSimpleName());

        // SETUP PATHS
        Path logs = dir.toAbsolutePath().resolve("logs/videolan.log");

        // LOGGER INIT
        if (!Files.exists(logs.toAbsolutePath())) {
            if (logs.getParent().toFile().mkdirs()) LOGGER.info(IT, "Logger dir created");
            else compressAndDeleteLogFile(logs);
        }

        // VLCJ INIT
        CustomDirectoryProvider.init(dir.toAbsolutePath().resolve("videolan/"));

        FACTORY = ThreadCore.tryAndReturnNull(defaultVar -> {
            String[] args = JarTool.readStringList(loader.getModuleClassLoader(), "/videolan/arguments.json").toArray(new String[0]);
            args[2] = logs.toAbsolutePath().toString();

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