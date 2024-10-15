package org.watermedia.api.media.meta;

/**
 * Quality preference.
 */
public enum MediaQuality {
    /**
     * Qualities same or below 240p threshold
     */
    LOWEST(240),

    /**
     * Qualities same or below 480p threshold
     */
    LOWER(480),

    /**
     * Qualities below 540p threshold
     */
    LOW(540),

    /**
     * Qualities same or below 720p threshold
     */
    AVERAGE(720),

    /**
     * Qualities same or below 1080p threshold
     */
    HIGH(1080),

    /**
     * Qualities same or below 2K threshold
     */
    HIGHER(1440),

    /**
     * Qualities same or below 4K threshold
     */
    HIGHEST(2160);

    private final int threadshool;
    MediaQuality(int threshold) {
        this.threadshool = threshold;
    }

    public static final MediaQuality[] VALUES = values();

    public static MediaQuality calculate(int width) { // TODO: evaluate height for tiktok reels
        if (width >= LOWEST.threadshool  && width < LOWER.threadshool) {
            return LOWEST;
        } else if (width >= LOWER.threadshool && width < LOW.threadshool) {
            return LOWER;
        } else if (width >= LOW.threadshool && width < AVERAGE.threadshool) {
            return LOW;
        } else if (width >= AVERAGE.threadshool && width < HIGH.threadshool) {
            return AVERAGE;
        } else if (width >= HIGH.threadshool && width < HIGHER.threadshool) {
            return HIGH;
        } else if (width >= HIGHER.threadshool && width < HIGHEST.threadshool) {
            return HIGHER;
        } else {
            return HIGHEST;
        }
    }

    public MediaQuality getNext() {
        var ordinal = this.ordinal();
        if (ordinal >= VALUES.length) {
            return null;
        }
        return VALUES[ordinal];
    }

    public MediaQuality getBack() {
        var ordinal = this.ordinal();
        if (ordinal == 0) {
            return null;
        }
        return VALUES[ordinal];
    }
}
