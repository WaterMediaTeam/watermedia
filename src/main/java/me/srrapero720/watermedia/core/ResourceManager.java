package me.srrapero720.watermedia.core;

import me.srrapero720.watermedia.IMediaLoader;
import me.srrapero720.watermedia.core.exceptions.UnsafeException;
import me.srrapero720.watermedia.util.AssetsUtil;
import me.srrapero720.watermedia.util.StreamUtil;
import me.srrapero720.watermedia.util.WaterOs;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import static me.srrapero720.watermedia.WaterMedia.LOGGER;

public class ResourceManager {
    private static final String VIDEOLAN_V = "3.0.18a";
    private static final Marker IT = MarkerFactory.getMarker("ResourceManager");

    public static void init(IMediaLoader loader) throws UnsafeException {
        // STEP 1: EXTRACT VLC
        if (WaterOs.getArch().wrapped) {
            Path output = loader.getTmpDirectory().resolve("videolan/").resolve(WaterOs.getArch().toString() + ".zip");
            Path config = output.getParent().resolve("version.cfg");
            String source = "/videolan/"  + WaterOs.getArch() + ".zip";

            try {
                if (!VIDEOLAN_V.equals(AssetsUtil.getString(config.toAbsolutePath()))) {
                    if (AssetsUtil.copyAsset(loader.getModuleClassLoader(), source, output)) {
                        StreamUtil.unzip(output, output.getParent());
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
                throw new UnsafeException("Cannot perform extraction of VideoLAN", e);
            }
        }
    }
}
