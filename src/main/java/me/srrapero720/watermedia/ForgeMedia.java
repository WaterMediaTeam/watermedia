package me.srrapero720.watermedia;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Loader for FORGE
 */
@Mod("watermedia")
public class ForgeMedia {
//    private static final Logger LOGGER = LogUtils.getLogger();
    private static final WaterMedia MEDIA = WaterMedia.get();

    public ForgeMedia() {
        MinecraftForge.EVENT_BUS.register(this);
        MEDIA.onFakeEvent();
    }


    @SubscribeEvent
    public static void onUnloadingLevel(WorldEvent.Unload event) {}
}
