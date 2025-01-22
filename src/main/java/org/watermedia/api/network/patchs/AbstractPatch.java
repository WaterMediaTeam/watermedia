package org.watermedia.api.network.patchs;

import java.net.URI;


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
public abstract class AbstractPatch {

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
     * @param uri Valid URL to check
     * @return Can be built a static uri
     */
    public abstract boolean isValid(URI uri);

    /**
     * Patch the URL
     * @param uri URL to patch
     * @return static URL
     * @throws FixingURLException if URL is null or invalid in this patch
     */
    public Result patch(URI uri, Quality prefQuality) throws FixingURLException {
        if (!isValid(uri)) throw new FixingURLException(uri, new IllegalArgumentException("Attempt to build a invalid URL in a invalid Compat"));
        return null;
    }

    @Override
    public String toString() {
        return name();
    }

    public static final class FixingURLException extends Exception {
        public FixingURLException(String url, Throwable t) { super("Failed to fix URL " + url, t); }
        public FixingURLException(URI url, Throwable t) { super("Failed to fix URL " + url.toString(), t); }
    }

    public static interface FallbackResult {
        Result compute(URI uri) throws FixingURLException;
    }

    public static class Result {
        private static final FallbackResult DEFAULT_FALLBACK = uri -> null;

        public final URI uri;
        public URI audioUrl;
        public final Quality quality;
        public final FallbackResult fallbackResult;
        public final boolean assumeStream;
        public final boolean assumeVideo;

        public Result(URI uri, boolean assumeVideo, boolean assumeStream) {
            this(uri, assumeVideo, assumeStream, DEFAULT_FALLBACK);
        }

        public Result(URI uri, boolean assumeVideo, boolean assumeStream, FallbackResult fallback) {
            this.uri = uri;
            this.quality = null;
            this.assumeVideo = assumeVideo;
            this.assumeStream = assumeStream;
            this.fallbackResult = fallback;
        }

        public Result setAudioTrack(URI url) {
            this.audioUrl = url;
            return this;
        }

        @Override
        public String toString() {
            return "Result{" +
                    "url=" + uri +
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