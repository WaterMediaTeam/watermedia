package me.srrapero720.watermedia.api.media.patch;

import org.jetbrains.annotations.NotNull;

import java.net.URL;

public abstract class BaseVideoPatch {
    public static BaseVideoPatch[] COMPAT_LIST = null;

    public static boolean init() {
        COMPAT_LIST = new BaseVideoPatch[] {
                new YoutubePatch(),
                new TwitchPatch(),
                new KickPatch(),
                new DrivePatch(),
                new TwitterPatch(),
        };
        return true;
    }
    public abstract boolean isValid(@NotNull URL url);
    public String build(@NotNull URL url) throws Exception {
        if (!isValid(url)) throw new IllegalArgumentException("Attempt to build a invalid URL in a invalid Compat");
        return null;
    }

    public static final class PatchingUrlException extends Exception {
        public PatchingUrlException(String url, Throwable t) { super("Failed to patch URL " + url, t); }
    }
}
