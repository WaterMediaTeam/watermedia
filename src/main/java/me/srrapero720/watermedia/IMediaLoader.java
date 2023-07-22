package me.srrapero720.watermedia;

import java.nio.file.Path;

public interface IMediaLoader {
    boolean isDevEnv();
    boolean isClient();
    boolean isThisModPresent(String modId);
    ClassLoader getClassLoader();
    String getLoaderName();
    Path getGameDir();
    Path getTempDir();
    boolean isTLauncher();
}