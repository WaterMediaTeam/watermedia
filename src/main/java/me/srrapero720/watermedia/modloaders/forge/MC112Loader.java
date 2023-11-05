package me.srrapero720.watermedia.modloaders.forge;

import me.srrapero720.watermedia.WaterMedia;
import me.srrapero720.watermedia.api.bootstrap.IBootstrap;
import me.srrapero720.watermedia.modloaders.IModLoader;
import me.srrapero720.watermedia.modloaders.ModLoader;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;

import java.io.File;
import java.nio.file.Path;

public class MC112Loader implements IBootstrap, IModLoader {
    private final Path tempPath = new File(System.getProperty("java.io.tmpdir")).toPath().toAbsolutePath().resolve("watermedia");
    private final Path processPath = new File("").toPath();
    private Exception exception;

    MC112Loader() {
        try {
            ModLoader.envInit(this);
            WaterMedia.init(this).init();
        } catch (Exception e) {
            this.exception = e;
        }
    }

    @EventHandler
    public void init(FMLPostInitializationEvent event) {
        if (exception != null) throw new RuntimeException("Failed due loading WaterMedia", exception);
    }

    @Override public String name() { return "Forge"; }
    @Override public Path processDir() { return processPath; }
    @Override public Path tempDir() { return tempPath; }
    @Override public boolean developerMode() { return false; }
    @Override public boolean enforceCrash() { return false; }
    @Override public boolean clientSide() { return FMLCommonHandler.instance().getSide().isClient(); }
    @Override public boolean modPresent(String modId) { return Loader.isModLoaded(modId); }

    @Override
    public boolean tlcheck() {
        return modPresent("tlskincape") || new File("").toPath().toAbsolutePath().toString().contains("tlauncher");
    }
}