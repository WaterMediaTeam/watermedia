package me.srrapero720.watermedia.internal.util;

import java.lang.reflect.Field;

public class WaterUtil {

    // 1 segundo equivale a 20 ticks
    // Multiplicando 20 * 50 obtienes 1000ms lo que equivale a 1 sec
    public static long gameTicksToMs(int ticks) {
        return ticks * 50L;
    }
    public static int msToGameTicks(long ticks) {
        return (int) (ticks / 50L);
    }

    public static <T> Field getClassField(Class<? super T> from, String name) {
        try {
            final Field f = from.getDeclaredField(name);
            f.setAccessible(true);
            return f;
        } catch (Exception e) {
            throw new RuntimeException("Unable to locate field " + from.getSimpleName() + "." + name, e);
        }
    }
}
