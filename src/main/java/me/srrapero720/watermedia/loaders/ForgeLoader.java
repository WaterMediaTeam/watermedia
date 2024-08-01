package me.srrapero720.watermedia.loaders;

import me.srrapero720.watermedia.WaterMedia;
import me.srrapero720.watermedia.core.exceptions.IllegalEnvironmentException;
import me.srrapero720.watermedia.core.exceptions.IllegalTLauncherException;
import me.srrapero720.watermedia.core.exceptions.IncompatibleModException;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLLoader;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.io.File;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.function.Supplier;

import static me.srrapero720.watermedia.WaterMedia.LOGGER;

@Mod(value = WaterMedia.ID)
public class ForgeLoader implements ILoader {
    private static final Marker IT = MarkerManager.getMarker("ForgeLoader");
    private static final Path tmpPath = new File(System.getProperty("java.io.tmpdir")).toPath().toAbsolutePath().resolve(WaterMedia.ID);
    private static final Path processPath = new File("").toPath().toAbsolutePath();

    public ForgeLoader() {
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
            if (modInstalled("xenon")) throw new IncompatibleModException("xenon", "Xenon");

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
        boolean tllike = false;
        try {
            tllike = modInstalled("tlskincape") || modInstalled("tlauncher_custom_cape_skin");
        } catch (Throwable t2) {
            LOGGER.error(IT, "Cannot check if TL was installed");
        }

        String f = processDir().toAbsolutePath().toString().toLowerCase();
        boolean tlauncher = f.contains("tlauncher");
        boolean atlauncher = f.contains("atlauncher");
        boolean sklauncher = f.contains("skcraftlauncher");
        boolean keventlauncher = f.contains("keventlauncher");

        return tllike || (tlauncher && !atlauncher && !sklauncher && !keventlauncher);
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
