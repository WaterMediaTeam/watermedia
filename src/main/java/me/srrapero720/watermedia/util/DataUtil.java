package me.srrapero720.watermedia.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DataUtil {
    @SafeVarargs
    public static <T> List<T> listOf(T... array) {
        return new ArrayList<>(Arrays.asList(array));
    }
}
