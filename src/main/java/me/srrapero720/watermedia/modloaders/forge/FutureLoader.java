package me.srrapero720.watermedia.modloaders.forge;

import me.srrapero720.watermedia.WaterMedia;
import me.srrapero720.watermedia.api.loader.IEnvLoader;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLLoader;

import java.io.File;

public class FutureLoader implements IEnvLoader {
    FutureLoader() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener((FMLCommonSetupEvent event) -> WaterMedia.getInstance(null).crash());
    }

    @Override
    public boolean tlauncher() {
        // FOLDER VALIDATION - Avoid ATLauncher and SKLauncher (for some reason)
        String f = new File("").toPath().toAbsolutePath().toString().toLowerCase();
        boolean tlauncher = f.contains("tlauncher");
        boolean atlauncher = f.contains("atlauncher");
        boolean sklauncher = f.contains("sklauncher");

        // TLSKINCAPE VALIDATION
        boolean tlskincape = installed("tlskincape");

        return tlskincape || (tlauncher && !atlauncher && !sklauncher);
    }

    @Override
    public boolean development() {
        return !FMLLoader.isProduction();
    }

    @Override
    public boolean client() {
        return FMLLoader.getDist().isClient();
    }

    @Override
    public boolean installed(String modId) {
        if (ModList.get() != null) return ModList.get().isLoaded(modId);
        else return FMLLoader.getLoadingModList().getModFileById(modId) != null;
    }
}