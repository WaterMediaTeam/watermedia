package me.srrapero720.watermedia.core.util;

import java.nio.file.Path;

public interface IWaterMediaLoader {
    boolean isDevEnv();
    boolean isClient();
    boolean isThisModPresent(String modId);
    ClassLoader getClassLoader();
    String getLoaderName();
    Path getGameDir();
    Path getTempDir();
}