package me.srrapero720.watermedia.api.url.fixers;

import java.net.URL;


/**
 * Base class for URL Fixing...
 * To make your own fixer, you should use services.<br>
 * Create a file in:
 * <code>resources/META-INF/services/</code><br>
 * named:
 * <code>me.srrapero720.watermedia.api.url.fixers.URLFixer</code>
 * and put inside the entire package to your fixer.
 * WATERMeDIA loads it by yourself ;)
 */
public abstract class URLFixer {

    /**
     * used for loggers
     * @return class name
     */
    public String name() { return this.getClass().getSimpleName(); }

    /**
     * Name of the platform used for this fixer
     * @return platform name
     */
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

    @Override
    public String toString() {
        return name();
    }

    public static final class FixingURLException extends Exception {
        public FixingURLException(String url, Throwable t) { super("Failed to fix URL " + url, t); }
        public FixingURLException(URL url, Throwable t) { super("Failed to fix URL " + url.toString(), t); }
    }

    public static class Result {
        public final URL url;
        public URL audioUrl;
        public final Quality quality;
        public final boolean assumeStream;
        public final boolean assumeVideo;

        public Result(URL url, boolean assumeVideo, boolean assumeStream) {
            this.url = url;
            this.quality = null;
            this.assumeVideo = assumeVideo;
            this.assumeStream = assumeStream;
        }

        public Result setAudioTrack(URL url) {
            this.audioUrl = url;
            return this;
        }

        @Override
        public String toString() {
            return "Result{" +
                    "url=" + url +
                    ", audioUrl=" + audioUrl +
                    ", quality=" + quality +
                    ", assumeStream=" + assumeStream +
                    ", assumeVideo=" + assumeVideo +
                    '}';
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