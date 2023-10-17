package me.srrapero720.watermedia.modloaders.forge;

import me.lib720.watermod.reflect.ReflectTool;
import me.srrapero720.watermedia.WaterMedia;
import me.srrapero720.watermedia.api.loader.IEnvLoader;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
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
        return installed("tlskincape") || new File("").toPath().toAbsolutePath().toString().contains("tlauncher");
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