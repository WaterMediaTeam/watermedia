package me.srrapero720.watermedia.minecraft;

import me.srrapero720.watermedia.WaterMedia;
import me.srrapero720.watermedia.IMediaLoader;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.network.NetworkConstants;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.io.File;
import java.nio.file.Path;

/**
 * Loader for FORGE
 * Doing things with FORGE classes/api is safe
 */
@Mod(WaterMedia.ID)
public class ForgeModLoader implements IMediaLoader {
    private static final Marker IT = MarkerFactory.getMarker("ForgeLoader");
    private final WaterMedia INSTANCE;

    public ForgeModLoader() {
        // DEFINE
        INSTANCE = new WaterMedia(this);
        IEventBus BUS = FMLJavaModLoadingContext.get().getModEventBus();

        // SETUP
        BUS.addListener((FMLClientSetupEvent event) -> INSTANCE.throwClientException());
        BUS.addListener((FMLDedicatedServerSetupEvent event) -> INSTANCE.throwServerException());

        // INIT
        INSTANCE.init();

        //Make sure the mod being absent on the other network side does not cause the client to display the server as incompatible
        ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(() -> NetworkConstants.IGNORESERVERONLY, (a, b) -> true));
    }

    @Override
    public boolean isDevEnv() {
        return !FMLLoader.isProduction();
    }

    @Override
    public boolean isClient() {
        return FMLLoader.getDist().isClient();
    }

    @Override
    public boolean isThisModPresent(String modid) {
        if (ModList.get() != null) return ModList.get().isLoaded(modid);
        else return FMLLoader.getLoadingModList().getModFileById(modid) != null;
    }

    @Override
    public ClassLoader getClassLoader() {
        return Thread.currentThread().getContextClassLoader(); // This will be problematic
    }

    @Override
    public String getLoaderName() {
        return "FORGE";
    }

    @Override
    public Path getGameDir() {
        return FMLLoader.getGamePath();
    }

    @Override
    public Path getTempDir() {
        return new File(System.getProperty("java.io.tmpdir")).toPath().toAbsolutePath().resolve("watermedia");
    }
}