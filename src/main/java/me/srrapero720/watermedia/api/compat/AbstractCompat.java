package me.srrapero720.watermedia.api.compat;

import me.srrapero720.watermedia.watercore_supplier.ThreadUtil;
import org.jetbrains.annotations.NotNull;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractCompat {
    public static final Map<String, String> CACHE = new HashMap<>();
    protected synchronized static void storeUrl(URL url, String result) {
        CACHE.put(url.toString(), result);
    }
    public abstract boolean isValid(@NotNull URL url);
    public String build(@NotNull URL url) {
        if (!isValid(url)) throw new IllegalArgumentException("Attempt to build a invalid URL in a invalid Compat");
        return null;
    }
}
