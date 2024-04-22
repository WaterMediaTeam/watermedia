package me.srrapero720.watermedia.api.url;


import me.srrapero720.watermedia.api.WaterMediaAPI;
import me.srrapero720.watermedia.api.url.fixers.URLFixer;
import me.srrapero720.watermedia.api.url.fixers.special.SpecialFixer;
import me.srrapero720.watermedia.core.tools.DataTool;
import me.srrapero720.watermedia.loaders.ILoader;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import static me.srrapero720.watermedia.WaterMedia.LOGGER;

public class UrlAPI extends WaterMediaAPI {
    public static final Marker IT = MarkerManager.getMarker("UrlAPI");
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
            try {
                URL url = new URL(strUrl);
                if (strUrl.startsWith("file:///") || strUrl.startsWith("local://")) return new URLFixer.Result(url, false, false);
                for (URLFixer fixer: FIXERS) {
                    if (fixer instanceof SpecialFixer && !specials) continue;
                    if (fixer.isValid(url)) return fixer.patch(url, null);
                }
                return new URLFixer.Result(url, false, false);
            } catch (Exception e) {
                LOGGER.error(IT, "Exception occurred fixing URL", e);
                return null;
            }
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
        try {
            new URL(url);
            return true;
        } catch (MalformedURLException e) {
            return false;
        }
    }

    public static boolean isValidPathUrl(String path) {
        try {
            new File(path).toURI().toURL();
            return true;
        } catch (MalformedURLException e) {
            return false;
        }
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


    @Override
    public Priority priority() {
        return Priority.NORMAL;
    }

    @Override
    public boolean prepare(ILoader bootCore) throws Exception {
        return FIXERS.isEmpty();
    }

    @Override
    public void start(ILoader bootCore) throws Exception {
        FIXERS.addAll(DataTool.toList(ServiceLoader.load(URLFixer.class)));
    }

    @Override
    public void release() {

    }
}