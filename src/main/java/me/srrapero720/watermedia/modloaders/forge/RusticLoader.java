package me.srrapero720.watermedia.modloaders.forge;

import me.lib720.watermod.reflect.ReflectTool;
import me.srrapero720.watermedia.WaterMedia;
import me.srrapero720.watermedia.api.loader.IEnvLoader;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.io.File;

import static me.srrapero720.watermedia.WaterMedia.LOGGER;

public class RusticLoader implements IEnvLoader {
    private static final Marker IT = MarkerManager.getMarker("LegacyForge");

    RusticLoader() {
        String MC_VERSION = ReflectTool.getValue("MC_VERSION", Loader.class, null);
        if (client()) LOGGER.info(IT, "Minecraft version '{}'", MC_VERSION);
    }

    @EventHandler
    public void init(FMLPostInitializationEvent event) {
        WaterMedia.getInstance(null).crash();
    }

    @Override
    public boolean tlauncher() {
        // FOLDER VALIDATION - Avoid ATLauncher and SKLauncher (for some reason)
        String f = new File("").toPath().toAbsolutePath().toString().toLowerCase();
        boolean tlauncher = f.contains("tlauncher");
        boolean atlauncher = f.contains("atlauncher");
        boolean sklauncher = f.contains("sklauncher");

        // TLSKINCAPE VALIDATION
        boolean tlskincape = installed("tlskincape");

        return tlskincape || (tlauncher && !atlauncher && !sklauncher);
    }

    @Override
    public boolean development() {
        return false;
    }

    @Override
    public boolean client() {
        return FMLCommonHandler.instance().getSide().isClient();
    }

    @Override
    public boolean installed(String modId) {
        return Loader.isModLoaded(modId);
    }
}