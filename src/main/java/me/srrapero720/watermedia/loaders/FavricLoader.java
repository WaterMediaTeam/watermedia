package me.srrapero720.watermedia.loaders;

import me.srrapero720.watermedia.WaterMedia;
import me.srrapero720.watermedia.core.exceptions.IllegalTLauncherException;
import me.srrapero720.watermedia.core.tools.Tool;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.io.File;
import java.nio.file.Path;

public class FavricLoader implements ClientModInitializer, ILoader {
    private static final Marker IT = MarkerManager.getMarker("FabricLoader");

    private final Path tempPath = new File(System.getProperty("java.io.tmpdir")).toPath().toAbsolutePath().resolve("watermedia");
    private final Path processPath = FabricLoader.getInstance().getGameDir();

    @Override
    public void onInitializeClient() {
        try {
            if (tlcheck()) throw new IllegalTLauncherException();

            if (clientSide()) WaterMedia.prepare(this).start();
        } catch (Exception e) {
            throw new RuntimeException("Failed starting " + WaterMedia.NAME + " for " + name() + ": " + e.getMessage(), e);
        }
    }

    @Override public String name() { return "Fabric"; }
    @Override public Path processDir() { return processPath; }
    @Override public Path tempDir() { return tempPath; }
    @Override public boolean developerMode() { return FabricLoader.getInstance().isDevelopmentEnvironment(); }
    @Override public boolean clientSide() { return FabricLoader.getInstance().getEnvironmentType().equals(EnvType.CLIENT); }

    @Override
    public boolean tlcheck() {
        // TLSKINCAPE VALIDATION
        boolean tllike = FabricLoader.getInstance().isModLoaded("tlskincape") || FabricLoader.getInstance().isModLoaded("tlauncher_custom_cape_skin");

        if (!tllike) {
            tllike = Tool.t();
            if (!tllike) {
                try {
                    ClassLoader currentCL = Thread.currentThread().getContextClassLoader();
                    Thread.currentThread().setContextClassLoader(PreLaunchEntrypoint.class.getClassLoader());
                    tllike = Tool.t();
                    Thread.currentThread().setContextClassLoader(currentCL);
                } catch (Exception e) {}
            }
        }
        return tllike;
    }
}
