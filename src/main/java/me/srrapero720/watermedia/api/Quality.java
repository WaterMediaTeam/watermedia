package me.srrapero720.watermedia.api;

/**
 * Quality preference.
 */
public enum Quality {
    /**
     * Unknown quality, highest by default, we don't know what is.
     */
    UNKNOWN,

    /**
     * If mrl has available 240p and 120p variants, patchers will select 120p
     */
    LOWEST,
    /**
     * If mrl has available 240p, 480p and 540p, patchers will select 480p.<br>
     * In case 480p isn't available, then 240p is the chosen one.
     */
    LOW,

    /**
     * If mrl has available 480p, 540p and 720p, patchers will select 540p.<br>
     * In case 540p isn't available, then 720p is the chosen one.
     */
    AVERAGE,
    /**
     * If mrl has available 720p, 1080p and 1440p, patchers will select 1440p.<br>
     * In case 1440p isn't available, then 1080p is the chosen one.<br><br>
     * <p>
     * If even 1080p or 1440p isn't available, 2K is the next option in case 4K exists
     * When 1080p to 2K was missing then fallbacks to 720p; in case it also doesn't exist, then it uses highest (4K)
     */
    HIGH,
    /**
     * If mrl has available 1440p, 2K and 4K variants, patchers will select 4K
     */
    HIGHEST;

    public static final Quality[] VALUES = values();

    public Quality getNext() {
        var ordinal = this.ordinal();
        if (ordinal >= VALUES.length) {
            return null;
        }
        return VALUES[ordinal];
    }

    public Quality getBack() {
        var ordinal = this.ordinal();
        if (ordinal == 0) {
            return null;
        }
        return VALUES[ordinal];
    }
}
