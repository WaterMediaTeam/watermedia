package me.srrapero720.watermedia.core.videolan;


import me.lib720.caprica.vlcj.factory.MediaPlayerFactory;
import me.lib720.caprica.vlcj.factory.discovery.provider.CustomDirectoryProvider;
import me.srrapero720.watermedia.core.util.IWaterMediaLoader;
import me.srrapero720.watermedia.core.util.Tools;
import me.srrapero720.watermedia.api.WaterMediaAPI;
import me.srrapero720.watermedia.api.external.ThreadUtil;
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
    public static final Marker IT = MarkerFactory.getMarker("VideoLAN");
    private static MediaPlayerFactory FACTORY;
    public static MediaPlayerFactory factory() { return FACTORY; }

    public static void init(IWaterMediaLoader modLoader) {
        Path workingDir = modLoader.getTempDir();
        if (FACTORY != null) throw new IllegalStateException("Rejected attempt to reload VideoLAN");

        // SETUP PATHS
        var logs = workingDir.resolve("logs/latest.log");
        var path = workingDir.resolve("vlc/");

        // LOGGER INIT
        if (!Files.exists(logs.toAbsolutePath())) {
            if (logs.getParent().toFile().mkdirs()) LOGGER.info(IT, "Logger dir created");
            else compressAndDeleteLogFile(logs);
        }

        // INIT
        CustomDirectoryProvider.init(path);
        BinManager.init(path);

        if (Tools.getArch().wrapped) {
            // Check if we need to update binaries
            boolean fresh = false;
            if (!BinManager.resVersion().equals(BinManager.installedVersion())) {
                // CLEAR
                LOGGER.info(IT, "Running VLC cleanup");
                BinManager.cleanup();

                // EXTRACT
                LOGGER.info(IT, "Running VLC binary installation");
                BinManager.extractAll(modLoader);

                // SET LOCAL VERSION
                try {
                    var config = path.resolve("version.cfg");
                    if (!Files.exists(config.getParent())) Files.createDirectories(config.getParent());
                    Files.writeString(config, BinManager.resVersion());
                } catch (Exception e) {
                    LOGGER.error(IT, "Exception writing configuration file", e);
                }
                fresh = true;
            } else LOGGER.warn(IT, "Detected WaterMedia's VLC installation, skipping extract");

            // Integrity check
            if (!fresh) {
                LOGGER.info(IT, "Running integrity check");
                for (var binary : BinManager.values()) binary.checkIntegrity(modLoader);
            }
        } else {
            LOGGER.error(IT, "###########################  VLC NOT PRE-INSTALLED  ###################################");
            LOGGER.error(IT, "WATERMeDIA doesn't include VLC binaries for your operative system / system architecture");
            LOGGER.error(IT, "You had to install VLC manually in https://www.videolan.org/ - More info ask to SrRapero720");
            LOGGER.error(IT, "###########################  VLC NOT PRE-INSTALLED  ###################################");
        }

        FACTORY = ThreadUtil.tryAndReturnNull(defaultVar -> {
            String[] args = Tools.getJsonListFromRes(modLoader.getClassLoader(), "vlc/args.json").toArray(new String[0]);
            for (int i = 0; i < args.length; i++) {
                args[i] = args[i].replace("%logfile%", logs.toAbsolutePath().toString());
            }

            return WaterMediaAPI.createVLCFactory(args);
        }, e -> LOGGER.error(IT, "Failed to load VLC", e));
    }


    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static void compressAndDeleteLogFile(Path logFilePath) {
        File logFile = logFilePath.toFile();
        if (!logFile.exists() || !logFile.isFile()) return;

        // Output for new gZIP
        var date = new Date(System.currentTimeMillis()).toLocalDate().toString();
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