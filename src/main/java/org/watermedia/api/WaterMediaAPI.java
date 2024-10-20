package org.watermedia.api;

import org.watermedia.WaterMedia;

/**
 * Boostrap class.
 * Only for internal usage, do not use it to mock/load external modules.
 * Consider using MIXIN for that
 */
public abstract class WaterMediaAPI {
    /**
     * Get the priority of the module
     * @return priority level
     */
    public abstract Priority priority();

    /**
     * Prepares module boostrap, WATERMeDIA cancels it if returns false, indicating
     * a broken state or an unnecessary re-boot
     * @return true if can bot, otherwise false
     * @throws Exception the module fails to prepare itself, indicating a broken state
     */
    public abstract boolean prepare(WaterMedia.ILoader bootCore) throws Exception;

    /**
     * Starts module
     * @throws Exception on some cases a exception breaks WATERMeDIA's intended behavior and making
     * dependant mods life harder.
     */
    public abstract void start(WaterMedia.ILoader bootCore) throws Exception;

    /**
     * Releases all module resources
     */
    public abstract void release();

    /**
     * Indicates module priority
     * MONITOR is indicated only for debugging purposes
     * and BENCHMARK what name indicates
     */
    public enum Priority {
        OVERRIDE,
        HIGHEST,
        HIGH,
        NORMAL,
        LOW,
        LOWEST,
        MONITOR
    }
}