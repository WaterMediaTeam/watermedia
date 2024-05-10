package me.srrapero720.watermedia.api;


import me.srrapero720.watermedia.api.math.MathAPI;
import me.srrapero720.watermedia.loaders.ILoader;

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
    public abstract boolean prepare(ILoader bootCore) throws Exception;

    /**
     * Starts module
     * @throws Exception on some cases a exception breaks WATERMeDIA's intended behavior and making
     * dependant mods life harder.
     */
    public abstract void start(ILoader bootCore) throws Exception;

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
        HIGHEST,
        HIGH,
        NORMAL,
        LOW,
        LOWEST,
        MONITOR,
        BENCHMARK
    }

    /**
     * Returns the color of a specified level in range of 1 ~ 255
     * @param a alpha level
     * @param r red color
     * @param g green color
     * @param b blue color
     * @deprecated IMPORANT: this method a bouncer of {@link MathAPI#argb(int, int, int, int)}. Please switch as soon as possible
     * @return color int
     */
    @Deprecated(forRemoval = true)
    public static int math_colorARGB(int a, int r, int g, int b) {
        return MathAPI.argb(a, r, g, b);
    }
}