package org.watermedia.loaders;

import jdk.internal.loader.ClassLoaders;
import net.fabricmc.api.ModInitializer;
import org.watermedia.WaterMedia;
import org.watermedia.core.exceptions.IllegalTLauncherException;
import org.watermedia.core.tools.Tool;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.io.File;
import java.nio.file.Path;
import java.util.Collection;

public class FavricMCLoader implements ClientModInitializer, ModInitializer, ILoader {
    private static final Marker IT = MarkerManager.getMarker("FabricLoader");

    private final Path tempPath = new File(System.getProperty("java.io.tmpdir")).toPath().toAbsolutePath().resolve("watermedia");
    private final Path processPath = FabricLoader.getInstance().getGameDir();

    @Override
    public void onInitializeClient() {
        try {
            if (tlcheck()) throw new IllegalTLauncherException();

            if (clientSide()) WaterMedia.prepare(this).start();
        } catch (Exception e) {
            throw new RuntimeException("Failed starting " + WaterMedia.NAME + " for " + name() + ": " + e.getMessage(), e);
        }
    }

    @Override public String name() { return "Fabric"; }
    @Override public Path processDir() { return processPath; }
    @Override public Path tempDir() { return tempPath; }
    @Override public boolean developerMode() { return FabricLoader.getInstance().isDevelopmentEnvironment(); }
    @Override public boolean clientSide() { return FabricLoader.getInstance().getEnvironmentType().equals(EnvType.CLIENT); }

    @Override
    public boolean tlcheck() {
        // TLSKINCAPE VALIDATION
        boolean cape = FabricLoader.getInstance().isModLoaded("tlskincape") || FabricLoader.getInstance().isModLoaded("tlauncher_custom_cape_skin");

        // first lookup attempt
        boolean isT = Tool.t() || cape;

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
    public void onInitialize() {
        WaterMedia.LOGGER.warn("Stub running successfully");
    }
}
