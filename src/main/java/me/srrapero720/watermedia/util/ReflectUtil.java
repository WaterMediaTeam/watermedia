package me.srrapero720.watermedia.util;

import java.lang.reflect.Field;

public class ReflectUtil {
    public static <T> Field getFieldFrom(Class<? super T> clazz, String offical) {
        try {
            var f = clazz.getDeclaredField(offical);
            f.setAccessible(true);
            return f;
        } catch (Exception var3) {
            throw new RuntimeException("Unable to locate field " + clazz.getSimpleName() + "." + offical, var3);
        }
    }
}
