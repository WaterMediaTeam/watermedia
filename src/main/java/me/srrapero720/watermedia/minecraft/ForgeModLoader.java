package me.srrapero720.watermedia.minecraft;

import me.srrapero720.watermedia.IMediaLoader;
import me.srrapero720.watermedia.WaterMedia;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLLoader;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.io.File;
import java.nio.file.Path;

import static me.srrapero720.watermedia.WaterMedia.LOGGER;

/**
 * Loader for FORGE
 * Doing things with FORGE classes/api is safe
 * IMPORTANT: this class just fires post-launch just to interact with forge stuff
 */
@Mod(WaterMedia.ID)
public class ForgeModLoader implements IMediaLoader {
    private static final Marker IT = MarkerFactory.getMarker("ForgeLoader");
    private final WaterMedia WM;
    private ClassLoader CL;

    static {
        LOGGER.info(IT, "Class was PRELOADED");
    }

    public ForgeModLoader() {
        LOGGER.info(IT, "Starting ForgeModLoader");
        WM = new WaterMedia(this);
        IEventBus BUS = FMLJavaModLoadingContext.get().getModEventBus();

        // SETUP
        BUS.addListener((FMLClientSetupEvent event) -> WM.exceptionThrow());

        // INIT
        WM.init();

        //Make sure the mod being absent on the other network side does not cause the client to display the server as incompatible
        // TODO: Use any tricky way to do that on old forge versions
//        ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(() -> NetworkConstants.IGNORESERVERONLY, (a, b) -> true));
    }

    @Override
    public boolean isDev() { return !FMLLoader.isProduction(); }

    @Override
    public boolean isClient() { return FMLLoader.getDist().isClient(); }

    @Override
    public boolean isInstalled(String modid) {
        if (ModList.get() != null) return ModList.get().isLoaded(modid);
        else return FMLLoader.getLoadingModList().getModFileById(modid) != null;
    }

    @Override
    public ClassLoader getJarClassLoader() {
        if (CL != null) return CL;
        if (WM.test$classLoader(this.getClass().getClassLoader())) return CL = this.getClass().getClassLoader();
        return CL = Thread.currentThread().getContextClassLoader();
    }

    @Override
    public String getLoaderName() { return "FORGE"; }

    @Override
    public Path getWorkingDir() { return FMLLoader.getGamePath(); }

    @Override
    public Path getTempDir() {
        return new File(System.getProperty("java.io.tmpdir")).toPath().toAbsolutePath().resolve("watermedia");
    }

    @Override
    public boolean isTLauncher() {
        return isInstalled("tlskincape") || getWorkingDir().toAbsolutePath().toString().contains("tlauncher");
    }
}