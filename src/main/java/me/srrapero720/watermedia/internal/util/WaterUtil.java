package me.srrapero720.watermedia.internal.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mojang.logging.LogUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static me.srrapero720.watermedia.WaterMedia.LOGGER;

public class WaterUtil {
    /**
     * 1 seconds in Minecraft equals 20 ticks
     * 20x50 equals 1000ms (1 sec)
     *
     * @param ticks Minecraft Ticks
     * @return ticks converted to MS
     */
    public static long gameTicksToMs(int ticks) { return ticks * 50L; }

    /**
     * 1000ms (1 sec) equals 20 ms in Minecraft
     * 1000/50 equals 20 Ticks (1 sec)
     *
     * @param ms Time in milliseconds
     * @return Milliseconds converted to Ticks
     */
    public static int msToGameTicks(long ms) { return (int) (ms / 50L); }

    public static <T> Field getClassField(Class<? super T> from, String name) {
        try {
            final Field f = from.getDeclaredField(name);
            f.setAccessible(true);
            return f;
        } catch (Exception e) {
            throw new RuntimeException("Unable to locate field " + from.getSimpleName() + "." + name, e);
        }
    }

    public static ClassLoader classLoader() { return WaterUtil.class.getClassLoader(); }
    public static InputStream resourceAsStream(String path) { return classLoader().getResourceAsStream(path); }

    public static String[] getArrayStringFromRes(String path) { return getJsonListFromRes(path).toArray(new String[0]); }
    public static List<String> getJsonListFromRes(String path) {
        List<String> result = new ArrayList<>();
        try (InputStream stream = resourceAsStream(path); BufferedReader reader = (stream != null) ? new BufferedReader(new InputStreamReader(stream)) : null) {
            if (reader != null) result.addAll(new Gson().fromJson(reader, new TypeToken<List<String>>() {}.getType()));
            else throw new IllegalArgumentException("File not found!");

        } catch (Exception e) { LOGGER.error("Exception trying to read JSON from {}", path, e);
        } finally { result.forEach(LOGGER::debug); }

        return result;
    }
}
