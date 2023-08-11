package me.srrapero720.watermedia;

import java.nio.file.Path;

public interface IMediaLoader {

    ClassLoader getModuleClassLoader();
    String getName();
    Path getProcessDirectory();
    Path getTmpDirectory();

}