package me.srrapero720.watermedia.minecraft;

import jdk.internal.loader.ClassLoaders;
import me.srrapero720.watermedia.WaterMedia;
import me.srrapero720.watermedia.IMediaLoader;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;
import org.apache.commons.io.IOUtils;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Path;

import static me.srrapero720.watermedia.WaterMedia.LOGGER;

/**
 * Loader for FABRIC
 * Doing things with FABRIC classes/api is safe
 */
public class FabricModLoader implements PreLaunchEntrypoint, IMediaLoader {
    private static final Marker IT = MarkerFactory.getMarker("FabricLoader");
    private final WaterMedia INSTANCE;
    private ClassLoader CL;

    public FabricModLoader() {
        INSTANCE = new WaterMedia(this);
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
        INSTANCE.init();
        INSTANCE.throwClientException();
        INSTANCE.throwServerException();
    }

    public static FabricModLoader getInstance() {
        return (FabricModLoader) FabricLoader.getInstance().getEntrypointContainers(WaterMedia.ID, PreLaunchEntrypoint.class);
    }

    @Override
    public boolean isDevEnv() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    @Override
    public boolean isClient() {
        return FabricLoader.getInstance().getEnvironmentType().equals(EnvType.CLIENT);
    }

    @Override
    public boolean isThisModPresent(String modid) {
        return FabricLoader.getInstance().isModLoaded(modid);
    }

    @Override
    public ClassLoader getClassLoader() {
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
    public Path getGameDir() {
        return FabricLoader.getInstance().getGameDir();
    }

    @Override
    public Path getTempDir() {
        return new File(System.getProperty("java.io.tmpdir")).toPath().toAbsolutePath().resolve("watermedia");
    }

    @Override
    public boolean isTLauncher() {
        return false;
    }

    private ClassLoader getWorkingClassLoader() {
        ClassLoader cl;
        InputStream dummy;

        // ATTEMPT #1 - SELF CLASS CLASSLOADER
        cl = ForgeModLoader.class.getClassLoader();
        if ((dummy = cl.getResourceAsStream("/vlc/args.json")) != null) {
            LOGGER.info(IT, "Found working ClassLoader (SELF)");
            IOUtils.closeQuietly(dummy);
            return cl;
        }

        // ATTEMPT #2 - SELF INSTANCE CLASSLOADER
        cl = this.getClass().getClassLoader();
        if ((dummy = cl.getResourceAsStream("/vlc/args.json")) != null) {
            LOGGER.info(IT, "Found working ClassLoader (SELF)");
            IOUtils.closeQuietly(dummy);
            return cl;
        }

        // ATTEMPT #3 - CURRENT THREAD CLASSLOADER
        cl = Thread.currentThread().getContextClassLoader();
        if ((dummy = cl.getResourceAsStream("/vlc/args.json")) != null) {
            LOGGER.info(IT, "Found working ClassLoader (CurrentThread)");
            IOUtils.closeQuietly(dummy);
            return cl;
        }

        // ATTEMPT #4 - PLATFORM CLASSLOADER
        cl = ClassLoaders.platformClassLoader();
        if ((dummy = cl.getResourceAsStream("/vlc/args.json")) != null) {
            LOGGER.info(IT, "Found working ClassLoader (PlatformClassLoader)");
            IOUtils.closeQuietly(dummy);
            return cl;
        }

        // ATTEMPT #5 - SYSTEM CLASSLOADER
        cl = ClassLoader.getSystemClassLoader();
        if ((dummy = cl.getResourceAsStream("/vlc/args.json")) != null) {
            LOGGER.info(IT, "Found working ClassLoader (SystemClassLoader)");
            IOUtils.closeQuietly(dummy);
            return cl;
        }

        return null;
    }
}