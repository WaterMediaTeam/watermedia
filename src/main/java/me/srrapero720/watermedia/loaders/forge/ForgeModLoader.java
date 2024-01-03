package me.srrapero720.watermedia.loaders.forge;

import me.srrapero720.watermedia.WaterMedia;
import me.srrapero720.watermedia.loaders.IBootCore;
import me.srrapero720.watermedia.tools.exceptions.IllegalEnvironmentException;
import me.srrapero720.watermedia.tools.exceptions.IllegalTLauncherException;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLLoader;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.io.File;
import java.nio.file.Path;

import static me.srrapero720.watermedia.WaterMedia.LOGGER;

@Mod(modid = WaterMedia.ID, acceptableRemoteVersions = "*", clientSideOnly = true, value = WaterMedia.ID, dependencies="after:watermedia_natives")
public class ForgeModLoader implements IBootCore {
    private static final Marker IT = MarkerManager.getMarker("ForgeModLoader");
    private static final Path tmpPath = new File(System.getProperty("java.io.tmpdir")).toPath().toAbsolutePath().resolve("watermedia");
    private static final Path processPath = new File("").toPath().toAbsolutePath();

    public ForgeModLoader() {
        LOGGER.info(IT, "Preparing '{}' for {}", WaterMedia.ID, name().toUpperCase());
        try {
            if (tlcheck()) throw new IllegalTLauncherException();

            if (clientSide()) WaterMedia.create(this).init();
            else if (developerMode()) throw new IllegalEnvironmentException();

        } catch (Exception e) {
            throw new RuntimeException("Cannot run " + WaterMedia.NAME + " for FORGE", e);
        }
    }

    @Override
    public String name() {
        return "Forge";
    }

    @Override
    public Path tempDir() {
        return tmpPath;
    }

    @Override
    public Path processDir() {
        return processPath;
    }

    @Override
    public boolean tlcheck() {
        try {
            return Loader.isModLoaded("tlskincape") || new File("").toPath().toAbsolutePath().toString().contains("tlauncher");
        } catch (Throwable t) {
            try {
                return FMLLoader.getLoadingModList().getModFileById("tlskincape") != null || processPath.toAbsolutePath().toString().contains("tlauncher");
            } catch (Throwable t2) {
                LOGGER.error(IT, "Cannot check if TL was installed");
            }
        }
        return false;
    }

    @Override
    public boolean clientSide() {
        try {
            return FMLCommonHandler.instance().getSide().isClient();
        } catch (Throwable t) {
            try {
                return FMLLoader.getDist().isClient();
            } catch (Throwable t2) {
                LOGGER.error(IT, "Cannot check if was client, assuming it was");
                return true;
            }
        }
    }

    @Override
    public boolean developerMode() {
        try {
            return !FMLLoader.isProduction();
        } catch (Throwable t) {
            LOGGER.error(IT, "Cannot check if was developer env, assuming it wasn't");
            return false;
        }
    }
}