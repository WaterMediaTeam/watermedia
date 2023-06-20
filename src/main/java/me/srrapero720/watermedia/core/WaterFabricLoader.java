package me.srrapero720.watermedia.core;

import me.srrapero720.watermedia.WaterMedia;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

/**
 * Loader for FABRIC
 * Doing things with FABRIC classes/api is safe
 */
public class WaterFabricLoader implements ClientModInitializer {
    private static final Logger LOGGER = LoggerFactory.getLogger(WaterMedia.class);
    private static final Marker IT = MarkerFactory.getMarker("FabricLoader");
    private static void load() {
//        var allMods = FabricLoader.getInstance().getAllMods();
//        var self = allMods.stream().filter(modContainer -> modContainer.getMetadata().getId().equals("watermedia")).findAny();
        WaterMedia.load(FabricLoader.getInstance().getGameDir());
    }

    public WaterFabricLoader() {
        if (FabricLoader.getInstance().getEnvironmentType().equals(EnvType.SERVER)) {
            if (FabricLoader.getInstance().isDevelopmentEnvironment()) LOGGER.warn("WATERMeDIA shouldn't be installed on server. Ignoring crash because we are in DEV MODE.");
            else {
                LOGGER.warn(IT, "###########################  CAREFULL  ###################################");
                LOGGER.warn(IT, "Message from SrRapero720: Fabric is a bullshit, and i cant prevent you install WATERMeDIA on servers");
                LOGGER.warn(IT, "If you see this message i highly suggest to switch to FORGE. this mod may uses unsafe client side classes and methods");
                LOGGER.warn(IT, "and is not tested or designed to be on servers. expect crashes");
                LOGGER.warn(IT, "###########################  CAREFULL  ###################################");
            }
        } else load();
    }

    @Override
    public void onInitializeClient() {}
}