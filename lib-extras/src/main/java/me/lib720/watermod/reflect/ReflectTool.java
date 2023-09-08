package me.lib720.watermod.reflect;

import me.lib720.watermod.safety.TryCore;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static me.lib720.watermod.WaterMod.LOGGER;

public class ReflectTool {
    private static final Marker IT = MarkerManager.getMarker("Tools");

    public static void invoke(String name, Class<?> clazz, Object instance) {
        TryCore.simple(() -> {
            Method method = clazz.getMethod(name);
            method.setAccessible(true);
            method.invoke(instance);
        }, (e) -> LOGGER.error(IT, "Cannot execute method '{}: {}' caused by '{}' things may not work well", name, e.getClass().getSimpleName(), e.getMessage()));
    }

    @SuppressWarnings("unchecked")
    public static <T> T invokeWithReturn(String name, Class<?> clazz, Object instance, Object ...args) {
        return TryCore.withReturn((defaultVar) -> {
            List<Class<?>> argClazz = new ArrayList<>();
            for (Object cl: args) argClazz.add(cl.getClass());

            Method method = clazz.getMethod(name, argClazz.toArray(new Class[0]));
            method.setAccessible(true);
            return (T) method.invoke(instance, args);
        }, null);
    }

    @SuppressWarnings("unchecked")
    public static <T> T findAndInvokeWithReturn(String[] name, Class<?> clazz, Object instance, Object ...args) {
        return TryCore.withReturn(defaultVar -> {
            for (int i = 0; i < name.length; i++) {
                try {
                    List<Class<?>> argClazz = new ArrayList<>();
                    for (Object cl: args) argClazz.add(cl.getClass());

                    Method method = clazz.getMethod(name[i], argClazz.toArray(new Class[0]));
                    method.setAccessible(true);
                    return (T) method.invoke(instance, args);
                } catch (NoSuchMethodException e) {
                    LOGGER.error(IT, "Cannot execute any method of '{}: {}' caused by '{}' things may not work well", e.getClass().getSimpleName(), Arrays.toString(name), e.getMessage(), e);
                }
            }
            return defaultVar;
        }, null);
    }
}