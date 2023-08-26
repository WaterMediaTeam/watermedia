package me.srrapero720.watermedia.api.url;

import java.awt.*;
import java.net.URL;

public abstract class URLFixer {
    public String name() { return this.getClass().getSimpleName(); }

    public abstract String platform();

    /**
     * Validates if URL can be processed by this URLPatch instance
     * @param url Valid URL to check
     * @return Can be built a static url
     */
    public abstract boolean isValid(URL url);

    /**
     * Patch the URL
     * @param url URL to patch
     * @return static URL
     * @throws FixingURLException if URL is null or invalid in this patch
     */
    public Result patch(URL url, Quality prefQuality) throws FixingURLException {
        if (!isValid(url)) throw new FixingURLException(url, new IllegalArgumentException("Attempt to build a invalid URL in a invalid Compat"));
        return null;
    }

    public static final class FixingURLException extends Throwable {
        public FixingURLException(String url, Throwable t) { super("Failed to fix URL " + url, t); }
        public FixingURLException(URL url, Throwable t) { super("Failed to fix URL " + url.toString(), t); }
    }

    public static class Result {
        public final URL url;
        public final Quality quality;
        public final boolean assumeStream;
        public final boolean assumeVideo;

        public Result(URL url, boolean assumeVideo, boolean assumeStream) {
            this.url = url;
            this.quality = null;
            this.assumeVideo = assumeVideo;
            this.assumeStream = assumeStream;
        }
    }

    public enum Quality {
        LOWEST(),
        LOW(),
        MIDDLE(),
        HIGH(),
        HIGHEST(),
    }
}