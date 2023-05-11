package me.srrapero720.watermedia;

import me.srrapero720.watermedia.vlc.VLC;

/**
 * Here goes all required methods to load WATERMeDIA
 * This is loader-safe, because each mod-loader is a IWaterMedia.
 */
@SuppressWarnings("BooleanMethodIsAlwaysInverted")
public class WaterMedia {
    private static volatile WaterMedia self;
    private static RuntimeException state;

    /**
     * Get a WaterMedia ready instance
     * If isn't loaded runs <pre>Watermedia.load()</pre>
     * When loading state returned is false throws a IllegalStateException with the fail cause
     *
     * @return Self instance
     * @throws IllegalStateException if instance is null and if loading state failed
     */
    public static WaterMedia get() {
        var instance = self;
        if (instance != null) return instance;

        synchronized(WaterMedia.class) {
            if (self == null && !load()) throw new IllegalMediaLoadingState();
            return self;
        }
    }

    /**
     * This method tries to load libraries. If something is broken instead of
     * throw errors returns the loaded state. use <pre>Watermedia.get()</pre> also
     * tries to load everything if isn't loaded, but if cant load anything then throws a IllegalStateException
     * @return Library load state (true if is loaded)
     */
    private static boolean load() {
        if (self != null) return true;
        self = new WaterMedia();

        if (!VLC.load()) return false;
//        if (!LavaPlayer.load()) return false;

        // REST OF CODE
        return true;
    }

    void onFakeEvent() {
        // Como deben verse los handlers para X cosa
    }

    public static final class IllegalMediaLoadingState extends IllegalStateException {
        public IllegalMediaLoadingState() {
            super("Failed to load libraries from WaterMedia", state);
        }
    }
}