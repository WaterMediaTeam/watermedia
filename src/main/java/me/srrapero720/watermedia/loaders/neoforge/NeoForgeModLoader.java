package me.srrapero720.watermedia.loaders.neoforge;

import me.srrapero720.watermedia.WaterMedia;
import me.srrapero720.watermedia.loaders.IBootCore;
import me.srrapero720.watermedia.tools.exceptions.IllegalEnvironmentException;
import me.srrapero720.watermedia.tools.exceptions.IllegalTLauncherException;
import net.neoforge.fml.common.Mod;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.io.File;
import java.nio.file.Path;

import static me.srrapero720.watermedia.WaterMedia.LOGGER;

@Mod(WaterMedia.ID)
public class NeoForgeModLoader implements IBootCore {
    private static final Marker IT = MarkerManager.getMarker("ForgeModLoader");
    private static final Path tmpPath = new File(System.getProperty("java.io.tmpdir")).toPath().toAbsolutePath().resolve("watermedia");
    private static final Path processPath = new File("").toPath().toAbsolutePath();

    public NeoForgeModLoader() {
        LOGGER.info(IT, "Preparing '{}' for (neo){}", WaterMedia.ID, name().toUpperCase());
        try {
            if (tlcheck()) throw new IllegalTLauncherException();

            if (clientSide()) WaterMedia.create(this).init();
            else if (developerMode()) throw new IllegalEnvironmentException();

        } catch (Exception e) {
            throw new RuntimeException("Cannot run " + WaterMedia.NAME + " for FORGE", e);
        }
    }

    @Override
    public String name() {
        return "NeoFroge";
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
        return false; // TODO
    }

    @Override
    public boolean clientSide() {
        return false; // TODO
    }

    @Override
    public boolean developerMode() {
        return false; // TODO
    }
}