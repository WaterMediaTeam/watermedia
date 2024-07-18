package me.srrapero720.watermedia.loader;

import me.srrapero720.watermedia.WaterMedia;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;

public class McFabricLoader implements ClientModInitializer, ILoader {
    private static final Path CWD = FabricLoader.getInstance().getGameDir();

    @Override
    public void onInitializeClient() {
        try {
            WaterMedia.prepare(this).start();
        } catch (Exception e) {
            throw new RuntimeException("Failed starting " + WaterMedia.NAME + " for " + name() + ": " + e.getMessage(), e);
        }
    }

    @Override public String name() { return "Fabric"; }
    @Override public Path cwd() { return CWD; }
    @Override public Path tmp() { return ILoader.TMP_DEFAULT; }

    @Override public boolean client() { return FabricLoader.getInstance().getEnvironmentType().equals(EnvType.CLIENT); }
}
