package me.srrapero720.watermedia.api.network;

import me.srrapero720.watermedia.api.MediaContext;
import me.srrapero720.watermedia.api.WaterMediaAPI;
import me.srrapero720.watermedia.api.network.patchs.AbstractPatch;
import me.srrapero720.watermedia.loader.ILoader;
import me.srrapero720.watermedia.tools.DataTool;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

import static me.srrapero720.watermedia.WaterMedia.LOGGER;

public class NetworkAPI extends WaterMediaAPI {
    public static final Marker IT = MarkerManager.getMarker(NetworkAPI.class.getSimpleName());

    private static final ServiceLoader<AbstractPatch> PATCHES = ServiceLoader.load(AbstractPatch.class);

    public static void patchSource(MediaURI url, MediaContext context) {
        try {
            for (AbstractPatch patch: PATCHES) {
                if (!patch.validate(url)) continue;
                patch.patch(url, context);
            }
        } catch (Exception e) {
            LOGGER.error(IT, "Failed to patch URL '{}'", url.getUri(), e);
        }
    }

    public static String[] getPatchNames() {
        ArrayList<String> r = new ArrayList<>();
        for (AbstractPatch patch: PATCHES) {
            r.add(patch.name());
        }
        return r.toArray(new String[0]);
    }

    /**
     * Parses a query string from a {@link URL#getQuery()} in a Map
     * @param query query string
     * @return map with all values as a String
     */
    public static Map<String, String> parseQuery(String query) {
        final var result = new HashMap<String, String>();
        final var params = query.split("&");
        for (String p: params) {
            var keyVal = p.split("=");
            if (keyVal.length == 2) {
                result.put(keyVal[0], keyVal[1]);
            }
        }
        return result;
    }

    /**
     * Encodes a map of string into a Query string
     * @param map map of params
     * @return encoded string with all values
     */
    public static String encodeQuery(Map<String, ?> map) {
        var builder = new StringBuilder();
        map.forEach((k, v) -> {
            builder.append(k).append("=");
            if (v instanceof Map valueMap) {
                builder.append(DataTool.GSON.toJson(valueMap));
            } else {
                builder.append(v);
            }

            builder.append("&");
        });

        if (builder.charAt(builder.length() - 1) == '&') {
            builder.deleteCharAt(builder.length() - 1);
        }
        return "?" + URLEncoder.encode(builder.toString(), StandardCharsets.UTF_8);
    }

    @Override
    public Priority priority() {
        return Priority.LOW;
    }

    @Override
    public boolean prepare(ILoader bootCore) throws Exception {
        return true;
    }

    @Override
    public void start(ILoader bootCore) throws Exception {
        // TODO: Google drive hates this
        CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));
    }

    @Override
    public void release() {

    }
}