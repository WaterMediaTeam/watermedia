package me.srrapero720.watermedia;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.FMLPaths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Loader for FORGE
 * Doing things with FORGE classes/api is safe
 */
@Mod("watermedia")
public class WaterForge {
    private static final Logger LOGGER = LoggerFactory.getLogger(WaterMedia.class);

    public WaterForge() {
        if (FMLEnvironment.dist.isDedicatedServer()) {
            if (FMLLoader.isProduction()) throw new IllegalStateException("WATERMeDIA shouldn't be installed on server!!");
            else LOGGER.warn("WATERMeDIA shouldn't be installed on server. Ignoring crash because we are in DEV MODE.");
        } else load();
    }

    public static void load() { WaterMedia.load(FMLPaths.GAMEDIR.get(), !FMLLoader.isProduction()); }
}