package me.srrapero720.watermedia.api;

import me.lib720.caprica.vlcj.factory.MediaPlayerFactory;
import me.lib720.caprica.vlcj.factory.discovery.NativeDiscovery;
import me.srrapero720.watermedia.api.video.patch.AbstractURLPatch;
import me.srrapero720.watermedia.api.video.players.VideoLanPlayer;
import me.srrapero720.watermedia.api.external.ThreadUtil;
import me.srrapero720.watermedia.vlc.VLCManager;

import static me.srrapero720.watermedia.WaterMedia.LOGGER;

import java.net.URL;
import java.util.Arrays;

public final class WaterMediaAPI {

    /**
     * 1 seconds in Minecraft equals 20 ticks
     * 20x50 equals 1000ms (1 sec)
     *
     * @param ticks Minecraft Ticks
     * @return ticks converted to MS
     */
    public static long gameTicksToMs(int ticks) { return ticks * 50L; }

    /**
     * 1000ms (1 sec) equals 20 ms in Minecraft
     * 1000/50 equals 20 Ticks (1 sec)
     *
     * @param ms Time in milliseconds
     * @return Milliseconds converted to Ticks
     */
    public static int msToGameTicks(long ms) { return (int) (ms / 50L); }

    /**
     * This method is used by default on {@link VideoLanPlayer#start(CharSequence, String[])}
     * Is not recommended to use on external methods
     * @param url Media URL to patch
     * @return Media URL patched to be fully compatible with VLC (static resource)
     */
    public static String patchNonStaticUrl(String url) {
        return ThreadUtil.tryAndReturn(defaultVar -> {
            for (var compat: AbstractURLPatch.URL_PATCHERS) if (compat.isValid(new URL(url))) return compat.build(new URL(url));
            return defaultVar;
        }, e -> LOGGER.error("Exception occurred trying to run patchNonStaticUrl", e), url);
    }

    /**
     * Check if VLC is loaded and ready to be used on {@link VideoLanPlayer} or to make
     * a new {@link MediaPlayerFactory} instance
     * @return if is reddy or not
     */
    public static boolean isVLCReady() { return VLCManager.getDefaultFactory() != null; }

    /**
     * Use it at your own risk
     * By default this method makes a ReleaseHook to release everything after close Minecraft
     * Suggestion: Use the same VLC arguments for logging but with other filename
     * Example: <pre> "--logfile", "logs/vlc/mymod-latest.log",</pre>
     * @param vlcArgs arguments to make another VLC instance
     * @return a PlayerFactory to create custom VLC players. {@link VideoLanPlayer} can accept factory for new instances
     */
    public static MediaPlayerFactory newVLCPlayerFactory(String[] vlcArgs) {
        var discovery = new NativeDiscovery();
        if (discovery.discover()) {
            var factory = new MediaPlayerFactory(discovery, vlcArgs);
            LOGGER.info("New instance of VLC loaded from '{}' with the next args:\n{}", discovery.discoveredPath(), Arrays.toString(vlcArgs));
            Runtime.getRuntime().addShutdownHook(new Thread(factory::release));
            return factory;
        }

        LOGGER.error("VLC was not found on your system.");
        return null;
    }
}
