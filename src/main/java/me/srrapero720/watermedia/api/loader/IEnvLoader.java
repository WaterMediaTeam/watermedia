package me.srrapero720.watermedia.api.loader;

/**
 * This is specially designed for modded environments.
 * In vanilla Java or another type of modlauncher is not required
 */
public interface IEnvLoader {
    /**
     * Check if the current environment was a client
     * this is important to prevent run on clients and load any client-side class like GL11
     * @return if was client-side
     */
    boolean client();

    /**
     * Checks if the current environment was a development environment
     * We cannot consider offline-mode as a dev env because _cracked users_<br>
     * Is necessary search any special trigger like forge
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
     * Please DO NOT bypass
     * @return if the current environment is using TLauncher
     */
    boolean tlauncher();
}