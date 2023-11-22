package me.srrapero720.watermedia.modloaders.forge;

import me.srrapero720.watermedia.WaterMedia;
import me.srrapero720.watermedia.api.loader.IEnvLoader;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.FMLNetworkConstants;
import org.apache.commons.lang3.tuple.Pair;

import java.io.File;

public class CurrentLoader implements IEnvLoader {
    CurrentLoader() {
        ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.DISPLAYTEST,
                () -> Pair.of(() -> FMLNetworkConstants.IGNORESERVERONLY, (incoming, isNetwork) -> true));
        FMLJavaModLoadingContext.get().getModEventBus().addListener((FMLCommonSetupEvent event) -> WaterMedia.getInstance().crash());
    }

    @Override
    public boolean tlauncher() {
        return installed("tlskincape") || new File("").toPath().toAbsolutePath().toString().contains("tlauncher");
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