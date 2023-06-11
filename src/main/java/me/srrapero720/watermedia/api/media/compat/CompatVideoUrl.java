package me.srrapero720.watermedia.api.media.compat;

import me.srrapero720.watermedia.internal.util.ThreadUtil;
import org.jetbrains.annotations.NotNull;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public abstract class CompatVideoUrl {
    public static final Map<String, String> CACHE = new HashMap<>();
    public static CompatVideoUrl[] COMPAT_LIST = null;

    public static boolean init() {
        COMPAT_LIST = new CompatVideoUrl[] {
                new YoutubeCompat(),
                new TwitchCompat(),
                new KickCompat(),
                new DriveCompatVideoUrl(),
                new TwitterCompat(),
        };
        return true;
    }

    public static String compat(String url) {
        return ThreadUtil.tryAndReturn(defaultVar -> {
            for (var compat: COMPAT_LIST) if (compat.isValid(new URL(url))) return compat.build(new URL(url));
            return defaultVar;
        }, url);
    }

    protected synchronized static String storeUrl(URL url, String result) { CACHE.put(url.toString(), result); return result; }
    protected static String getStored(URL url) { return CACHE.get(url.toString()); }
    protected static boolean isStored(URL url) { return CACHE.containsKey(url.toString()); }
    public abstract boolean isValid(@NotNull URL url);
    public String build(@NotNull URL url) {
        if (!isValid(url)) throw new IllegalArgumentException("Attempt to build a invalid URL in a invalid Compat");
        return null;
    }
}
