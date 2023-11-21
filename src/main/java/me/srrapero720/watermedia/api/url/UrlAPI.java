package me.srrapero720.watermedia.api.url;


import me.lib720.watermod.safety.TryCore;
import me.srrapero720.watermedia.api.WaterMediaAPI;
import me.srrapero720.watermedia.api.url.fixers.URLFixer;
import me.srrapero720.watermedia.api.url.fixers.special.SpecialFixer;
import me.srrapero720.watermedia.loaders.IBootCore;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.io.File;
import java.net.URL;
import java.util.*;

import static me.srrapero720.watermedia.WaterMedia.LOGGER;

public class UrlAPI extends WaterMediaAPI {
    public static final Marker IT = MarkerManager.getMarker(UrlAPI.class.getSimpleName());
    private static final ServiceLoader<URLFixer> FX = ServiceLoader.load(URLFixer.class);

    /**
     * Fixes string url to be stored in a URL and begin usable on VLC<br>
     * by default, this method keeps special fixers disabled
     * @param strUrl string url
     * @return result of the fixed url, null if was a broken URL
     */
    public static URLFixer.Result fixURL(String strUrl) {
        return fixURL(strUrl, false);
    }

    /**
     * Fixes string url to be stored in a URL and begin usable on VLC
     * @param strUrl string url
     * @param specials enable special fixers - be aware
     * @return result of the fixed url, null if was a broken URL
     */
    public static URLFixer.Result fixURL(String strUrl, boolean specials) {
        if (isValid(strUrl)) {
            return TryCore.withReturn(defaultVar -> {
                URL url = new URL(strUrl);
                for (URLFixer fixer: FX) {
                    if (fixer instanceof SpecialFixer && !specials) continue;
                    if (fixer.isValid(url)) return fixer.patch(url, null);
                }
                return new URLFixer.Result(url.toString(), false, false);
            }, e -> LOGGER.error(IT, "Exception occurred fixing URL", e), null);
        } else if (isValidPathUrl(strUrl)) { // nothing to FIX
            return TryCore.withReturn(defaultVar -> {
                File f = new File(strUrl);
                if (!f.exists()) throw new IllegalArgumentException("File path cannot be readed or be used in a URL");
                return new URLFixer.Result(strUrl, false, false);
            }, e -> LOGGER.error(IT, "Exception occurred converting file path to URL", e), null);
        }

        LOGGER.error(IT, "URL doesn't have a valid syntax, cannot be fixed");
        return null;
    }

    /**
     * Tool to know what platforms are supported and include it in your mod
     * @return array of current fixer platforms. By default doesn't include special fixers
     */
    public static String[] getFixersPlatforms() { return getFixersPlatforms(false); }

    /**
     * Tool to know what platforms are supported and include it in your mod
     * @param specials list special fixers too - be aware
     * @return array of current fixer platforms.
     */
    public static String[] getFixersPlatforms(boolean specials) {
        List<String> result = new ArrayList<>();
        for (URLFixer fixer: FX) {
            if (fixer instanceof SpecialFixer && !specials) continue;
            result.add(fixer.platform());
        }
        return result.toArray(new String[0]);
    }

    /**
     * Check if URL string was valid
     * @param url string url
     * @return if was valid
     */
    public static boolean isValid(String url) {
        return TryCore.withReturn(defaultVar -> { new URL(url); return true; }, false);
    }

    public static boolean isValidPathUrl(String path) {
        return TryCore.withReturn(defaultVar -> {
            new File(path).toURI().toURL();
            return true;
        }, false);
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

    public UrlAPI() {
        super();
    }

    @Override
    public Priority priority() {
        return Priority.NORMAL;
    }

    @Override
    public boolean prepare(IBootCore bootCore) { return false; }

    @Override
    public void start(IBootCore bootCore) throws Exception {}

    @Override
    public void release() {}
}