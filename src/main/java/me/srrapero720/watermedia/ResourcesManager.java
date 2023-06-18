package me.srrapero720.watermedia;

import me.lib720.caprica.vlcj.factory.discovery.provider.LocalDirectoryProvider;
import me.srrapero720.watermedia.api.external.ThreadUtil;
import me.srrapero720.watermedia.vlc.BinManager;
import me.srrapero720.watermedia.vlc.LuaManager;
import me.srrapero720.watermedia.vlc.VLCManager;

import java.nio.file.Files;
import java.nio.file.Path;

import static me.srrapero720.watermedia.WaterMedia.LOGGER;

public class ResourcesManager {

    public static boolean init(Path rootPath) {
        var vlcPath = rootPath.resolve("cache/vlc/");
        var config = vlcPath.resolve("version.cfg");
        var version = ResourcesManager.getLocalVersion(config);
        LocalDirectoryProvider.init(vlcPath);

        // Check if we need to update binaries
        if (version == null || !version.equals(VLCManager.version)) {
            ResourcesManager.clear();
            ResourcesManager.extract();
            ResourcesManager.setLocalVersion(config);
        } else LOGGER.warn("VLC detected and match with the wrapped version");
        return true;
    }


    public static void clear() {
        LOGGER.warn("Running bin deletion from local files");
        for (var binary : BinManager.values()) binary.delete();
        LOGGER.warn("Bin deletion finished");

        LOGGER.warn("Running VLC LUAC script deletion from local files");
        for (var luac : LuaManager.values()) luac.delete();
        LOGGER.warn("VLC LUAC script deletion finished");
    }

    public static void extract() {
        LOGGER.warn("Running bin extraction from JAR to local files");
        for (var binary : BinManager.values()) binary.extract();
        LOGGER.warn("Windows bin extraction finished");

        LOGGER.warn("Running VLC LUAC script extraction from JAR to local files");
        for (var luac : LuaManager.values()) luac.extract();
        LOGGER.warn("VLC LUAC script extraction finished");
    }

    public static String getLocalVersion(Path from) {
        return ThreadUtil.tryAndReturn(defaultVar -> Files.exists(from) ? Files.readString(from) : defaultVar, null);
    }

    public static void setLocalVersion(Path from) {
        ThreadUtil.trySimple(() -> {
            if (!Files.exists(from.getParent())) Files.createDirectories(from.getParent());
            Files.writeString(from, VLCManager.version);
        }, e -> LOGGER.error("Could not write to configuration file", e));
    }
}
