package me.srrapero720.watermedia.core;

import me.srrapero720.watermedia.WaterMedia;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.FMLPaths;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import static me.srrapero720.watermedia.WaterMedia.LOGGER;

/**
 * Loader for FORGE
 * Doing things with FORGE classes/api is safe
 */
@Mod("watermedia")
public class WaterForgeLoader {
    private static final Marker IT = MarkerFactory.getMarker("ForgeLoader");

    public WaterForgeLoader() {
        if (FMLEnvironment.dist.isDedicatedServer()) {
            LOGGER.error(IT, "###########################  ILLEGAL ENVIRONMENT  ###################################");
            LOGGER.error(IT, "WATERMeDIA is not designed to run on SERVER_SIDE. remove this mod from your server to stop crashing");
            LOGGER.error(IT, "If dependant mods throws error trying to load WATERMeDIA classes report it to the creator.");
            LOGGER.error(IT, "###########################  ILLEGAL ENVIRONMENT  ###################################");

            if (FMLLoader.isProduction()) throw new IllegalStateException("REMOVE WATERMeDIA FROM SERVER_SIDE, THIS IS A CLIENT_SIDE MOD!!!");
            else LOGGER.warn(IT, "WATERMeDIA should not be installed on server. discarding crash because we are running on dev-env");
        } else load();
    }

    /* FMLLoader.getLoadingModList().getModFileById("watermedia").getFile().getFilePath(), */
    public static void load() { WaterMedia.load(FMLPaths.GAMEDIR.get()); }
}