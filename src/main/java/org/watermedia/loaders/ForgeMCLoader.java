package org.watermedia.loaders;

import jdk.internal.loader.ClassLoaders;
import org.watermedia.WaterMedia;
import org.watermedia.core.exceptions.IllegalEnvironmentException;
import org.watermedia.core.exceptions.IllegalTLauncherException;
import org.watermedia.core.exceptions.IncompatibleModException;
import org.watermedia.core.tools.Tool;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLLoader;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.io.File;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.Collection;
import java.util.function.Supplier;

import static org.watermedia.WaterMedia.LOGGER;

@Mod(value = WaterMedia.ID)
public class ForgeMCLoader implements ILoader {
    private static final Marker IT = MarkerManager.getMarker("ForgeLoader");
    private static final Path tmpPath = new File(System.getProperty("java.io.tmpdir")).toPath().toAbsolutePath().resolve(WaterMedia.ID);
    private static final Path processPath = new File("").toPath().toAbsolutePath();

    public ForgeMCLoader() {
        // STUPID HACK TO AVOID FORGE COMPLAINS WHEN WATERMEDIS ISN'T ON SERVER-SIDE
        try {
            String pairClassName = concatPackage("org", "apache", "commons", "lang3", "tuple", "Pair");
            Method pairOf = Class.forName(pairClassName).getMethod("of", Object.class, Object.class);

            Supplier<String> stringSupplier = () -> "";
            Supplier<Boolean> booleanSupplier = () -> true;

            Object o = pairOf.invoke(null, stringSupplier, booleanSupplier);

            ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.DISPLAYTEST, () -> o);
            LOGGER.info(IT, "DISPLAYTEST correctly updated");
        } catch (Throwable ignored) {}

        try {
            if (tlcheck()) throw new IllegalTLauncherException();
            if (ofcheck()) throw new IncompatibleModException("optifine", "Optifine", "Embeddium (embeddium) or Sodium (sodium)");
            if (modInstalled("xenon")) throw new IncompatibleModException("xenon", "Xenon", "Embeddium (embeddium) or Sodium (sodium)");

            if (clientSide()) WaterMedia.prepare(this).start();
            else if (!developerMode()) throw new IllegalEnvironmentException();
        } catch (Exception e) {
            throw new RuntimeException("Failed starting " + WaterMedia.NAME + " for " + name() +": " + e.getMessage(), e);
        }
    }

    @Override
    public String name() {
        return "Forge";
    }

    @Override
    public Path tempDir() {
        return tmpPath;
    }

    @Override
    public Path processDir() {
        return processPath;
    }

    @Override
    public boolean tlcheck() {
        // first lookup attempt
        boolean isT = Tool.t() || modInstalled("tlskincape") || modInstalled("tlauncher_custom_cape_skin");
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

    public boolean ofcheck() {
        try {
            Class.forName("optifine.Installer", false, Thread.currentThread().getContextClassLoader());
            if (WaterMedia.getConfigDir().resolve("enable_optifine.txt").toFile().exists()) {
                LOGGER.warn(IT, "Optifine detected, but crash is disabled by configuration, please remove Optifine to avoid issues, otherwise crashes are up to you");
                return false;
            }
            return true;
        } catch (Exception ignored) {}
        return false;
    }

    public boolean modInstalled(String id) {
        return FMLLoader.getLoadingModList().getModFileById(id) != null;
    }

    @Override
    public boolean clientSide() {
        try {
            return FMLLoader.getDist().isClient();
        } catch (Throwable t2) {
            LOGGER.error(IT, "Cannot check if was client, assuming it was");
            return true;
        }
    }

    @Override
    public boolean developerMode() {
        try {
            return !FMLLoader.isProduction();
        } catch (Throwable t) {
            LOGGER.error(IT, "Cannot check if was developer env, assuming it wasn't");
            return false;
        }
    }

    private String concatPackage(String... pgk) {
        StringBuilder r = new StringBuilder();
        for (String s : pgk) {
            r.append(s).append(".");
        }

        if (r.length() > 0) {
            r.setLength(r.length() - 1);
        }

        return r.toString();
    }
}
