package org.watermedia.loader;

import org.watermedia.WaterMedia;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;

import java.nio.file.Path;

import static net.fabricmc.loader.api.FabricLoader.getInstance;

// FABRIC IS USED AS COSMIC CUBE MOD LOADER, THIS LOADER MUST BE WORKING ON IT TOO
public class FabricLoader implements ClientModInitializer, WaterMedia.ILoader {
    private static final Path CWD = getInstance().getGameDir();

    @Override
    public void onInitializeClient() {
        try {
            WaterMedia.prepare(this).start();
        } catch (Exception e) {
            throw new RuntimeException("Failed starting " + WaterMedia.NAME + " for " + name() + ": " + e.getMessage(), e);
        }
    }

    @Override public String name() { return "Fabric (agnostic)"; }
    @Override public Path cwd() { return CWD; }
    @Override public Path tmp() { return WaterMedia.DEFAULT_LOADER.tmp(); }

    @Override public boolean client() { return getInstance().getEnvironmentType() == EnvType.CLIENT; }
}
