package me.srrapero720.watermedia.modloaders.forge;

import me.srrapero720.watermedia.WaterMedia;
import me.srrapero720.watermedia.api.bootstrap.IBootstrap;
import me.srrapero720.watermedia.modloaders.IModLoader;
import me.srrapero720.watermedia.modloaders.ModLoader;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLLoader;

import java.io.File;
import java.nio.file.Path;

public class MC116Loader implements IBootstrap, IModLoader {
    private final Path tempPath = new File(System.getProperty("java.io.tmpdir")).toPath().toAbsolutePath().resolve("watermedia");
    private final Path processPath = new File("").toPath();
    private Exception exception;

    MC116Loader() {
        try {
            ModLoader.envInit(this);
            WaterMedia.init(this).init();
        } catch (Exception e) {
            this.exception = e;
        }

        FMLJavaModLoadingContext.get().getModEventBus().addListener((FMLCommonSetupEvent event) -> {
            if (exception != null) throw new RuntimeException("Failed due loading WaterMedia", exception);
        });
    }

    @Override public String name() { return "Forge"; }
    @Override public Path processDir() { return processPath; }
    @Override public Path tempDir() { return tempPath; }
    @Override public boolean developerMode() { return !FMLLoader.isProduction(); }
    @Override public boolean enforceCrash() { return !developerMode(); }
    @Override public boolean clientSide() { return FMLLoader.getDist().isClient(); }
    @Override public boolean modPresent(String modId) { return FMLLoader.getLoadingModList().getModFileById(modId) != null; }

    @Override
    public boolean tlcheck() {
        return modPresent("tlskincape") || new File("").toPath().toAbsolutePath().toString().contains("tlauncher");
    }
}