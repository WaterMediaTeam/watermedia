package me.srrapero720.watermedia.loaders;

import com.mojang.logging.LogUtils;
import me.srrapero720.watermedia.WaterMedia;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;

/**
 * Loader for FABRIC
 */
public class FabricMediaLoader implements ClientModInitializer {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static void load() {
        WaterMedia.load(FabricLoader.getInstance().getGameDir(), FabricLoader.getInstance().isDevelopmentEnvironment());
    }


    @Override
    public void onInitializeClient() {
        // Not required
//        if (FabricLoader.getInstance().getEnvironmentType().equals(EnvType.SERVER))
//            throw new IllegalStateException("WATERMeDIA shouldn't be installed on server!!");
        load();
    }
}
