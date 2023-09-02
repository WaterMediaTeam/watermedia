package me.lib720.caprica.vlcj;

import me.lib720.caprica.vlcj.factory.discovery.provider.CustomDirectoryProvider;

import java.nio.file.Path;

public class VideoLan4J {
    private static final ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();

    public static void init(Path customDir) {
        CustomDirectoryProvider.init(customDir);
    }

    public static void native$checkClassLoader() {
        Thread t = Thread.currentThread();
        if (t.getContextClassLoader() == null) t.setContextClassLoader(contextClassLoader);
    }
}