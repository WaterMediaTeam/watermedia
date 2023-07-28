package me.srrapero720.watermedia.minecraft;

import jdk.internal.loader.ClassLoaders;
import me.srrapero720.watermedia.IMediaLoader;
import me.srrapero720.watermedia.WaterMedia;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.io.File;
import java.nio.file.Path;

import static me.srrapero720.watermedia.WaterMedia.LOGGER;

/**
 * Loader for FABRIC
 * Doing things with FABRIC classes/api is safe
 */
public class FabricModLoader implements PreLaunchEntrypoint, IMediaLoader {
    private static final Marker IT = MarkerFactory.getMarker("FabricLoader");
    private final WaterMedia WM;
    private ClassLoader CL;

    public FabricModLoader() {
        WM = new WaterMedia(this);
    }

    @Override
    public void onPreLaunch() {
        if (getWorkingClassLoader() != null) {
            launchWaterMedia();
        } else {
            LOGGER.error(IT, "###########################  ILLEGAL INIT STATE  ###################################");
            LOGGER.error(IT, "By some FABRIC-LOADER restrictions, WATERMeDIA can't be loaded by itself using loader");
            LOGGER.error(IT, "Dependant mods needs to load WATERMeDIA in game thread by themself");
            LOGGER.error(IT, "Instance was created and can be loaded using FabricModLoader#launchWaterMedia()");
            LOGGER.error(IT, "###########################  ILLEGAL INIT STATE  ###################################");
        }
    }

    public void launchWaterMedia() {
        assert getWorkingClassLoader() != null;
        WM.init();
        WM.throwClientException();
        WM.throwServerException();
    }

    public static FabricModLoader getInstance() {
        return (FabricModLoader) FabricLoader.getInstance().getEntrypointContainers(WaterMedia.ID, PreLaunchEntrypoint.class);
    }

    @Override
    public boolean isDev() { return FabricLoader.getInstance().isDevelopmentEnvironment(); }

    @Override
    public boolean isClient() { return FabricLoader.getInstance().getEnvironmentType().equals(EnvType.CLIENT); }

    @Override
    public boolean isInstalled(String modid) { return FabricLoader.getInstance().isModLoaded(modid); }

    @Override
    public ClassLoader getJarClassLoader() {
        if (CL != null) return CL;

        ClassLoader cl = getWorkingClassLoader();
        if (cl == null) {
            LOGGER.error(IT, "Cannot get a working ClassLoader, returning (CurrentThread) by default");
            return Thread.currentThread().getContextClassLoader(); // This is not working
        }

        return CL = cl;
    }

    @Override
    public String getLoaderName() {
        return "FABRIC";
    }

    @Override
    public Path getWorkingDir() {
        return FabricLoader.getInstance().getGameDir();
    }

    @Override
    public Path getTempDir() {
        return new File(System.getProperty("java.io.tmpdir")).toPath().toAbsolutePath().resolve("watermedia");
    }

    // TODO: DECIDE DO IT OR NOT, DEPENDS OF FABRIC BEHAVIOR
    @Override
    public boolean isTLauncher() { return false; }

    private ClassLoader getWorkingClassLoader() {
        ClassLoader[] attempts = new ClassLoader[] {
                FabricModLoader.class.getClassLoader(),
                this.getClass().getClassLoader(),
                Thread.currentThread().getContextClassLoader(),
                ClassLoaders.platformClassLoader(),
                ClassLoader.getSystemClassLoader(),
                ClassLoaders.appClassLoader(),
                FabricLoader.getInstance().getClass().getClassLoader()
        };

        for (int i = 0; i < attempts.length; i++)
            if (WM.workingClassLoader(attempts[i])) {
                LOGGER.info(IT, "Founded a working classloader, index {}", i);
                return attempts[i];
            }
        return null;
    }
}