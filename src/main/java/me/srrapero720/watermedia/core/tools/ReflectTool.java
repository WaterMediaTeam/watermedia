package me.srrapero720.watermedia.core.tools;

import me.lib720.watermod.ThreadCore;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.lang.reflect.Method;

import static me.srrapero720.watermedia.WaterMedia.LOGGER;

public class ReflectTool {
    private static final Marker IT = MarkerManager.getMarker("Reflecting");

    public static void executeMethod(String name, Class<?> clazz, Object instance) {
        ThreadCore.trySimple(() -> {
            Method method = clazz.getMethod(name);
            method.setAccessible(true);
            method.invoke(instance);
        }, (e) -> LOGGER.error(IT, "Cannot execute method '{}: {}' caused by '{}' things may not work well", name, e.getClass().getSimpleName(), e.getMessage()));
    }

    @SuppressWarnings("unchecked")
    public static <T> T executeAndReturnMethod(String name, Class<?> clazz, Object instance) {
        return ThreadCore.tryAndReturn((defaultVar) -> {
            Method method = clazz.getMethod(name);
            method.setAccessible(true);
            return (T) method.invoke(instance);
        }, null);
    }
}
