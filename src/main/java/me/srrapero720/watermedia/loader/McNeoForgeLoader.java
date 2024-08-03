package me.srrapero720.watermedia.loader;

import me.srrapero720.watermedia.WaterMedia;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLLoader;

import java.nio.file.Path;

@Mod(WaterMedia.ID)
public class McNeoForgeLoader implements ILoader {
    public McNeoForgeLoader() {
        try {
            WaterMedia.prepare(this).start();
        } catch (Exception e) {
            throw new RuntimeException("Failed starting " + WaterMedia.NAME + " for " + name() +": " + e.getMessage(), e);
        }
    }

    @Override public String name() { return "NeoForge"; }
    @Override public Path tmp() { return TMP_DEFAULT; }
    @Override public Path cwd() { return FMLLoader.getGamePath(); }
    @Override public boolean client() { return FMLLoader.getDist().isClient(); }
}
