package me.srrapero720.watermedia;

import me.srrapero720.watermedia.loaders.IBootCore;
import me.srrapero720.watermedia.api.WaterMediaAPI;
import me.srrapero720.watermedia.tools.IOTool;
import me.srrapero720.watermedia.tools.JarTool;
import me.srrapero720.watermedia.tools.OsTool;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.nio.file.Files;
import java.nio.file.Path;

import static me.srrapero720.watermedia.WaterMedia.LOGGER;

@Deprecated
public class AssetsModule extends WaterMediaAPI {
    // CONSTANTS
    private static final Marker IT = MarkerManager.getMarker(AssetsModule.class.getSimpleName());
    private static final String VIDEOLAN_CFG_NAME = "version.cfg";
    private static final String VIDEOLAN_BIN_ASSET = "videolan/" + OsTool.ARCH + ".zip";
    private static final String VIDEOLAN_VER_ASSET = "/videolan/" + VIDEOLAN_CFG_NAME;
    private static final String LOADING_GIF = "config/watermedia/assets/loading.gif";

    private final Path zipOutputFile;
    private final Path configOutputFile;

    private final Path loadingGif;

    public AssetsModule() {
        super();
        IBootCore bootstrap = WaterMedia.getInstance().getBootCore();
        this.zipOutputFile = bootstrap.tempDir().resolve(VIDEOLAN_BIN_ASSET);
        this.configOutputFile = zipOutputFile.getParent().resolve(VIDEOLAN_CFG_NAME);

        this.loadingGif = bootstrap.processDir().resolve(LOADING_GIF);
    }

    @Override
    public Priority priority() {
        return Priority.HIGHEST;
    }

    @Override
    public boolean prepare() throws Exception {
        String versionInJar = JarTool.readString(VIDEOLAN_VER_ASSET);
        String versionInFile = IOTool.readString(configOutputFile);
        boolean wrapped = !OsTool.ARCH.wrapped;
        boolean versionMatch = versionInFile != null && versionInFile.equalsIgnoreCase(versionInJar);
        if (!wrapped && !versionMatch) return true;

        LOGGER.error(IT, "Binaries are {} and version file {}", wrapped ? "wrapped" : "NOT wrapped", versionMatch ? "match" : "DOESN'T match");
        return false;
    }

    @Override
    public void start() throws Exception {
        init$videolan$extract();
        init$assets$extract();
    }

    @Override
    public void release() {}

    public void init$videolan$extract() throws Exception {
        LOGGER.info(IT, "Extracting VideoLAN binaries...");
        if (JarTool.copyAsset(VIDEOLAN_BIN_ASSET, zipOutputFile)) {
            IOTool.unzip(IT, zipOutputFile);
            Files.delete(zipOutputFile);
        }

        JarTool.copyAsset(VIDEOLAN_VER_ASSET, configOutputFile);
        LOGGER.info(IT, "VideoLAN binaries extracted successfully");
    }

    public void init$assets$extract() throws RuntimeException {
        LOGGER.info(IT, "Extracting Assets...");
        if (!loadingGif.toFile().exists()) JarTool.copyAsset("/pictures/loading.gif", loadingGif);
        LOGGER.info(IT, "Assets extracted successfully");
    }
}