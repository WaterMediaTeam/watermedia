package me.srrapero720.watermedia.modloaders.forge;

import me.srrapero720.watermedia.WaterMedia;
import me.srrapero720.watermedia.api.loader.IEnvLoader;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.io.File;

import static me.srrapero720.watermedia.WaterMedia.LOGGER;

public class LegacyLoader implements IEnvLoader {
    private static final Marker IT = MarkerManager.getMarker("LegacyForge");

    LegacyLoader() {
        LOGGER.info(IT, "Minecraft version '{}'", Loader.MC_VERSION);
        WaterMedia.getInstance().onEnvironmentInit(this);
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
        return FMLCommonHandler.instance().getSide() == Side.CLIENT;
    }

    @Override
    public boolean installed(String modId) {
        return Loader.isModLoaded(modId);
    }
}
