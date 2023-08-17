package me.srrapero720.watermedia.modloaders;

import me.srrapero720.watermedia.api.loader.IEnvLoader;
import me.srrapero720.watermedia.api.loader.IMediaLoader;
import me.srrapero720.watermedia.WaterMedia;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.io.File;
import java.nio.file.Path;

import static me.srrapero720.watermedia.WaterMedia.LOGGER;

/**
 * Loader for FABRIC
 * Is safe do things inside this class
 * By default, this class preloads WATERMeDIA over other mods
 * but haven't warranties, because other mods can preload easy too.
 */
public class FabricML implements PreLaunchEntrypoint, IMediaLoader, IEnvLoader {
    private static final Marker IT = MarkerManager.getMarker("FabricModLoader");
    private static final String NAME = "Fabric";
    private final WaterMedia instance;
    private ClassLoader CL;

    public FabricML() {
        LOGGER.info(IT, "Starting...");
        instance = WaterMedia.getInstance(this);
    }

    @Override
    public void onPreLaunch() {
        if (client()) instance.init();
        instance.crash();
    }

    @Override
    public boolean development() { return FabricLoader.getInstance().isDevelopmentEnvironment(); }

    @Override
    public boolean client() { return FabricLoader.getInstance().getEnvironmentType().equals(EnvType.CLIENT); }

    @Override
    public boolean installed(String modid) { return FabricLoader.getInstance().isModLoaded(modid); }

    @Override
    public ClassLoader getModuleClassLoader() {
        if (CL != null) return CL;
        return CL = Thread.currentThread().getContextClassLoader();
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Path getProcessDirectory() {
        return FabricLoader.getInstance().getGameDir();
    }

    @Override
    public Path getTmpDirectory() {
        return new File(System.getProperty("java.io.tmpdir")).toPath().toAbsolutePath().resolve("watermedia");
    }

    @Override
    public boolean tlauncher() {
        return installed("tlskincape") || getProcessDirectory().toAbsolutePath().toString().contains("tlauncher");
    }
}