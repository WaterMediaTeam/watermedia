package me.srrapero720.watermedia.internal.modloaders;

import com.mojang.logging.LogUtils;
import me.srrapero720.watermedia.WaterMedia;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Loader for FABRIC
 * Doing things with FABRIC classes/api is safe
 */
public class WMForFabric implements ModInitializer {
    private static final Logger LOGGER = LoggerFactory.getLogger(WaterMedia.class);
    private static void load() {
        WaterMedia.load(FabricLoader.getInstance().getGameDir(), FabricLoader.getInstance().isDevelopmentEnvironment());
    }

    public WMForFabric() {
        if (FabricLoader.getInstance().getEnvironmentType().equals(EnvType.SERVER)) {
            if (FabricLoader.getInstance().isDevelopmentEnvironment()) LOGGER.warn("WATERMeDIA shouldn't be installed on server. Ignoring crash because we are in DEV MODE.");
            else throw new IllegalStateException("WATERMeDIA shouldn't be installed on server!!");
        } else load();
    }

    @Override
    public void onInitialize() {}
}