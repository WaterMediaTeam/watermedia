package me.srrapero720.watermedia.core;

import me.srrapero720.watermedia.api.loader.IMediaLoader;
import me.srrapero720.watermedia.core.tools.exceptions.ReInitException;
import me.srrapero720.watermedia.core.tools.FileTool;
import me.srrapero720.watermedia.core.tools.JarTool;
import me.srrapero720.watermedia.core.tools.OsTool;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import static me.srrapero720.watermedia.WaterMedia.LOGGER;

public class JarAssets {
    private static final String VIDEOLAN_V = "3.0.18a";
    private static final Marker IT = MarkerManager.getMarker("ResourceManager");
    private static boolean loaded = false;

    public static void init(IMediaLoader loader) throws Exception {
        if (loaded) throw new ReInitException(JarAssets.class.getSimpleName());

        // STEP 1: EXTRACT VLC
        if (OsTool.getArch().wrapped) {
            Path output = loader.tmpPath().resolve("videolan/").resolve(OsTool.getArch().toString() + ".zip");
            Path config = output.getParent().resolve("version.cfg");
            String source = "/videolan/"  + OsTool.getArch() + ".zip";

            try {
                if (!VIDEOLAN_V.equals(FileTool.readString(config.toAbsolutePath()))) {
                    if (JarTool.copyAsset(loader.classLoader(), source, output)) {
                        FileTool.unzip(output, output.getParent());
                        Files.delete(output);
                    }

                    // WRITE VERSION FILE
                    try {
                        Files.write(config, VIDEOLAN_V.getBytes(), StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE, StandardOpenOption.CREATE);
                    } catch (Exception e) {
                        LOGGER.error(IT, "Exception writing configuration file", e);
                    }
                }

            } catch (Exception e) {
                throw new IOException("Cannot perform extraction of VideoLAN", e);
            }
        }

        // STEP 2: EXTRACT LOADING GIF (was extracted on process root folder)
        Path loadingGif = loader.processPath().resolve("config/watermedia/assets/loading.gif");
        if (!Files.exists(loadingGif)) {
            JarTool.copyAsset(loader.classLoader(), "/pictures/loading.gif", loadingGif);
        }

        loaded = true;
    }
}