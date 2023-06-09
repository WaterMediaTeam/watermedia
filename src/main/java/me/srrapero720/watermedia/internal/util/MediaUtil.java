package me.srrapero720.watermedia.internal.util;

public class MediaUtil {

    // 1 segundo equivale a 20 ticks
    // Multiplicando 20 * 50 obtienes 1000ms lo que equivale a 1 sec
    public static long gameTicksToMs(int ticks) {
        return ticks * 50L;
    }
    public static int msToGameTicks(long ticks) {
        return (int) (ticks / 50L);
    }
}
