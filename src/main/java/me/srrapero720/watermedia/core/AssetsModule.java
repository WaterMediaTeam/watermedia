package me.srrapero720.watermedia.core;

import me.srrapero720.watermedia.api.loader.IMediaLoader;
import me.srrapero720.watermedia.api.loader.IModuleBootstrap;
import me.srrapero720.watermedia.core.tools.FileTool;
import me.srrapero720.watermedia.core.tools.JarTool;
import me.srrapero720.watermedia.core.tools.OsTool;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.nio.file.Files;
import java.nio.file.Path;

import static me.srrapero720.watermedia.WaterMedia.LOGGER;

public class AssetsModule extends IModuleBootstrap {
    // CONSTANTS
    private static final Marker IT = MarkerManager.getMarker(AssetsModule.class.getSimpleName());
    private static final String VIDEOLAN_CFG_NAME = "version.cfg";
    private static final String VIDEOLAN_BIN_ASSET = "videolan/" + OsTool.getArch() + ".7z";
    private static final String VIDEOLAN_VER_ASSET = "/videolan/" + VIDEOLAN_CFG_NAME;
    private static final String LOADING_GIF = "config/watermedia/assets/loading.gif";

    private final Path zipOutputFile;
    private final Path configOutputFile;

    private final Path loadingGif;
    public AssetsModule(IMediaLoader loader) {
        super(loader);
        this.zipOutputFile = loader.tmpPath().resolve(VIDEOLAN_BIN_ASSET);
        this.configOutputFile = zipOutputFile.getParent().resolve(VIDEOLAN_CFG_NAME);

        this.loadingGif = loader.processPath().resolve(LOADING_GIF);
    }

    @Override
    public boolean boot() {
        LOGGER.info(IT, "Booting Module");

        String versionInJar = JarTool.readString(VIDEOLAN_VER_ASSET);
        String versionInFile = FileTool.readString(configOutputFile);
        boolean wrapped = !OsTool.getArch().wrapped;
        boolean versionMatch = versionInFile != null && versionInFile.equalsIgnoreCase(versionInJar);
        if (!wrapped && !versionMatch) return true;

        LOGGER.warn(IT, "Booting aborted, binaries are {} and version file {}", wrapped ? "wrapped" : "NOT wrapped", versionMatch ? "MATCH" : "DOESN'T MATCH");
        return false;
    }

    @Override
    public void init() throws Exception {
        LOGGER.info(IT, "Executing module...");
        init$videolan$extract();
        init$assets$extract();
        LOGGER.info(IT, "Execution successfully");
    }

    @Override
    public void release() {}


    public void init$videolan$extract() throws Exception {
        LOGGER.info(IT, "Extracting VideoLAN binaries...");
        if (JarTool.copyAsset(VIDEOLAN_BIN_ASSET, zipOutputFile)) {
            FileTool.un7zip(zipOutputFile);
            Files.delete(zipOutputFile);
        }

        JarTool.copyAsset(VIDEOLAN_VER_ASSET, configOutputFile);
        LOGGER.info(IT, "VideoLAN binaries extracted successfully");
    }

    public void init$assets$extract() {
        LOGGER.info(IT, "Extracting Assets...");
        if (!loadingGif.toFile().exists()) JarTool.copyAsset("/pictures/loading.gif", loadingGif);
        LOGGER.info(IT, "Assets extracted successfully");
    }
}