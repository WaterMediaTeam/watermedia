package org.watermedia.loader;

import org.watermedia.WaterMedia;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.api.EnvType;

import java.nio.file.Path;

import static net.fabricmc.loader.api.FabricLoader.getInstance;

public class FabricLoader implements ModInitializer, WaterMedia.ILoader {
    private static final Path CWD = getInstance().getGameDir();

    @Override
    public void onInitialize() {
        try {
            WaterMedia.prepare(this).start();
        } catch (Exception e) {
            throw new RuntimeException("Failed starting " + WaterMedia.NAME + " for " + name() + ": " + e.getMessage(), e);
        }
    }

    @Override public String name() { return "Fabric"; }
    @Override public Path cwd() { return CWD; }
    @Override public Path tmp() { return WaterMedia.DEFAULT_LOADER.tmp(); }
    @Override public boolean client() { return getInstance().getEnvironmentType() == EnvType.CLIENT; }
}
