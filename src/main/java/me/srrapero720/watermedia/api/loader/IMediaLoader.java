package me.srrapero720.watermedia.api.loader;

import java.nio.file.Path;

public interface IMediaLoader {

    ClassLoader getModuleClassLoader();
    String getName();
    Path getProcessDirectory();
    Path getTmpDirectory();

}