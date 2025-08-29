package org.watermedia.loaders;

import jdk.internal.loader.ClassLoaders;
import org.watermedia.WaterMedia;
import org.watermedia.core.exceptions.IllegalEnvironmentException;
import org.watermedia.core.exceptions.IllegalTLauncherException;
import org.watermedia.core.exceptions.IncompatibleModException;
import org.watermedia.core.tools.Tool;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLLoader;

import java.io.File;
import java.nio.file.Path;
import java.util.Collection;

@Mod(value = WaterMedia.ID)
public class NeoFLoader implements ILoader {
    private static final Path tmpPath = new File(System.getProperty("java.io.tmpdir")).toPath().toAbsolutePath().resolve(WaterMedia.ID);

    public NeoFLoader() {
        try {
            if (tlcheck()) throw new IllegalTLauncherException();
            if (ofcheck()) throw new IncompatibleModException("optifine", "Optifine", "Embeddium (embeddium) or Sodium (sodium)");

            if (clientSide()) WaterMedia.prepare(this).start();
            else if (!developerMode()) throw new IllegalEnvironmentException();
        } catch (Exception e) {
            throw new RuntimeException("Failed starting " + WaterMedia.NAME + " for " + name() +": " + e.getMessage(), e);
        }
    }

    public boolean ofcheck() {
        try {
            Class.forName("optifine.Installer", false, Thread.currentThread().getContextClassLoader());
            return true;
        } catch (Exception ignored) {}
        return false;
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
        // first lookup attempt
        boolean isT = Tool.t();
        final ClassLoader current = Thread.currentThread().getContextClassLoader();

        try {
            // second attempt
            if (!isT) {
                Class<?> launcher = Class.forName("cpw.mods.modlauncher.Launcher");
                Thread.currentThread().setContextClassLoader(launcher.getClassLoader());
                isT = Tool.t();
                Thread.currentThread().setContextClassLoader(current);
            }
        } catch (Throwable ignored) {}

        try {
            // second point one attempt
            if (!isT) {
                Class<?> launcher = Class.forName("net.minecraftforge.modlauncher.Launcher");
                Thread.currentThread().setContextClassLoader(launcher.getClassLoader());
                isT = Tool.t();
                Thread.currentThread().setContextClassLoader(current);
            }
        } catch (Throwable ignored) {}

        try {
            // third... too deep
            if (!isT) {
                Class<?> launcher = Class.forName("cpw.mods.bootstraplauncher.BootstrapLauncher");
                Thread.currentThread().setContextClassLoader(launcher.getClassLoader());
                isT = Tool.t();
                Thread.currentThread().setContextClassLoader(current);
            }
        } catch (Throwable ignored) {}

        try {
            // third point one... too deep but not deeper
            if (!isT) {
                Class<?> launcher = Class.forName("net.minecraftforge.bootstraplauncher.BootstrapLauncher");
                Thread.currentThread().setContextClassLoader(launcher.getClassLoader());
                isT = Tool.t();
                Thread.currentThread().setContextClassLoader(current);
            }
        } catch (Throwable ignored) {}

        try {
            // see you all in hell
            if (!isT) {
                Thread.currentThread().setContextClassLoader(ClassLoader.getSystemClassLoader());
                isT = Tool.t();
                Thread.currentThread().setContextClassLoader(current);
            }
        } catch (Throwable ignore) {}

        try {
            // welcome to hell
            if (!isT) {
                Thread.currentThread().setContextClassLoader(ClassLoaders.appClassLoader());
                isT = Tool.t();
                Thread.currentThread().setContextClassLoader(current);
            }
        } catch (Throwable ignored) {}


        try {
            // I CHOOSE VIOLENCE TODAY
            if (!isT) {
                Collection<StackTraceElement[]> traceElements = Thread.getAllStackTraces().values();
                for (StackTraceElement[] elements: traceElements) {
                    for (StackTraceElement e: elements) {
                        if (e.getClassName().startsWith("org.tlauncher")) {
                            isT = true;
                            break;
                        }
                    }
                }
            }
        } catch (Exception ignored) {}
        return isT;
    }

    @Override
    public boolean clientSide() {
        return FMLLoader.getDist().isClient();
    }

    @Override
    public boolean developerMode() {
        return !FMLLoader.isProduction();
    }
}
