package org.watermedia.api;

import me.srrapero720.watermedia.api.MediaContext;
import org.watermedia.WaterMedia;
import org.watermedia.api.network.MRL;
import org.watermedia.core.network.patchs.AbstractPatch;
import org.watermedia.tools.DataTool;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

import static org.watermedia.WaterMedia.LOGGER;

public class NetworkAPI extends WaterMediaAPI {
    public static final Marker IT = MarkerManager.getMarker(NetworkAPI.class.getSimpleName());

    private static final ServiceLoader<AbstractPatch> PATCHES = ServiceLoader.load(AbstractPatch.class);

    public static void patchMRL(MRL mrl, MediaContext context) {
        try {
            for (AbstractPatch patch: PATCHES) {
                if (!patch.validate(mrl)) continue;
                patch.patch(context, mrl);
            }
        } catch (Exception e) {
            LOGGER.error(IT, "Failed to patch URL '{}'", mrl.getUri(), e);
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
    public static Map<String, String> decodeQuery(String query) {
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
    public boolean prepare(WaterMedia.ILoader bootCore) throws Exception {
        return true;
    }

    @Override
    public void start(WaterMedia.ILoader bootCore) throws Exception {

    }

    @Override
    public void release() {

    }
}