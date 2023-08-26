package me.srrapero720.watermedia.core.tools;

import me.lib720.watermod.safety.TryCore;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.lang.reflect.Method;

import static me.srrapero720.watermedia.WaterMedia.LOGGER;

public class ReflectTool {
    private static final Marker IT = MarkerManager.getMarker("Tools");

    public static void executeMethod(String name, Class<?> clazz, Object instance) {
        TryCore.simple(() -> {
            Method method = clazz.getMethod(name);
            method.setAccessible(true);
            method.invoke(instance);
        }, (e) -> LOGGER.error(IT, "Cannot execute method '{}: {}' caused by '{}' things may not work well", name, e.getClass().getSimpleName(), e.getMessage()));
    }

    @SuppressWarnings("unchecked")
    public static <T> T executeAndReturnMethod(String name, Class<?> clazz, Object instance) {
        return TryCore.withReturn((defaultVar) -> {
            Method method = clazz.getMethod(name);
            method.setAccessible(true);
            return (T) method.invoke(instance);
        }, null);
    }
}