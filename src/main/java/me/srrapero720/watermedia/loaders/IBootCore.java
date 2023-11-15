package me.srrapero720.watermedia.loaders;

import java.nio.file.Path;

/**
 * Required for Bootstrap
 */
public interface IBootCore {
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

    /**
     * Aggressive trigger for TLauncher
     * this was used by internal usages of WATERMeDIA.
     * Please DO NOT bypass
     * @return if the current environment is using TLauncher
     */
    boolean tlcheck();

    /**
     * Check if the current environment was a client
     * this is important to prevent run on clients and load any client-side class like GL11
     * @return if was client-side
     */
    boolean clientSide();

    /**
     * Checks if the current environment was a development environment
     * We cannot consider offline-mode as a dev env because _cracked users_<br>
     * Is necessary search any special trigger like forge
     * @return if was a development env
     */
    boolean developerMode();
}