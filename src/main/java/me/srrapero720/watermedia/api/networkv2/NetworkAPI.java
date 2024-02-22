package me.srrapero720.watermedia.api.networkv2;

import me.lib720.watermod.safety.TryCore;
import me.srrapero720.watermedia.api.url.UrlAPI;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.net.URI;
import java.net.URL;
import java.util.ServiceLoader;

import static me.srrapero720.watermedia.WaterMedia.LOGGER;

public class NetworkAPI {
    public static final Marker IT = MarkerManager.getMarker(UrlAPI.class.getSimpleName());
    public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/112.0.0.0 Safari/537.36 Edg/112.0.1722.68";
    private static final ServiceLoader<URLPatcher> URL_PATCHES = ServiceLoader.load(URLPatcher.class);

    public static DynamicURL patchURL(DynamicURL url) {
        if (url.isLocal()) return url;
        try {
            for (URLPatcher patcher: URL_PATCHES) {
                if (!patcher.isValid(url)) continue;
                try {
                    return patcher.patch(url, URLPatcher.Quality.HIGHEST);
                } catch (Exception e) {
                    return url;
                }
            }
        } catch (Exception e) {
            LOGGER.error(IT, "Failed to patch URL '{}'", url.getSource(), e);
        }
        return url;
    }

    /**
     * Gets a URL instance based on a given string, null if is malformed or invalid
     * @param url string url
     * @return if was valid
     */
    public static URL parseUrl(String url) {
        return TryCore.withReturn(defaultVar -> new URL(url), null);
    }

    /**
     * Gets a URL instance based on a given string, null if is malformed or invalid
     * @param uri string url
     * @return if was valid
     */
    public static URL parseUrl(URI uri) {
        return TryCore.withReturn(defaultVar -> uri.toURL(), null);
    }
}
