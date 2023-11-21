package me.lib720.watermod.reflect;

import me.lib720.watermod.safety.TryCore;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static me.lib720.watermod.WaterMod.LOGGER;

public class ReflectTool {
    private static final Marker IT = MarkerManager.getMarker("Tools");

    @SuppressWarnings("unchecked")
    public static <T> T getValue(String name, Class<?> clazz, Object instance) {
        return TryCore.withReturn(defaultVar -> {
           Field f = clazz.getField(name);
           f.setAccessible(true);
           return (T) f.get(instance);
        }, null);
    }

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
    public static <T> T findAndInvokeWithReturn(String[] methodNames, Class<?> classFrom, Object instance, Object ...arguments) {
        return TryCore.withReturn(defaultVar -> {
            Class<?>[] classes = new Class<?>[arguments.length];
            String[] classesNames = new String[arguments.length];
            for (int i = 0; i < arguments.length; i++) {
                classes[i] = arguments[i].getClass();
                classesNames[i] = classes[i].getSimpleName();
            }

            for (int i = 0; i < methodNames.length; i++) {
                try {
                    Method method = classFrom.getMethod(methodNames[i], classes);
                    method.setAccessible(true);
                    return (T) method.invoke(instance, arguments);
                } catch (NoSuchMethodException e) {
                    LOGGER.warn(IT, "Failed to execute '{}.{}({})' caused by {}", classFrom.getSimpleName(), methodNames[i], Arrays.toString(classesNames), e.getMessage());
                }
            }
            LOGGER.fatal(IT, "Cannot execute any method of '{}: {}', things may not work well", classFrom.getSimpleName(), Arrays.toString(methodNames));
            return defaultVar;
        }, null);
    }
}