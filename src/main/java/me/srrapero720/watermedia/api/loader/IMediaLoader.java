package me.srrapero720.watermedia.api.loader;

import java.nio.file.Path;

/**
 * Required for Bootstrap
 */
public interface IMediaLoader {
    /**
     * Name should be without spaces or number and with capital letters
     * @return loader name
     */
    String name();

    /**
     * Tmp path is normally java.io.tmp directory; you can set up your custom TMP dir,
     * Just in case you need it (for cleaning or something else)
     * @return Path instance to tmp directory
     */
    Path tmpPath();

    /**
     * Absolute path to current process directory
     * normally is enough just use `new File("")` but is better do not assume is always it
     * @return Path instance to process directyr
     */
    Path processPath();

    /**
     * Classloader for this module
     * The main purpose is help to bootstrap find their own JAR resources
     * @deprecated It was designed to avoid fabric issues getting resources
     * these issues was fixed already, so is not longer needed a "classloader getter"
     * @return ClassLoader instance, preferred it was the JAR classloader
     */
    @Deprecated
    ClassLoader classLoader();
}