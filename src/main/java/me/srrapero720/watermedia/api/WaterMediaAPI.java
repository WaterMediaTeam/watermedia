package me.srrapero720.watermedia.api;

import me.lib720.caprica.vlcj.factory.MediaPlayerFactory;
import me.lib720.caprica.vlcj.factory.discovery.NativeDiscovery;
import me.srrapero720.watermedia.Util;
import me.srrapero720.watermedia.api.images.RenderablePicture;
import me.srrapero720.watermedia.api.url.URLPatch;
import me.srrapero720.watermedia.api.url.patch.*;
import me.srrapero720.watermedia.api.video.VideoLanPlayer;
import me.srrapero720.watermedia.api.external.ThreadUtil;
import me.srrapero720.watermedia.core.videolan.VideoLAN;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import static me.srrapero720.watermedia.WaterMedia.LOGGER;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class WaterMediaAPI {
    private static final Marker IT = MarkerFactory.getMarker("WaterMediaAPI");
    public static final RenderablePicture LOADING_GIF = new RenderablePicture(Util.getGifFromResources("/images/loading.gif"));
    public static final RenderablePicture VLC_FAILED = new RenderablePicture(Util.getImageFromResources("/images/vlc_win_failed.png"));
    public static final RenderablePicture VLC_FAILED_INSTALL = new RenderablePicture(Util.getImageFromResources("/images/vlc_otheros_failed.png"));

    private static final List<URLPatch> URL_PATCHERS = new ArrayList<>(List.of(
            new YoutubePatch(),
            new TwitchPatch(),
            new KickPatch(),
            new DrivePatch(),
            new TwitterPatch(),
            new OnedrivePatch(),
            new DropboxPatch())
    );

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
     * Check if any String is a valid URL
     * @param url the URL in a string
     * @return if is valid.
     */
    public static boolean isURLValid(String url) { return ThreadUtil.tryAndReturn(defaultVar -> { new URL(url); return true; }, false); }


    /**
     * Creates your own URLPatch and register it to WaterMediaAPI
     * @param patch All patches you want to Use
     */
    public static void registerURLPatch(@NotNull URLPatch ...patch) {
        for (final URLPatch p: patch) {
            LOGGER.warn(IT, "Registered new URLPatch: {}", p.getClass().getSimpleName());
            URL_PATCHERS.add(p);
        }
    }

    /**
     * This method is used by default on {@link VideoLanPlayer#start(CharSequence, String[])}
     * Is not recommended external usages
     * @param url Media URL to patch
     * @return Media URL patched to be fully compatible with VLC (static resource)
     */
    public static String urlPatch(String url) {
        return ThreadUtil.tryAndReturn(defaultVar -> {
            for (var compat: URL_PATCHERS) if (compat.isValid(new URL(url))) return compat.patch(new URL(url));
            return defaultVar;
        }, e -> LOGGER.error(IT, "Exception occurred trying to run patchNonStaticUrl", e), url);
    }

    /**
     * Use your own VLCArgs at your own risk
     * By default this method makes a ReleaseHook to release everything after close Minecraft
     * Suggestion: Use the same VLC arguments for logging but with other filename
     * Example: <pre> "--logfile", "logs/vlc/mymod-latest.log",</pre>
     * @param vlcArgs arguments to make another VLC instance
     * @return a PlayerFactory to create custom VLC players. {@link VideoLanPlayer} can accept factory for new instances
     */
    public static MediaPlayerFactory createVLCFactory(String[] vlcArgs) {
        var discovery = new NativeDiscovery();
        if (discovery.discover()) {
            var factory = new MediaPlayerFactory(discovery, vlcArgs);
            LOGGER.info(IT, "New instance of VLC loaded from '{}' with the next args:\n{}", discovery.discoveredPath(), Arrays.toString(vlcArgs));
            Runtime.getRuntime().addShutdownHook(new Thread(factory::release));
            return factory;
        }

        LOGGER.error(IT, "VLC was not found on your system.");
        return null;
    }

    /**
     * Check if VLC is loaded and ready to be used on {@link VideoLanPlayer} or to make
     * a new {@link MediaPlayerFactory} instance
     * @return if is reddy or not
     */
    public static boolean isVLCReady() { return VideoLAN.defaultFactory() != null; }
}
