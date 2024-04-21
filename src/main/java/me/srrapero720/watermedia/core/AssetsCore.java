package me.srrapero720.watermedia.core;

import me.srrapero720.watermedia.api.loader.IMediaLoader;
import me.srrapero720.watermedia.core.tools.FileTool;
import me.srrapero720.watermedia.core.tools.JarTool;
import me.srrapero720.watermedia.core.tools.OsTool;
import me.srrapero720.watermedia.core.tools.exceptions.ReInitException;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import watermod.safety.TryCore;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicBoolean;

import static me.srrapero720.watermedia.WaterMedia.LOGGER;

public class AssetsCore {
    private static final Marker IT = MarkerManager.getMarker(AssetsCore.class.getSimpleName());
    private static final AtomicBoolean loaded = new AtomicBoolean(false);

    // VLC
    private static final String VLC_BIN_RES = ("videolan/" + OsTool.getArch()).concat(".7z");
    private static final String VLC_V_FILE = "version.cfg";
    private static final String VLC_V_RES = "/videolan/".concat(VLC_V_FILE);

    // ASSETS
    private static final String LOADING_GIF = "config/watermedia/assets/loading.gif";

    public static void init(IMediaLoader loader) throws Exception {
        if (loaded.compareAndSet(true, true)) throw new ReInitException(AssetsCore.class.getSimpleName());
        init$extractVLC(loader);
        init$extractLoadingGif(loader);
    }


    private static void init$extractVLC(IMediaLoader loader) throws IOException {
        if (!OsTool.getArch().wrapped) return;
        LOGGER.info(IT, "Extracting VLC...");

        File zipOutput = loader.tmpPath().resolve(VLC_BIN_RES).toFile(); // <tmp>\watermedia\videolan\<os-arch>.zip
        Path configOutput = zipOutput.toPath().getParent().resolve(VLC_V_FILE); // <tmp>\watermedia\videolan\version.cfg

        try {
            String versionInJar = JarTool.readString(VLC_V_RES);
            String versionInFile = FileTool.readString(configOutput);

            // SKIP EXTRACTION IF MATCH
            if (versionInFile != null && versionInFile.equalsIgnoreCase(versionInJar) && !zipOutput.exists()) {
                LOGGER.info(IT, "Extraction cancelled, config file matches with JAR config file");
                return;
            }

            // EXTRACT ZIP FROM JAR AND UNZIP
            if ((!zipOutput.exists() && JarTool.copyAsset(VLC_BIN_RES, zipOutput.toPath())) || zipOutput.exists()) {
                FileTool.un7zip(zipOutput.toPath());
                zipOutput.deleteOnExit();

                TryCore.simple(() -> JarTool.copyAsset(VLC_V_RES, configOutput), LOGGER::error);
            }
        } catch (Exception e) {
            throw new IOException("Cannot perform extraction of VideoLAN", e);
        } finally {
            LOGGER.info(IT, "Extraction finished");
        }
    }

    private static void init$extractLoadingGif(IMediaLoader loader) {
        Path loadingGif = loader.processPath().resolve(LOADING_GIF);
        if (!loadingGif.toFile().exists()) JarTool.copyAsset("/pictures/loading.gif", loadingGif);
    }
}