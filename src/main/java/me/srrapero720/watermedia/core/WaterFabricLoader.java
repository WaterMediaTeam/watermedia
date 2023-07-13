package me.srrapero720.watermedia.core;

import me.srrapero720.watermedia.WaterMedia;
import me.srrapero720.watermedia.core.util.IModLoader;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.io.File;
import java.nio.file.Path;

/**
 * Loader for FABRIC
 * Doing things with FABRIC classes/api is safe
 */
public class WaterFabricLoader implements ModInitializer, IModLoader {
    private static final Marker IT = MarkerFactory.getMarker("FabricLoader");
    private final WaterMedia INSTANCE;

    public WaterFabricLoader() {
        INSTANCE = new WaterMedia(this);
    }

    @Override
    public void onInitialize() {
        INSTANCE.init();
        INSTANCE.throwClientException();
        INSTANCE.throwServerException();
    }

    @Override
    public boolean isDevEnv() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    @Override
    public boolean isClient() {
        return FabricLoader.getInstance().getEnvironmentType().equals(EnvType.CLIENT);
    }

    @Override
    public boolean isThisModPresent(String modid) {
        return FabricLoader.getInstance().isModLoaded(modid);
    }

    @Override
    public ClassLoader getClassLoader() {
        return Thread.currentThread().getContextClassLoader(); // This is not working
    }

    @Override
    public String getLoaderName() {
        return "FABRIC";
    }

    @Override
    public Path getGameDir() {
        return FabricLoader.getInstance().getGameDir();
    }

    @Override
    public Path getTempDir() {
        return new File(System.getProperty("java.io.tmpdir")).toPath();
    }
}