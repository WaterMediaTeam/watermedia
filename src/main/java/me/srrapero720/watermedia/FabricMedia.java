package me.srrapero720.watermedia;

import net.fabricmc.api.ModInitializer;

/**
 * Loader for FABRIC
 */
public class FabricMedia implements ModInitializer {
    private static final WaterMedia MEDIA = WaterMedia.get();
    @Override
    public void onInitialize() {
        MEDIA.onFakeEvent();
    }
}
