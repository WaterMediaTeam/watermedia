package me.srrapero720.watermedia.api.loader;

import java.nio.file.Path;

public interface IMediaLoader {
    String name();
    Path tmpPath();
    Path processPath();
    ClassLoader classLoader();
}