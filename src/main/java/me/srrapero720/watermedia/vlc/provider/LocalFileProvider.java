package me.srrapero720.watermedia.vlc.provider;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import me.srrapero720.watermedia.vlc.extractor.DLLExtractor;
import me.srrapero720.watermedia.vlc.extractor.LuaExtractor;
import me.lib720.caprica.vlcj.factory.discovery.provider.DiscoveryDirectoryProvider;

import static me.srrapero720.watermedia.WaterMedia.LOGGER;

public class LocalFileProvider implements DiscoveryDirectoryProvider {
    public final Path GAME_DIR;
    public LocalFileProvider(Path gameDir) {
        super();
        this.GAME_DIR = gameDir;
    }
    
    @Override public int priority() { return 5; }
    @Override public boolean supported() { return true; }
    @Override public String[] directories() {
        var vlc = GAME_DIR.resolve("cache/vlc/");
        LOGGER.info("Path from file extraction is '{}'", vlc.toAbsolutePath().toFile());
        var config = vlc.resolve("version.cfg");
        boolean shouldExtract;

        // Check if we need to update binaries
        try {
            if (Files.exists(config)) {
                String configVersion = Files.readString(config);
                // Add a way to get the current VLC version you want to install
                String expectedVersion = getCurrentVlcVersion();
                shouldExtract = !configVersion.equals(expectedVersion);
            } else {
                shouldExtract = true;
            }
        } catch (IOException e) {
            LOGGER.error("Could not read configuration file", e);
            shouldExtract = true;
        }

        if (shouldExtract) {
            LOGGER.warn("Extracting BINS before VLCJ attempt to load local files");
            for (var binary: DLLExtractor.values()) binary.extract();

            LOGGER.warn("Also extracting LUA scripts");
            for (var luac: LuaExtractor.values()) luac.extract();

            try {
                // Write the version of VLC binaries you just installed
                Files.writeString(config, getCurrentVlcVersion());
            } catch (IOException e) {
                LOGGER.error("Could not write to configuration file", e);
            }
        } else {
            LOGGER.warn("Extracted Bins detected... skip extracting");
        }

        return new String[] { vlc.toAbsolutePath().toString() };
    }

    private String getCurrentVlcVersion() { return "20230530"; } // Nightlies version: https://artifacts.videolan.org/vlc/nightly-win64-llvm/
}