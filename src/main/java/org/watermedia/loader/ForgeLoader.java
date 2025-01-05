package org.watermedia.loader;

import org.watermedia.WaterMedia;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLLoader;

import java.nio.file.Path;

@Mod(WaterMedia.ID)
public class ForgeLoader implements WaterMedia.ILoader {
    public ForgeLoader() {
        try {
            WaterMedia.prepare(this).start();
        } catch (Exception e) {
            throw new RuntimeException("Failed starting " + WaterMedia.NAME + " for " + name() +": " + e.getMessage(), e);
        }
    }

    @Override public String name() { return "Forge"; }
    @Override public Path tmp() { return WaterMedia.DEFAULT_LOADER.tmp(); }
    @Override public Path cwd() { return WaterMedia.DEFAULT_LOADER.cwd(); }
    @Override public boolean client() { return FMLLoader.getDist().isClient(); }
}
