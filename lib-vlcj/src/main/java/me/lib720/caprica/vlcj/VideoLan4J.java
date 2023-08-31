package me.lib720.caprica.vlcj;

import java.nio.file.Path;

public class VideoLan4J {
    private static final ClassLoader contextClasssLoader = Thread.currentThread().getContextClassLoader();
    private static Path customDir;

    public static void init(Path customDir) {
        VideoLan4J.customDir = customDir;
    }

    public static void native$checkClassLoader() {
        Thread t = Thread.currentThread();
        if (t.getContextClassLoader() == null) t.setContextClassLoader(contextClasssLoader);
    }

    public static Path getCustomDir() {
        return customDir;
    }
}