package me.srrapero720.watermedia.loaders;

import com.mojang.logging.LogUtils;
import me.srrapero720.watermedia.WaterMedia;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.FMLPaths;
import org.slf4j.Logger;

/**
 * Loader for FORGE
 */
@Mod("watermedia")
public class ForgeMediaLoader {
    private static final Logger LOGGER = LogUtils.getLogger();

    public ForgeMediaLoader() {
        if (FMLEnvironment.dist.isDedicatedServer()) {
            if (FMLLoader.isProduction()) throw new IllegalStateException("WATERMeDIA shouldn't be installed on server!!");
            else LOGGER.warn("WATERMeDIA shouldn't be installed on server. Ignoring crash because we are in DEV MODE.");
        } else load();
    }

    public static void load() {
        WaterMedia.load(FMLPaths.GAMEDIR.get(), !FMLLoader.isProduction());
    }
}