package me.srrapero720.watermedia.api.loader;

/**
 * This is specially designed for modded environments.
 * In vanilla Java or... another type of modlauncher is not required
 */
public interface IEnvLoader {
    /**
     * Checks for the current environments.
     * @return if was client-side
     */
    boolean client();

    /**
     * Checks for any special trigger for development environment.
     * @return if was a development env
     */
    boolean development();

    /**
     * Checks if a mod is installed using the mod id
     * This can be addressed using FML or FabricLoader
     * @param modId id of the mod
     * @return if was installed
     */
    boolean installed(String modId);

    /**
     * Aggressive trigger for TLauncher
     * this was used by internal usages of WATERMeDIA.
     * @return if the current environment is using TLauncher
     */
    boolean tlauncher();
}