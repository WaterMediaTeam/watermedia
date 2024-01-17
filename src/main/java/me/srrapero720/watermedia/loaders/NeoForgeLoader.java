package me.srrapero720.watermedia.loaders;

import me.srrapero720.watermedia.WaterMedia;
import me.srrapero720.watermedia.tools.exceptions.IllegalEnvironmentException;
import me.srrapero720.watermedia.tools.exceptions.IllegalTLauncherException;
import net.neoforge.fml.common.Mod;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.io.File;
import java.nio.file.Path;

import static me.srrapero720.watermedia.WaterMedia.LOGGER;

@Mod(WaterMedia.ID)
public class NeoForgeLoader implements ILoader {
    private static final Marker IT = MarkerManager.getMarker("NeoForge");
    private static final Path tmpPath = new File(System.getProperty("java.io.tmpdir")).toPath().toAbsolutePath().resolve(WaterMedia.ID);
    private static final Path processPath = new File("").toPath().toAbsolutePath();

    public NeoForgeLoader() {
        LOGGER.info(IT, "Preparing '{}' for {}", WaterMedia.ID, name().toUpperCase());
        try {
            if (tlcheck()) throw new IllegalTLauncherException();

            if (clientSide()) WaterMedia.prepare(this).start();
            else if (!developerMode()) throw new IllegalEnvironmentException();

        } catch (Throwable e) {
            throw new RuntimeException("Cannot run " + WaterMedia.NAME + " for (NEO)FORGE", e);
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