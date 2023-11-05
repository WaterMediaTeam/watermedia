package me.srrapero720.watermedia.modloaders;

/**
 * This is specially designed for modded environments.
 * In vanilla Java or another type of modlauncher is not required
 */
public interface IModLoader {
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

    /**
     * Should crash when is on server-side<br>
     * This method only exists by the stupid Fabric
     * @return if was a development env
     */
    boolean enforceCrash();

    /**
     * Attempt to find a mod on the environment
     * @param modId id of the mod
     * @return true when find it, otherwise false.
     */
    boolean modPresent(String modId);

    /**
     * Aggressive trigger for TLauncher
     * this was used by internal usages of WATERMeDIA.
     * Please DO NOT bypass
     * @return if the current environment is using TLauncher
     */
    boolean tlcheck();
}