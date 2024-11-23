package org.watermedia.loader;

import org.watermedia.WaterMedia;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLLoader;

import java.nio.file.Path;

@Mod(WaterMedia.ID)
public class McNeoForgeLoader implements WaterMedia.ILoader {
    public McNeoForgeLoader() {
        try {
            WaterMedia.prepare(this).start();
        } catch (Exception e) {
            throw new RuntimeException("Failed starting " + WaterMedia.NAME + " for " + name() +": " + e.getMessage(), e);
        }
    }

    @Override public String name() { return "NeoForge"; }
    @Override public Path tmp() { return WaterMedia.DEFAULT_LOADER.tmp(); }
    @Override public Path cwd() { return FMLLoader.getGamePath(); }
    @Override public boolean client() { return FMLLoader.getDist().isClient(); }
}
