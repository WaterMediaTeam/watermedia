package me.srrapero720.watermedia.minecraft;

import cpw.mods.modlauncher.Launcher;
import me.srrapero720.watermedia.IEnvLoader;
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
import org.spongepowered.asm.launch.MixinBootstrap;

import static me.srrapero720.watermedia.WaterMedia.LOGGER;

/**
 * Loader for FORGE
 * Doing things with FORGE classes/api is safe
 * IMPORTANT: this class just fires post-launch just to interact with forge stuff
 */
@Mod(WaterMedia.ID)
public class ForgeModLoader implements IEnvLoader {
    private static final Marker IT = MarkerFactory.getMarker("ForgeLoader");
    private final WaterMedia instance;

    public ForgeModLoader() throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        LOGGER.info(IT, "Starting ForgeModLoader");

        instance = ((WaterMedia) Class.forName("me.srrapero720.watermedia.minecraft.CpwLoader", false, Launcher.INSTANCE.getClass().getClassLoader()).getField("instance").get(null));
        instance.onEnvironmentInit(this);

        // SETUP
        IEventBus BUS = FMLJavaModLoadingContext.get().getModEventBus();
        BUS.addListener((FMLClientSetupEvent event) -> instance.crash());
        BUS.addListener((FMLDedicatedServerSetupEvent event) -> instance.crash());

        // TODO: Use any tricky way to do that on old forge versions
        //Make sure the mod being absent on the other network side does not cause the client to display the server as incompatible
//        ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(() -> NetworkConstants.IGNORESERVERONLY, (a, b) -> true));
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