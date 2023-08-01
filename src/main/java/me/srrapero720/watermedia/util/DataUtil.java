package me.srrapero720.watermedia.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class DataUtil {
    @SafeVarargs
    public static <T> List<T> listOf(T... array) {
        return new ArrayList<>(Arrays.asList(array));
    }

    public static boolean existsAnyMatch(int[] iterable, AnyMatchTester tester) {
        for (int i: iterable) if (tester.test(i)) return true;
        return false;
    }

    public interface AnyMatchTester {
        boolean test(int v);
    }
}
