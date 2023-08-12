package me.srrapero720.watermedia.minecraft;

import me.srrapero720.watermedia.api.loader.IEnvLoader;
import me.srrapero720.watermedia.api.loader.IMediaLoader;
import me.srrapero720.watermedia.WaterMedia;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
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
public class ForgeModLoader implements IMediaLoader, IEnvLoader {
    private static final Marker IT = MarkerFactory.getMarker("ForgeModLoader");
    private static final String NAME = "Forge";
    private final WaterMedia instance;

    private ClassLoader CL;
    public ForgeModLoader() {
        LOGGER.info(IT, "Starting...");

        instance = WaterMedia.getInstance(this);
        if (client()) instance.init();

        // SETUP
        IEventBus BUS = FMLJavaModLoadingContext.get().getModEventBus();
        BUS.addListener((FMLClientSetupEvent event) -> instance.crash());
        BUS.addListener((FMLDedicatedServerSetupEvent event) -> instance.crash());

        // TODO: Use any tricky way to do that on old forge versions
        //Make sure the mod being absent on the other network side does not cause the client to display the server as incompatible
//        ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(() -> NetworkConstants.IGNORESERVERONLY, (a, b) -> true));
    }

    @Override
    public ClassLoader getModuleClassLoader() {
        return (CL != null) ? CL : (CL = Thread.currentThread().getContextClassLoader());
    }

    @Override
    public String getName() { return NAME; }

    @Override
    public Path getProcessDirectory() { return new File("").toPath(); }

    @Override
    public Path getTmpDirectory() {
        return new File(System.getProperty("java.io.tmpdir")).toPath().toAbsolutePath().resolve("watermedia");
    }

    @Override
    public boolean development() { return !FMLLoader.isProduction(); }

    @Override
    public boolean client() { return FMLLoader.getDist().isClient(); }

    @Override
    public boolean installed(String modid) {
        if (ModList.get() != null) return ModList.get().isLoaded(modid);
        else return FMLLoader.getLoadingModList().getModFileById(modid) != null;
    }
    @Override
    public boolean tlauncher() {
        return installed("tlskincape") || FMLLoader.getGamePath().toAbsolutePath().toString().contains("tlauncher");
    }
}