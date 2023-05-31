package me.srrapero720.watermedia;

import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.FMLPaths;
import org.slf4j.Logger;

/**
 * Loader for FORGE
 */
@Mod("watermedia")
public class ForgeMedia {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final boolean status = WaterMedia.load(FMLPaths.GAMEDIR.get());

    public ForgeMedia() {
        MinecraftForge.EVENT_BUS.register(this);
        if (FMLEnvironment.dist.isDedicatedServer()) throw new IllegalStateException("WATERMeDIA shouldn't be installed on server!!");
        if (!status) LOGGER.error("Failed to load WATERMeDIA");
    }


    @SubscribeEvent
    public static void onUnloadingLevel(WorldEvent.Unload event) {

    }
}
