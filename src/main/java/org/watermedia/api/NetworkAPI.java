package org.watermedia.api;

import org.watermedia.WaterMedia;
import org.watermedia.api.network.MRL;
import org.watermedia.api.network.patchs.AbstractPatch;
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

public class NetworkAPI implements WaterMediaAPI {
    private static final Marker IT = MarkerManager.getMarker(NetworkAPI.class.getSimpleName());
    private static ServiceLoader<AbstractPatch> PATCHES;

    public static void patchMRL(MRL mrl) {
        assertIsStarted();
        try {
            for (AbstractPatch patch: PATCHES) {
                if (!patch.validate(mrl)) continue;
                patch.patch(mrl);
            }
        } catch (Exception e) {
            LOGGER.error(IT, "Failed to patch URL '{}'", mrl.getUri(), e);
        }
    }

    public static String[] getPatchNames() {
        assertIsStarted();

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

    private static void assertIsStarted() {
        if (PATCHES == null)
            throw new IllegalStateException("NetworkAPI is not started");
    }

    @Override
    public Priority priority() {
        return Priority.LOW;
    }

    @Override
    public boolean prepare(WaterMedia.ILoader loader) throws Exception {
        return PATCHES == null;
    }

    @Override
    public void start(WaterMedia.ILoader loader) throws Exception {
        if (PATCHES == null) {
            PATCHES = ServiceLoader.load(AbstractPatch.class);
            PATCHES.forEach(AbstractPatch::onStart);
            LOGGER.info(IT, "Patches started successfully");
        } else {
            throw new IllegalStateException("NetworkAPI is already started");
        }
    }

    @Override
    public void release() {
        PATCHES.forEach(AbstractPatch::onRelease);
        LOGGER.info(IT, "Patches released successfully");
        PATCHES = null;
    }
}