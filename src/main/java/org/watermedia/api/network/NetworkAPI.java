package org.watermedia.api.network;


import org.watermedia.api.WaterMediaAPI;
import org.watermedia.api.network.patchs.AbstractPatch;
import org.watermedia.core.tools.DataTool;
import org.watermedia.loaders.ILoader;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

import static org.watermedia.WaterMedia.LOGGER;

public class NetworkAPI extends WaterMediaAPI {
    public static final Marker IT = MarkerManager.getMarker("NetworkAPI");
    private static final List<AbstractPatch> FIXERS = new ArrayList<>();

    /**
     * Patchs URI for special online services like Youtube
     * @param strURI string uri
     * @return result of the fixed url, null if was a broken URL
     * @deprecated Validate is exhausting, can you do it for me?
     * Use instead {@link #patch(URI)}
     */
    @Deprecated
    public static AbstractPatch.Result patch(String strURI) {
        try {
            URI uri = parseURI(strURI);

            for (AbstractPatch fixer: FIXERS) {
                if (fixer.isValid(uri)) return fixer.patch(uri, null);
            }
            return new AbstractPatch.Result(uri, false, false);
        } catch (Exception e) {
            LOGGER.error(IT, "Exception occurred fixing URL", e);
            return null;
        }
    }

    /**
     * Patches URI for special online services like Youtube
     * @param uri uri
     * @return result of the fixed url, null if was a broken URL
     */
    public static AbstractPatch.Result patch(URI uri) {
        try {
            for (AbstractPatch fixer: FIXERS) {
                if (fixer.isValid(uri)) return fixer.patch(uri, null);
            }
            return new AbstractPatch.Result(uri, false, false);
        } catch (Exception e) {
            LOGGER.error(IT, "Exception occurred fixing URL", e);
            return null;
        }
    }

    /**
     * Converts string into a valid URI instance. It performs a path validation to also convert it into a valid URI
     *
     * @return URI instance
     */
    public static URI parseURI(String s) throws URISyntaxException {
        File f = new File(s);
        // accept local paths as file uris
        if (!f.isDirectory() && f.exists())
            return new File(s).toURI();

        return new URI(s);
    }

    /**
     * Tool to know what platforms are supported and include it in your mod
     * @return array of current fixer platforms. By default doesn't include special fixers
     */
    public static String[] getPatchPlatforms() {
        String[] result = new String[FIXERS.size()];
        for (int i = 0; i < FIXERS.size(); i++) {
            AbstractPatch fixer = FIXERS.get(i);
            result[i] = fixer.platform();
        }
        return result;
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
        FIXERS.addAll(DataTool.toList(ServiceLoader.load(AbstractPatch.class)));
    }

    @Override
    public void release() {

    }
}