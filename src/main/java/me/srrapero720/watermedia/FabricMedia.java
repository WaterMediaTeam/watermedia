package me.srrapero720.watermedia;

import net.fabricmc.api.ModInitializer;

/**
 * Loader for FABRIC
 */
public class FabricMedia implements ModInitializer {

    @Override
    public void onInitialize() {
        if (!WaterMedia.load()) throw new WaterMedia.IllegalMediaLoadingState();
    }
}
