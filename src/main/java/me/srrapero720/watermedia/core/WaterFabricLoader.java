package me.srrapero720.watermedia.core;

import me.srrapero720.watermedia.WaterMedia;
import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import static me.srrapero720.watermedia.WaterMedia.LOGGER;

/**
 * Loader for FABRIC
 * Doing things with FABRIC classes/api is safe
 */
@Deprecated(forRemoval = true)
public class WaterFabricLoader implements ClientModInitializer {
    private static final Marker IT = MarkerFactory.getMarker("FabricLoader");

    public WaterFabricLoader() {
        LOGGER.info(IT, "Running WaterMedia on Fabric environment");
        LOGGER.error(IT, "###########################  ILLEGAL ENVIRONMENT  ###################################");
        LOGGER.error(IT, "WATERMeDIA not longer provides support for Fabric environments, breaks basic Java");
        LOGGER.error(IT, "If you encounter issues loading VLC try install VLC3, if not then report to Fabric");
        LOGGER.error(IT, "###########################  ILLEGAL ENVIRONMENT  ###################################");
    }

    @Override
    public void onInitializeClient() {
        WaterMedia.init();
    }
}