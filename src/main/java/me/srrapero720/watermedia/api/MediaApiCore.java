package me.srrapero720.watermedia.api;

import me.srrapero720.watermedia.api.media.patch.BaseVideoPatch;
import me.srrapero720.watermedia.api.media.players.VideoLanPlayer;
import me.srrapero720.watermedia.threads.ThreadUtil;

import static me.srrapero720.watermedia.WaterMedia.LOGGER;

import java.net.URL;

public final class MediaApiCore {

    /**
     * This method is used by default on {@link VideoLanPlayer#start(CharSequence, String[])}
     * Is not recommended to use on external methods
     * @param url Media URL to patch
     * @return Media URL patched to be fully compatible with VLC (static resource)
     */
    public static String patchNonStaticUrl(String url) {
        return ThreadUtil.tryAndReturn(defaultVar -> {
            for (var compat: BaseVideoPatch.COMPAT_LIST) if (compat.isValid(new URL(url))) return compat.build(new URL(url));
            return defaultVar;
        }, e -> LOGGER.error("Exception occurred trying to run patchNonStaticUrl", e), url);
    }

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
}
