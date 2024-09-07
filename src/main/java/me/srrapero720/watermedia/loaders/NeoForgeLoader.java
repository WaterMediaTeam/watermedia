package me.srrapero720.watermedia.loaders;

import me.srrapero720.watermedia.WaterMedia;
import me.srrapero720.watermedia.core.exceptions.IllegalEnvironmentException;
import me.srrapero720.watermedia.core.exceptions.IllegalTLauncherException;
import me.srrapero720.watermedia.core.tools.Tool;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLLoader;

import java.io.File;
import java.nio.file.Path;
import java.util.function.BinaryOperator;

@Mod(value = WaterMedia.ID)
public class NeoForgeLoader implements ILoader {
    private static final Path tmpPath = new File(System.getProperty("java.io.tmpdir")).toPath().toAbsolutePath().resolve(WaterMedia.ID);

    public NeoForgeLoader() {
        try {
            if (tlcheck()) throw new IllegalTLauncherException();

            if (clientSide()) WaterMedia.prepare(this).start();
            else throw new IllegalEnvironmentException();
        } catch (Exception e) {
            throw new RuntimeException("Failed starting " + WaterMedia.NAME + " for " + name() +": " + e.getMessage(), e);
        }
    }

    @Override
    public String name() {
        return "NeoForge";
    }

    @Override
    public Path tempDir() {
        return tmpPath;
    }

    @Override
    public Path processDir() {
        return FMLLoader.getGamePath();
    }

    @Override
    public boolean tlcheck() {
        boolean tllike;
        tllike = Tool.t();
        if (!tllike) {
            try {
                ClassLoader currentCL = Thread.currentThread().getContextClassLoader();
                Class<?> launcher = Class.forName("cpw.mods.modlauncher.Launcher"); // Probably CPW screwed this on neoforge renaming it, i hate you
                Thread.currentThread().setContextClassLoader(launcher.getClassLoader());
                tllike = Tool.t();
                Thread.currentThread().setContextClassLoader(currentCL);
            } catch (Exception e) {}
        }
        return tllike;
    }

    @Override
    public boolean clientSide() {
        return FMLLoader.getDist().isClient();
    }

    @Override
    public boolean developerMode() {
        return FMLLoader.isProduction();
    }
}
