package me.srrapero720.watermedia.vlc.extras;

import java.nio.file.Files;
import java.nio.file.Path;

import me.srrapero720.watermedia.internal.util.ThreadUtil;
import me.srrapero720.watermedia.vlc.VLCManager;
import me.srrapero720.watermedia.vlc.extractor.DLLExtractor;
import me.srrapero720.watermedia.vlc.extractor.LuaExtractor;
import me.lib720.caprica.vlcj4.factory.discovery.provider.DiscoveryDirectoryProvider;

import static me.srrapero720.watermedia.WaterMedia.LOGGER;

public record LocalFileProvider(Path rootPath) implements DiscoveryDirectoryProvider {

    @Override
    public int priority() { return 5; }

    @Override
    public boolean supported() { return true; }

    @Override
    public String[] directories() {
        var vlc = rootPath.resolve("cache/vlc/");
        var config = vlc.resolve("version.cfg");
        var version = this.getLocalVersion(config);

        // Check if we need to update binaries
        if (version != null && !version.equals(VLCManager.getVersion())) {
            this.extract();
            this.setLocalVersion(config);
        } else {
            LOGGER.warn("VLC extracted match with the wrapped version. Skipping...");
        }

        return new String[]{vlc.toAbsolutePath().toString()};
    }

    private void extract() {
        LOGGER.warn("Running Windows bin extraction from JAR to local files");
        for (var binary : DLLExtractor.values()) binary.extract();
        LOGGER.warn("Windows bin extraction finished");

        LOGGER.warn("Running VLC LUAC script extraction from JAR to local files");
        for (var luac : LuaExtractor.values()) luac.extract();
        LOGGER.warn("VLC LUAC script extraction finished");
    }

    private String getLocalVersion(Path from) {
        return ThreadUtil.tryAndReturn(defaultVar -> Files.exists(from) ? Files.readString(from) : defaultVar, null);
    }
    private void setLocalVersion(Path from) {
        ThreadUtil.trySimple(() -> Files.writeString(from, VLCManager.getVersion()), e -> LOGGER.error("Could not write to configuration file", e));
    }
}