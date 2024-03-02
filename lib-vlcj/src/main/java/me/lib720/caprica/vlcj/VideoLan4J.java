package me.lib720.caprica.vlcj;


import me.lib720.caprica.vlcj.factory.discovery.provider.CustomDirectoryProvider;

import java.nio.file.Path;

public class VideoLan4J {
    public static void init(Path customDir) {
        CustomDirectoryProvider.init(customDir);
    }

    public static void native$checkClassLoader(ClassLoader classLoader) {
        Thread t = Thread.currentThread();
        if (t.getContextClassLoader() == null) t.setContextClassLoader(classLoader);
    }
}