package me.srrapero720.watermedia.loader;

/**
 * New jar module attached to WATERMeDIA
 */
public interface IModule {
    /**
     * Provides the module name to identify it internally on crash reports
     * @return module name
     */
    String name();

    /**
     * Provides the classloader of the module (in case environment do odd things on jar modules)
     * @return module classloader
     */
    ClassLoader classLoader();

}
