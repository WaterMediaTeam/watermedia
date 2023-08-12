package me.srrapero720.watermedia.core;

import me.srrapero720.watermedia.api.loader.IMediaLoader;
import me.srrapero720.watermedia.tools.exceptions.ReloadingException;
import me.srrapero720.watermedia.tools.FileTool;
import me.srrapero720.watermedia.tools.JarTool;
import me.srrapero720.watermedia.tools.OsTool;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import static me.srrapero720.watermedia.WaterMedia.LOGGER;

public class ResourceManager {
    private static final String VIDEOLAN_V = "3.0.18a";
    private static final Marker IT = MarkerFactory.getMarker("ResourceManager");
    private static boolean loaded = false;

    public static void init(IMediaLoader loader) throws Exception {
        if (loaded) throw new ReloadingException(ResourceManager.class.getSimpleName());
        // STEP 1: EXTRACT VLC
        if (OsTool.getArch().wrapped) {
            Path output = loader.getTmpDirectory().resolve("videolan/").resolve(OsTool.getArch().toString() + ".zip");
            Path config = output.getParent().resolve("version.cfg");
            String source = "/videolan/"  + OsTool.getArch() + ".zip";

            try {
                if (!VIDEOLAN_V.equals(FileTool.readString(config.toAbsolutePath()))) {
                    if (JarTool.copyAsset(loader.getModuleClassLoader(), source, output.toString())) {
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
        loaded = true;
    }
}
