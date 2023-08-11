package me.srrapero720.watermedia;

import java.nio.file.Path;

public interface IMediaPreLoader {
    boolean isDev();
    boolean isClient();
    boolean isInstalled(String modId);
    ClassLoader getJarClassLoader();
    String getLoaderName();
    Path getWorkingDir();
    Path getTempDir();
    boolean isTLauncher();
}
