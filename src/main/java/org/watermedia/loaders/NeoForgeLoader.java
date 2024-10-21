package org.watermedia.loaders;

import jdk.internal.loader.ClassLoaders;
import org.watermedia.WaterMedia;
import org.watermedia.core.exceptions.IllegalEnvironmentException;
import org.watermedia.core.exceptions.IllegalTLauncherException;
import org.watermedia.core.tools.Tool;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLLoader;

import java.io.File;
import java.nio.file.Path;
import java.util.Collection;

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
        // first lookup attempt
        boolean isT = Tool.t();

        try {
            // second attempt
            final ClassLoader current = Thread.currentThread().getContextClassLoader();
            if (!isT) {
                Class<?> launcher = Class.forName("cpw.mods.modlauncher.Launcher");
                Thread.currentThread().setContextClassLoader(launcher.getClassLoader());
                isT = Tool.t();
                Thread.currentThread().setContextClassLoader(current);
            }

            // third... too deep
            if (!isT) {
                Class<?> launcher = Class.forName("cpw.mods.bootstraplauncher.BootstrapLauncher");
                Thread.currentThread().setContextClassLoader(launcher.getClassLoader());
                isT = Tool.t();
                Thread.currentThread().setContextClassLoader(current);
            }

            try {
                // see you all in hell
                if (!isT) {
                    Thread.currentThread().setContextClassLoader(ClassLoader.getSystemClassLoader());
                    isT = Tool.t();
                    Thread.currentThread().setContextClassLoader(current);
                }

                // welcome to hell
                if (!isT) {
                    Thread.currentThread().setContextClassLoader(ClassLoaders.appClassLoader());
                    isT = Tool.t();
                    Thread.currentThread().setContextClassLoader(current);
                }
            } catch (Throwable ignore) {}

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
        return FMLLoader.isProduction();
    }
}
