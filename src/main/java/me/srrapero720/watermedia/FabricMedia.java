package me.srrapero720.watermedia;

import com.mojang.logging.LogUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;

/**
 * Loader for FABRIC
 */
public class FabricMedia implements ModInitializer {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final boolean status = WaterMedia.load(FabricLoader.getInstance().getGameDir(), FabricLoader.getInstance().isDevelopmentEnvironment());
    @Override
    public void onInitialize() {
        if (FabricLoader.getInstance().getEnvironmentType().equals(EnvType.SERVER))
            throw new IllegalStateException("WATERMeDIA shouldn't be installed on server!!");
        if (!status) LOGGER.error("Failed to load WATERMeDIA");
    }
}
