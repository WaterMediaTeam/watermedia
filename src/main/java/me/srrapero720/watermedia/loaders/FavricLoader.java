package me.srrapero720.watermedia.loaders;

import me.srrapero720.watermedia.WaterMedia;
import me.srrapero720.watermedia.tools.exceptions.IllegalTLauncherException;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.io.File;
import java.nio.file.Path;

import static me.srrapero720.watermedia.WaterMedia.LOGGER;

public class FavricLoader implements PreLaunchEntrypoint, ILoader {
    private static final Marker IT = MarkerManager.getMarker("FabricModLoader");

    private final Path tempPath = new File(System.getProperty("java.io.tmpdir")).toPath().toAbsolutePath().resolve("watermedia");
    private final Path processPath = FabricLoader.getInstance().getGameDir();

    @Override
    public void onPreLaunch() {
        LOGGER.info(IT, "Preparing '{}' for {}", WaterMedia.NAME, name().toUpperCase());
        try {
            if (tlcheck()) throw new IllegalTLauncherException();

            if (clientSide()) WaterMedia.prepare(this).start();
        } catch (Exception e) {
            throw new RuntimeException("Cannot run " + WaterMedia.NAME + " for FABRIC", e);
        }
    }

    @Override public String name() { return "Fabric"; }
    @Override public Path processDir() { return processPath; }
    @Override public Path tempDir() { return tempPath; }
    @Override public boolean developerMode() { return FabricLoader.getInstance().isDevelopmentEnvironment(); }
    @Override public boolean clientSide() { return FabricLoader.getInstance().getEnvironmentType().equals(EnvType.CLIENT); }

    @Override
    public boolean tlcheck() {
        return FabricLoader.getInstance().isModLoaded("tlskincape") || processDir().toAbsolutePath().toString().contains("tlauncher");
    }
}