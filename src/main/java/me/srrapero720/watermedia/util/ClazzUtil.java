package me.srrapero720.watermedia.util;

public class ClazzUtil {
    public static boolean existsClass(String name) {
        try {
            Class.forName(name);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
