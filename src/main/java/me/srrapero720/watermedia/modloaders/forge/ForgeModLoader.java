package me.srrapero720.watermedia.modloaders.forge;

import me.lib720.watermod.concurrent.ThreadCore;
import me.srrapero720.watermedia.WaterMedia;
import me.srrapero720.watermedia.api.loader.IEnvLoader;
import me.srrapero720.watermedia.api.loader.IMediaLoader;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.io.File;
import java.nio.file.Path;

import static me.srrapero720.watermedia.WaterMedia.LOGGER;

@Mod(modid = WaterMedia.ID, acceptableRemoteVersions = "*", value = WaterMedia.ID)
public class ForgeModLoader implements IMediaLoader {
    private static final Marker IT = MarkerManager.getMarker("ForgeModLoader");
    private static final String NAME = "Forge";

    private Path PS;
    private Path TMP;

    public ForgeModLoader() {
        LOGGER.info(IT, "Starting...");

        WaterMedia instance = WaterMedia.getInstance(this);
        try {
            IEnvLoader loader = new RusticLoader();
            
            // Stress test
            loader.client();
            loader.installed("jei");
            loader.development();
            loader.tlauncher();

            instance.envInit(loader);

            ThreadCore.thread(() -> {
                LOGGER.warn(IT, "Rustic version detected, running ASYNC bootstrap");
                if (instance.env().client()) instance.init();
            });
            return;
        } catch (Throwable ignored) {}
        try {
            IEnvLoader loader = new CurrentLoader();
            
            // Stress test
            loader.client();
            loader.installed("jei");
            loader.development();
            loader.tlauncher();

            instance.envInit(loader);
        } catch (Throwable ignored) {}
        try {
            IEnvLoader loader = new FutureLoader();

            // Stress test
            loader.client();
            loader.installed("jei");
            loader.development();
            loader.tlauncher();

            instance.envInit(loader);
        } catch (Throwable ignored) {}

        if (instance.env().client()) instance.init();
    }

    @Override
    public String name() { return NAME; }

    @Override
    public Path processPath() { return (PS != null) ? PS : (PS = new File("").toPath()); }

    @Override
    public Path tmpPath() {
        return (TMP != null) ? TMP : (TMP = new File(System.getProperty("java.io.tmpdir")).toPath().toAbsolutePath().resolve("watermedia"));
    }
}