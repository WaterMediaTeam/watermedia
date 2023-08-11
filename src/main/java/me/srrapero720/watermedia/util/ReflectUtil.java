package me.srrapero720.watermedia.util;

public class ReflectUtil {
    public static boolean existsClass(String name) {
        try {
            Class.forName(name);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
