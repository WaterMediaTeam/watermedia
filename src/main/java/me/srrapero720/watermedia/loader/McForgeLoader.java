package me.srrapero720.watermedia.loader;

import me.srrapero720.watermedia.WaterMedia;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLLoader;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.nio.file.Path;

@Mod(WaterMedia.ID)
public class McForgeLoader implements ILoader {
    private static final Marker IT = MarkerManager.getMarker("ForgeLoader");

    public McForgeLoader() {
        try {
            WaterMedia.prepare(this).start();
        } catch (Exception e) {
            throw new RuntimeException("Failed starting " + WaterMedia.NAME + " for " + name() +": " + e.getMessage(), e);
        }
    }

    @Override public String name() { return "Forge"; }
    @Override public Path tmp() { return ILoader.TMP_DEFAULT; }
    @Override public Path cwd() { return ILoader.CWD_DEFAULT; }
    @Override public boolean client() { return FMLLoader.getDist().isClient(); }
}
