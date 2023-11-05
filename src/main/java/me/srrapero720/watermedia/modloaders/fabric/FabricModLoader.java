package me.srrapero720.watermedia.modloaders.fabric;

import me.srrapero720.watermedia.modloaders.IModLoader;
import me.srrapero720.watermedia.api.bootstrap.IBootstrap;
import me.srrapero720.watermedia.WaterMedia;
import me.srrapero720.watermedia.modloaders.ModLoader;
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
public class FabricModLoader implements PreLaunchEntrypoint, IBootstrap, IModLoader {
    private static final Marker IT = MarkerManager.getMarker("FabricModLoader");

    private final Path tempPath = new File(System.getProperty("java.io.tmpdir")).toPath().toAbsolutePath().resolve("watermedia");
    private final Path processPath = FabricLoader.getInstance().getGameDir();

    @Override
    public void onPreLaunch() {
        LOGGER.info(IT, "Preparing...");
        if (clientSide()) {
            try {
                ModLoader.envInit(this);
                WaterMedia.init(this).init();
            } catch (Exception e) {
                throw new RuntimeException("Failed due loading WaterMedia", e);
            }
        }
    }

    @Override public String name() { return "Fabric"; }
    @Override public Path processDir() { return processPath; }
    @Override public Path tempDir() { return tempPath; }
    @Override public boolean developerMode() { return FabricLoader.getInstance().isDevelopmentEnvironment(); }
    @Override public boolean enforceCrash() { return false; }
    @Override public boolean clientSide() { return FabricLoader.getInstance().getEnvironmentType().equals(EnvType.CLIENT); }
    @Override public boolean modPresent(String modid) { return FabricLoader.getInstance().isModLoaded(modid); }

    @Override
    public boolean tlcheck() {
        return modPresent("tlskincape") || processDir().toAbsolutePath().toString().contains("tlauncher");
    }
}