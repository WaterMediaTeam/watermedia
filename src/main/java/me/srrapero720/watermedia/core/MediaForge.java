package me.srrapero720.watermedia.core;

import me.srrapero720.watermedia.WaterMedia;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.FMLPaths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

/**
 * Loader for FORGE
 * Doing things with FORGE classes/api is safe
 */
@Mod("watermedia")
public class MediaForge {
    private static final Logger LOGGER = LoggerFactory.getLogger(WaterMedia.class);
    private static final Marker IT = MarkerFactory.getMarker("ForgeLoader");

    public MediaForge() {
        if (FMLEnvironment.dist.isDedicatedServer()) {
            if (FMLLoader.isProduction()) {
                LOGGER.error(IT, "###########################  ILLEGAL ENVIRONMENT  ###################################");
                LOGGER.error(IT, "WATERMeDIA is not designed to run on SERVER_SIDE. remove this mod from your server to stop crashing");
                LOGGER.error(IT, "If dependant mods throws error trying to load WATERMeDIA classes report it to the creator.");
                LOGGER.error(IT, "###########################  ILLEGAL ENVIRONMENT  ###################################");
                throw new IllegalStateException("REMOVE WATERMeDIA FROM SERVER_SIDE, THIS IS A CLIENT_SIDE MOD!!!");
            }
            else LOGGER.warn(IT, "WATERMeDIA shouldn't be installed on server. Ignoring crash because we are in DEV MODE.");
        } else load();
    }

    /* FMLLoader.getLoadingModList().getModFileById("watermedia").getFile().getFilePath(), */
    public static void load() { WaterMedia.load(FMLPaths.GAMEDIR.get()); }
}