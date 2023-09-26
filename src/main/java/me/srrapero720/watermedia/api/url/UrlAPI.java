package me.srrapero720.watermedia.api.url;


import me.lib720.watermod.safety.TryCore;
import me.srrapero720.watermedia.api.loader.IMediaLoader;
import me.srrapero720.watermedia.api.url.fixers.URLFixer;
import me.srrapero720.watermedia.api.url.fixers.special.SpecialFixer;
import me.srrapero720.watermedia.core.tools.DataTool;
import me.srrapero720.watermedia.core.tools.exceptions.ReInitException;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.net.URL;
import java.util.*;

import static me.srrapero720.watermedia.WaterMedia.LOGGER;

public class UrlAPI {
    private static final Marker IT = MarkerManager.getMarker("URLApi");
    private static final List<URLFixer> FIXERS = new ArrayList<>();

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
                for (int i = 0; i < FIXERS.size(); i++) {
                    URLFixer fixer = FIXERS.get(i);
                    if (fixer instanceof SpecialFixer && !specials) continue;
                    if (fixer.isValid(url)) return fixer.patch(url, null);
                }
                return new URLFixer.Result(url, false, false);
            }, e -> LOGGER.error(IT, "Exception occurred fixing URL", e), null);
        }

        LOGGER.error(IT, "URL is not valid");
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
        String[] result = new String[FIXERS.size()];
        for (int i = 0; i < FIXERS.size(); i++) {
            URLFixer fixer = FIXERS.get(i);
            if (fixer instanceof SpecialFixer && !specials) continue;
            result[i] = fixer.platform();
        }
        return result;
    }

    /**
     * Check if URL string was valid
     * @param url string url
     * @return if was valid
     */
    public static boolean isValid(String url) {
        return TryCore.withReturn(defaultVar -> { new URL(url); return true; }, false);
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

    public static void init(IMediaLoader loader) throws ReInitException {
        if (!FIXERS.isEmpty()) throw new ReInitException(IT.getName());

        LOGGER.info(IT,"Loading {}'s", URLFixer.class.getSimpleName());
        FIXERS.addAll(DataTool.toList(ServiceLoader.load(URLFixer.class)));
    }
}