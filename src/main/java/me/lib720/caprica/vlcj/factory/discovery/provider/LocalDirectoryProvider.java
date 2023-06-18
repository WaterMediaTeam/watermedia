package me.lib720.caprica.vlcj.factory.discovery.provider;

import java.io.File;
import java.nio.file.Path;

import me.lib720.caprica.vlcj.factory.discovery.provider.DiscoveryDirectoryProvider;

public class LocalDirectoryProvider implements DiscoveryDirectoryProvider {
    private static Path rootPath = null;
    public static void init(Path ph) { rootPath = ph; }

    @Override
    public int priority() { return 5; }

    @Override
    public boolean supported() { return true; }

    @Override
    public String[] directories() {
        var vlc = rootPath != null ? rootPath : new File("").toPath();
        return new String[]{vlc.toAbsolutePath().toString()};
    }
}