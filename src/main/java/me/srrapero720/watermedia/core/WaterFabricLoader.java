package me.srrapero720.watermedia.core;

import me.srrapero720.watermedia.WaterMedia;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import static me.srrapero720.watermedia.WaterMedia.LOGGER;

/**
 * Loader for FABRIC
 * Doing things with FABRIC classes/api is safe
 */
@Deprecated(forRemoval = true)
public class WaterFabricLoader implements ModInitializer {
    private static final Marker IT = MarkerFactory.getMarker("FabricLoader");

    @Override
    public void onInitialize() {
        FabricLoader.getInstance().getAllMods().forEach(modContainer -> {
            if (modContainer.getMetadata().getId().equals("fancyvideo_api")) WaterMedia.onFVADetected();
        });

        LOGGER.info(IT, "Running WaterMedia on Fabric environment");

        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            WaterMedia.init();
        } else {
            LOGGER.error(IT, "###########################  ILLEGAL ENVIRONMENT  ###################################");
            LOGGER.error(IT, "WATERMeDIA is not designed to run on SERVERS. remove this mod from server to stop crashes");
            LOGGER.error(IT, "If dependant mods throws error loading WATERMeDIA classes report it to the creator");
            LOGGER.error(IT, "###########################  ILLEGAL ENVIRONMENT  ###################################");
        }
    }
}