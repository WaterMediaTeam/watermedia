package org.watermedia.tools;

import com.google.gson.Gson;
import org.watermedia.api.MathAPI;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static org.watermedia.WaterMedia.LOGGER;

public class DataTool {
    public static final Gson GSON = new Gson();
    private static final int DEFAULT_BUFFER_SIZE = 8192;
    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

    public static long orElse(String s, int o) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return o;
        }
    }

    public static long orElse(String s, long o) {
        try {
            return Long.parseLong(s);
        } catch (NumberFormatException e) {
            return o;
        }
    }

    public static String orElse(String s, String s1) {
        return s == null ? s1 : s;
    }

    public static long futureTime(long afterTimeInSeconds) {
        return System.currentTimeMillis() + afterTimeInSeconds * 1000;
    }

    public static <T> T fromJSON(String s, Type t) {
        return GSON.fromJson(s, t);
    }

    public static <T> T fromJSON(InputStreamReader s, Type t) {
        return GSON.fromJson(s, t);
    }

    public static int[] filter(int[] its, int v) {
        int size = 0;
        for (int i: its) if (i != v) size++;

        int[] result = new int[size];

        int pos = 0;
        for (int i: its)
            if (i != v) result[pos++] = i;

        return result;
    }

    @SuppressWarnings("all")
    public static <T> T[] concat(T[] array, T... values) {
        Object t = Array.newInstance(array.getClass().getComponentType(), array.length + values.length);
        System.arraycopy(array, 0, t, 0, array.length);
        System.arraycopy(values, 0, t, array.length, values.length);
        return (T[]) t;
    }

    public static int[] unbox(List<Integer> arr) {
        int[] result = new int[arr.size()];
        for (int i = 0; i < arr.size(); i++) {
            result[i] = arr.get(i);
        }
        return result;
    }

    public static int[] unbox(Integer[] arr) {
        int[] result = new int[arr.length];
        int i = 0;
        while (i < arr.length) {
            result[i] = arr[i];
            i++;
        }
        return result;
    }

    public static <T> List<T> toList(Iterable<T> s) {
        List<T> r = new ArrayList<>();
        for (T t: s) r.add(t);
        return r;
    }

    public static byte[] readAllBytes(InputStream stream) throws IOException {
        final int length = stream.available();
        final ByteArrayOutputStream output = new ByteArrayOutputStream(length > 0 ? length : 32);

        final byte[] data = new byte[DEFAULT_BUFFER_SIZE];
        int readed = 0;

        while (readed != -1) {
            readed = stream.read(data);
            output.write(data, 0, readed);
        }

        return output.toByteArray();
    }

    public static String hexEncode(String string) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest(string.getBytes(StandardCharsets.UTF_8));

            return hexEncode(bytes);
        } catch (Exception e) {
            LOGGER.error("Failed to digest and encode string {}", string, e);
            return null;
        }
    }

    public static String hexEncode(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

    // I hate this bullshit with my life
    @SuppressWarnings("unchecked")
    public static <T, V> V[] getValueFrom(T[] arr, Function<T, V> how2) {
        V[] result = (V[]) new Object[arr.length];
        for (int i = 0; i < arr.length; i++) {
            var thing = arr[i];
            result[i] = how2.apply(thing);
        }
        return result;
    }

    // I hate this bullshit with my life
    public static <T> int[] getIntValueFrom(T[] arr, Function<T, Integer> how2) {
        int[] result = new int[arr.length];
        for (int i = 0; i < arr.length; i++) {
            var thing = arr[i];
            result[i] = how2.apply(thing);
        }
        return result;
    }
}