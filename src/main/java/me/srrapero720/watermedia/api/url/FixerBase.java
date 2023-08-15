package me.srrapero720.watermedia.api.url;

import java.net.URL;

public abstract class FixerBase {
    public String name() { return this.getClass().getSimpleName(); }

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
    public Result patch(URL url) throws FixingURLException {
        if (!isValid(url)) throw new FixingURLException(url, new IllegalArgumentException("Attempt to build a invalid URL in a invalid Compat"));
        return null;
    }

    public static final class FixingURLException extends Exception {
        public FixingURLException(String url, Throwable t) { super("Failed to fix URL " + url, t); }
        public FixingURLException(URL url, Throwable t) { super("Failed to fix URL " + url.toString(), t); }
    }

    public static class Result {
        public final URL url;
        public final boolean assumeStream;
        public final boolean assumeVideo;

        public Result(URL url, boolean assumeVideo, boolean assumeStream) {
            this.url = url;
            this.assumeVideo = assumeVideo;
            this.assumeStream = assumeStream;
        }
    }
}
