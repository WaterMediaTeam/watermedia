package me.srrapero720.watermedia.vlc;


import me.lib720.caprica.vlcj.factory.MediaPlayerFactory;
import me.lib720.caprica.vlcj.factory.discovery.provider.CustomDirectoryProvider;
import me.srrapero720.watermedia.api.WaterMediaAPI;
import me.srrapero720.watermedia.api.external.ThreadUtil;
import me.srrapero720.watermedia.MediaUtil;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Date;
import java.time.format.DateTimeFormatterBuilder;
import java.util.zip.GZIPOutputStream;

import static me.srrapero720.watermedia.WaterMedia.LOGGER;

public class VLCManager {
    public static final String version = "20230608-0224b"; // Comes from: https://artifacts.videolan.org/vlc-3.0/nightly-win64/
    private static MediaPlayerFactory defaultFactory;

    public static MediaPlayerFactory getDefaultFactory() {
        return defaultFactory;
    }

    public static boolean init(Path rootPath) {
        if (defaultFactory != null) return true;

        // LOGGER INIT
        var vlcLogs = rootPath.resolve("logs/vlc");
        if (!Files.exists(vlcLogs.toAbsolutePath())) vlcLogs.toFile().mkdirs();
        else compressAndDeleteLogFile(vlcLogs.resolve("latest.log"));

        //INIT
        var vlcPath = rootPath.resolve("cache/vlc/");
        var config = vlcPath.resolve("version.cfg");
        var version = getVersion(config);
        CustomDirectoryProvider.init(vlcPath);

        // Check if we need to update binaries
        if (version == null || !version.equals(VLCManager.version)) {
            // CLEAR
            LOGGER.warn("Running bin deletion from local files");
            for (var binary : BinMappings.values()) binary.delete(rootPath.resolve("cache/vlc"));

            LOGGER.warn("Running VLC LUAC script deletion from local files");
            for (var luac : LuaMappings.values()) luac.delete(rootPath.resolve("cache/vlc"));

            // EXTRACT
            LOGGER.warn("Running bin extraction from JAR to local files");
            for (var binary : BinMappings.values()) binary.extract(rootPath.resolve("cache/vlc"));

            LOGGER.warn("Running VLC LUAC script extraction from JAR to local files");
            for (var luac : LuaMappings.values()) luac.extract(rootPath.resolve("cache/vlc"));

            // SET LOCAL VERSION
            ThreadUtil.trySimple(() -> {
                if (!Files.exists(config.getParent())) Files.createDirectories(config.getParent());
                Files.writeString(config, getVersion());
            }, e -> LOGGER.error("Could not write to configuration file", e));

        } else LOGGER.warn("VLC detected and match with the wrapped version");

        defaultFactory = ThreadUtil.tryAndReturnNull(
                defaultVar -> WaterMediaAPI.newVLCPlayerFactory(MediaUtil.getArrayStringFromRes("vlc/command-line.json")), e -> LOGGER.error("Failed to load VLC", e)
        );

        return defaultFactory != null;
    }


    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void compressAndDeleteLogFile(Path logFilePath) {
        File logFile = logFilePath.toFile();
        if (!logFile.exists() || !logFile.isFile()) return;

        // Output for new gZIP
        var date = new Date(System.currentTimeMillis()).toLocalDate().toString();
        String compressedFilePath = logFile.getParent() + "/" + date + ".log.gz";

        int count = 0;
        while (new File(compressedFilePath).exists()) compressedFilePath = logFile.getParent() + "/" + date + "-" + (count++) + ".log.gz";

        try (GZIPOutputStream gzipOutputStream = new GZIPOutputStream(new FileOutputStream(compressedFilePath)); InputStream inputStream = new FileInputStream(logFile)) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) gzipOutputStream.write(buffer, 0, bytesRead);
        } catch (Exception e) {
            LOGGER.error("Failed to compress vlc.log");
        }
        logFile.delete();
    }

    private static String getVersion() { return version; }
    private static String getVersion(Path from) { return ThreadUtil.tryAndReturn(defaultVar -> Files.exists(from) ? Files.readString(from) : defaultVar, null); }
}