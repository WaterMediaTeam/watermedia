package me.srrapero720.watermedia.api.loader;

import java.nio.file.Path;

public interface IMediaLoader {

    ClassLoader classLoader();
    String name();
    Path processPath();
    Path tmpPath();
}