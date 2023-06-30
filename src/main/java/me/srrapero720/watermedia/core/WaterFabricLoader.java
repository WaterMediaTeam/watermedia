package me.srrapero720.watermedia.core;

import me.srrapero720.watermedia.WaterMedia;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import static me.srrapero720.watermedia.WaterMedia.LOGGER;

/**
 * Loader for FABRIC
 * Doing things with FABRIC classes/api is safe
 */
public class WaterFabricLoader implements ClientModInitializer {
    private static final Marker IT = MarkerFactory.getMarker("FabricLoader");

    public WaterFabricLoader() {
        LOGGER.info(IT, "Running WaterMedia on Fabric environment");
        WaterMedia.load(FabricLoader.getInstance().getGameDir());
    }

    @Override
    public void onInitializeClient() {}
}