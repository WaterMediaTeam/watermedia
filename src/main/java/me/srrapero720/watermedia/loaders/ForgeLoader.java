package me.srrapero720.watermedia.loaders;

import me.srrapero720.watermedia.WaterMedia;
import me.srrapero720.watermedia.core.tools.exceptions.IllegalEnvironmentException;
import me.srrapero720.watermedia.core.tools.exceptions.IllegalTLauncherException;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLLoader;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.io.File;
import java.nio.file.Path;

import static me.srrapero720.watermedia.WaterMedia.LOGGER;

@Mod(modid = WaterMedia.ID, acceptableRemoteVersions = "*", clientSideOnly = true, value = WaterMedia.ID)
public class ForgeLoader implements ILoader {
    private static final Marker IT = MarkerManager.getMarker("ForgeLoader");
    private static final Path tmpPath = new File(System.getProperty("java.io.tmpdir")).toPath().toAbsolutePath().resolve(WaterMedia.ID);
    private static final Path processPath = new File("").toPath().toAbsolutePath();

    public ForgeLoader() {
        try {
            ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.DISPLAYTEST, () -> Pair.of(() -> "", (incoming, isNetwork) -> true));
        } catch (Throwable ignored) {}

        try {
            if (tlcheck()) throw new IllegalTLauncherException();

            if (clientSide()) WaterMedia.prepare(this).start();
            else if (!developerMode()) throw new IllegalEnvironmentException();
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
        boolean tllike = false;
        try {
            tllike = Loader.isModLoaded("tlskincape")
                    || Loader.isModLoaded("tlauncher_custom_cape_skin");
        } catch (Throwable t) {
            try {
                tllike = FMLLoader.getLoadingModList().getModFileById("tlskincape") != null
                        || FMLLoader.getLoadingModList().getModFileById("tlauncher_custom_cape_skin") != null;
            } catch (Throwable t2) {
                LOGGER.error(IT, "Cannot check if TL was installed");
            }
        }

        String f = processDir().toAbsolutePath().toString().toLowerCase();
        boolean tlauncher = f.contains("tlauncher");
        boolean atlauncher = f.contains("atlauncher");
        boolean sklauncher = f.contains("skcraftlauncher");

        return tllike || (tlauncher && !atlauncher && !sklauncher);
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
