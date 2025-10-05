package org.watermedia.api.network;

import org.watermedia.api.WaterMediaAPI;
import org.watermedia.api.network.patchs.AbstractPatch;
import org.watermedia.api.network.patchs.*;
import org.watermedia.loaders.ILoader;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static org.watermedia.WaterMedia.LOGGER;

public class NetworkAPI extends WaterMediaAPI {
    public static final Marker IT = MarkerManager.getMarker("NetworkAPI");
    private static final List<AbstractPatch> FIXERS = new ArrayList<>();

    private static final Map<URI, AbstractPatch.Result> CACHE = new ConcurrentHashMap<URI, AbstractPatch.Result>() {
        private final HashMap<AbstractPatch.Result, Long> EXPIRES_IN = new HashMap<>();

        @Override
        public AbstractPatch.Result get(Object key) {
            AbstractPatch.Result patch = super.get(key);

            if (patch != null) {
                long expires = EXPIRES_IN.get(patch);
                if (System.currentTimeMillis() > expires) {
                    EXPIRES_IN.remove(patch);
                    this.remove(key);
                    LOGGER.debug("Cache patch for '{}' has expired", key);
                    return null;
                } else {
                    return patch;
                }
            } else {
                return null;
            }
        }

        @Override
        public AbstractPatch.Result put(URI key, AbstractPatch.Result value) {
            try {
                return super.put(key, value);
            } finally {
                EXPIRES_IN.put(value, System.currentTimeMillis() + 10000);
            }
        }
    };

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
            return patch(uri);
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
                if (fixer.isValid(uri)) {
                    AbstractPatch.Result r = CACHE.get(uri);
                    if (r != null) return r;

                    r = fixer.patch(uri, null);
                    CACHE.put(uri, r);
                    return r;
                }
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
    public static URI parseURI(String s) {
        File f = new File(s);
        // accept local paths as file uris
        if (!f.isDirectory() && f.exists())
            return new File(s).toURI();

        try {
            return new URI(s);
        } catch (Exception e) {
            return null;
        }
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

    /**
     * Registers a patch to be used on the API
     * @see AbstractPatch
     * @param patch patch instance
     */
    public static void registerPatch(AbstractPatch patch) {
        if (patch != null && !FIXERS.contains(patch)) {
            FIXERS.add(patch);
        }
    }

    /**
     * Unregisters a patch from the API
     * @see AbstractPatch
     * @param patch patch instance
     */
    public static void unregisterPatch(AbstractPatch patch) {
        if (patch != null) {
            FIXERS.remove(patch);
        }
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
        // YES, I HATE MIXINS ON WATERMEDIA (I)-(I)''
        registerPatch(new DiskPatch());
        registerPatch(new DrivePatch());
        registerPatch(new DropboxPatch());
        registerPatch(new KickPatch());
        registerPatch(new LightshotPatch());
        registerPatch(new MediaFirePatch());
        registerPatch(new OnedrivePatch());
        registerPatch(new PornHubPatch());
        registerPatch(new StreamablePatch());
        registerPatch(new TwitchPatch());
        registerPatch(new TwitterPatch());
        registerPatch(new YoutubePatch());
    }

    @Override
    public void release() {
        FIXERS.clear();
        CACHE.clear();
    }
}