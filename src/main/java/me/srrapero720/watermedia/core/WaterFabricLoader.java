package me.srrapero720.watermedia.core;

import me.srrapero720.watermedia.WaterMedia;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
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
    private static void load() {
        WaterMedia.load(FabricLoader.getInstance().getGameDir());
    }

    public WaterFabricLoader() {
        if (FabricLoader.getInstance().getEnvironmentType().equals(EnvType.SERVER)) {
            LOGGER.error(IT, "###########################  ILLEGAL ENVIRONMENT  ###################################");
            LOGGER.error(IT, "WATERMeDIA is not designed to run on SERVER_SIDE. this mod can cause a lot of crashes");
            LOGGER.error(IT, "But fabric is a bullshit and dependant mods can't be loaded on server without WMedia");
            LOGGER.error(IT, "if you experiment crashes with WATERMeDIA; please report it to dependant mod developer");
            LOGGER.error(IT, "###########################  ILLEGAL ENVIRONMENT  ###################################");

            if (FabricLoader.getInstance().isDevelopmentEnvironment())
                LOGGER.warn("WATERMeDIA shouldn't be installed on server. Ignoring crash because we are in DEV MODE.");
        } else load();
    }

    @Override
    public void onInitializeClient() {}
}