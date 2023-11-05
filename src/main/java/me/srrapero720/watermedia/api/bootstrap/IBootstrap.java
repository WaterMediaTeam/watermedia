package me.srrapero720.watermedia.api.bootstrap;

import java.nio.file.Path;

/**
 * Required for Bootstrap
 */
public interface IBootstrap {
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
    Path tempDir();

    /**
     * Absolute path to current process directory
     * normally is enough just use `new File("")` but is better do not assume is always it
     * @return Path instance to process directory
     */
    Path processDir();
}