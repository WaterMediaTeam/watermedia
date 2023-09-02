package me.srrapero720.watermedia.modloaders.forge;

import me.srrapero720.watermedia.WaterMedia;
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
    private ClassLoader CL;
    public ForgeModLoader() {
        LOGGER.info(IT, "Starting...");

        WaterMedia instance = WaterMedia.getInstance(this);
        try { WaterMedia.getInstance().envInit(new LegacyLoader()); } catch (Throwable ignored) {}
        try { WaterMedia.getInstance().envInit(new RusticLoader()); } catch (Throwable ignored) {}
        try { WaterMedia.getInstance().envInit(new WideLoader()); } catch (Throwable ignored) {}

        if (instance.env().client()) instance.init();
    }

    @Override
    public ClassLoader classLoader() { return (CL != null) ? CL : (CL = Thread.currentThread().getContextClassLoader()); }

    @Override
    public String name() { return NAME; }

    @Override
    public Path processPath() { return (PS != null) ? PS : (PS = new File("").toPath()); }

    @Override
    public Path tmpPath() {
        return (TMP != null) ? TMP : (TMP = new File(System.getProperty("java.io.tmpdir")).toPath().toAbsolutePath().resolve("watermedia"));
    }
}