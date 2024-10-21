package org.watermedia.loaders;

import org.watermedia.WaterMedia;

import java.io.File;
import java.nio.file.Path;

public class DSJarLoader implements ILoader {
    private static final Path TMP = new File(System.getProperty("java.io.tmpdir")).toPath().toAbsolutePath().resolve(WaterMedia.ID);
    private static final Path PROCESS = new File("").toPath().toAbsolutePath();

    public static void main(String... args) {

    }

    @Override
    public String name() {
        return "DSJL"; // DirectSource JarLoader
    }

    @Override
    public Path tempDir() {
        return TMP;
    }

    @Override
    public Path processDir() {
        return PROCESS;
    }

    @Override
    public boolean tlcheck() {
        return false;
    }

    @Override
    public boolean clientSide() {
        return true;
    }

    @Override
    public boolean developerMode() {
        return false;
    }
}
