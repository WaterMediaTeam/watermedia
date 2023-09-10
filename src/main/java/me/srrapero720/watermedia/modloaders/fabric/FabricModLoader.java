package me.srrapero720.watermedia.modloaders.fabric;

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
 * Is safe to do things inside this class
 * By default, this class preloads WATERMeDIA over other mods
 * but haven't warranties, because other mods can preload easy too.
 */
public class FabricModLoader implements PreLaunchEntrypoint, IMediaLoader, IEnvLoader {
    private static final Marker IT = MarkerManager.getMarker("FabricModLoader");
    private static final String NAME = "Fabric";
    private final WaterMedia instance;
    private Path TMP;

    public FabricModLoader() {
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
    public String name() { return NAME; }

    @Override
    public Path processPath() { return FabricLoader.getInstance().getGameDir(); }

    @Override
    public Path tmpPath() {
        return (TMP != null) ? TMP : (TMP = new File(System.getProperty("java.io.tmpdir")).toPath().toAbsolutePath().resolve("watermedia"));
    }

    @Override
    public boolean tlauncher() {
        return installed("tlskincape") || processPath().toAbsolutePath().toString().contains("tlauncher");
    }
}