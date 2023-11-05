package me.srrapero720.watermedia.modloaders.forge;

import me.srrapero720.watermedia.WaterMedia;
import me.srrapero720.watermedia.modloaders.IModLoader;
import me.srrapero720.watermedia.api.bootstrap.IBootstrap;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.io.File;
import java.nio.file.Path;

import static me.srrapero720.watermedia.WaterMedia.LOGGER;

@Mod(modid = WaterMedia.ID, acceptableRemoteVersions = "*", clientSideOnly = true, value = WaterMedia.ID)
public class ForgeModLoader {
    private static final Marker IT = MarkerManager.getMarker("ForgeModLoader");

    public ForgeModLoader() {
        LOGGER.info(IT, "Preparing...");

        IModLoader modLoader = null;
        Class<IModLoader>[] loaders = new Class[] { MC112Loader.class, MC116Loader.class };
        for (Class<IModLoader> loader: loaders) {
            try {
                modLoader = loader.getDeclaredConstructor().newInstance();
                break;
            } catch (Throwable ignored) {}
        }

        if (modLoader == null) throw new RuntimeException("Cannot run WaterMedia loader");
    }
}