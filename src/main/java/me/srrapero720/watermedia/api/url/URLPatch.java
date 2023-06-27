package me.srrapero720.watermedia.api.url;

import me.srrapero720.watermedia.api.WaterMediaAPI;
import me.srrapero720.watermedia.api.url.patch.*;
import org.jetbrains.annotations.NotNull;

import java.net.URL;

public abstract class URLPatch {
    public static boolean init() {
        WaterMediaAPI.registerURLPatch(
                new YoutubePatch(),
                new TwitchPatch(),
                new KickPatch(),
                new DrivePatch(),
                new TwitterPatch());
        return true;
    }

    /**
     * Validates if URL can be processed by this URLPatch instance
     * @param url Valid URL to check
     * @return Can be built a static url
     */
    public abstract boolean isValid(@NotNull URL url);

    /**
     * Patch the URL
     * @param url URL to patch
     * @return static URL
     * @throws PatchingUrlException if URL is null or invalid in this patch
     */
    public String patch(@NotNull URL url) throws PatchingUrlException {
        if (!isValid(url)) throw new PatchingUrlException(url, new IllegalArgumentException("Attempt to build a invalid URL in a invalid Compat"));
        return null;
    }

    public static final class PatchingUrlException extends Exception {
        public PatchingUrlException(String url, Throwable t) { super("Failed to patch URL " + url, t); }
        public PatchingUrlException(URL url, Throwable t) { super("Failed to patch URL " + url.toString(), t); }
    }
}
