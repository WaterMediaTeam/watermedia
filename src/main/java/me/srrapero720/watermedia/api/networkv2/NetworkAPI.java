package me.srrapero720.watermedia.api.networkv2;

import me.lib720.watermod.safety.TryCore;
import me.srrapero720.watermedia.api.WaterMediaAPI;
import me.srrapero720.watermedia.loaders.ILoader;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

import static me.srrapero720.watermedia.WaterMedia.LOGGER;

public class NetworkAPI extends WaterMediaAPI {
    public static final Marker IT = MarkerManager.getMarker(NetworkAPI.class.getSimpleName());
    public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/112.0.0.0 Safari/537.36 Edg/112.0.1722.68";
    private static final ServiceLoader<URLPatcher> URL_PATCHES = ServiceLoader.load(URLPatcher.class);

    public static DynamicURL patchURL(DynamicURL url) {
        if (url.isLocal()) return url;
        try {
            for (URLPatcher patcher: URL_PATCHES) {
                if (!patcher.isValid(url)) continue;
                return patcher.patch(url, URLPatcher.Quality.HIGHEST);
            }
        } catch (Exception e) {
            LOGGER.error(IT, "Failed to patch URL '{}'", url.getSource(), e);
        }
        return url;
    }

    public static String[] getPatchProviders() {
        ArrayList<String> result = new ArrayList<>();
        for (URLPatcher patcher: URL_PATCHES) {
            result.add(patcher.name());
        }
        return result.toArray(new String[0]);
    }

    public static DynamicURL createDynamicUrl(String urlOrPath) {
        if (urlOrPath.startsWith("local://")) {
            urlOrPath = urlOrPath.replace("local://", "");
            if (urlOrPath.startsWith(File.pathSeparator)) urlOrPath = urlOrPath.substring(1);
            urlOrPath = new File(urlOrPath).getAbsolutePath();
        }

        URL url = NetworkAPI.parseUrl(urlOrPath);
        return new DynamicURL(urlOrPath, url, (url == null) ? new File(urlOrPath) : null);
    }

    public static DynamicURL createDynamicUrl(final String urlOrPath, final boolean isVideo, final boolean isStream) {
        DynamicURL durl = createDynamicUrl(urlOrPath);
        durl.video = isVideo;
        durl.stream = isStream;
        return durl;
    }

    public static DynamicURL createDynamicUrl(final File file) {
        return new DynamicURL(file.getAbsoluteFile());
    }

    public static DynamicURL createDynamicUrl(final Path path) {
        return createDynamicUrl(path.toFile());
    }

    /**
     * Parses a query string from a {@link URL#getQuery()} in a Map
     * @param query query string
     * @return map with all values as a String
     */
    public static Map<String, String> parseQuery(String query) {
        Map<String, String> queryParams = new HashMap<>();
        String[] params = query.split("&");
        for (String param : params) {
            String[] keyValue = param.split("=");
            if (keyValue.length == 2) {
                String key = keyValue[0];
                String value = keyValue[1];
                queryParams.put(key, value);
            }
        }
        return queryParams;
    }

    /**
     * Gets a URL instance based on a given string, null if is malformed or invalid
     * @param url string url
     * @return the instanced URL, null if was not a valid URL
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

    @Override
    public Priority priority() {
        return Priority.LOW;
    }

    @Override
    public boolean prepare(ILoader bootCore) throws Exception {
        return false;
    }

    @Override
    public void start(ILoader bootCore) throws Exception {

    }

    @Override
    public void release() {

    }
}
