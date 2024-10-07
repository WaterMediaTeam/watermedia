package me.srrapero720.watermedia.tools;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.watermedia.WaterMedia.LOGGER;

public class DataTool {
    public static final Gson GSON = new Gson();
    private static final int DEFAULT_BUFFER_SIZE = 8192;
    private static final int MAX_BUFFER_SIZE = Integer.MAX_VALUE - 8;
    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

    public static long parseLongOr(String s, long o) {
        try {
            return Long.parseLong(s);
        } catch (NumberFormatException e) {
            return o;
        }
    }

    public static String orEmpty(String s) {
        return s == null ? "" : s;
    }

    public static String orOrEmpty(String s, String s2) {
        return s == null ? s2 == null ? "" : s2 : s;
    }

    public static String orElse(String s, String s1) {
        return s == null ? s1 : s;
    }

    public static <T> T fromJSON(String s, Type t) {
        return GSON.fromJson(s, t);
    }

    public static <T> T fromJSON(InputStreamReader s, Type t) {
        return GSON.fromJson(s, t);
    }

    public static int[] filterValue(int[] its, int v) {
        int size = 0;
        for (int i: its) if (i != v) size++;

        int[] result = new int[size];

        int pos = 0;
        for (int i: its)
            if (i != v) result[pos++] = i;

        return result;
    }

    @SuppressWarnings("all")
    public static <T> T[] concatArray(T[] array, T... values) {
        Object t = Array.newInstance(array.getClass().getComponentType(), array.length + values.length);
        System.arraycopy(array, 0, t, 0, array.length);
        System.arraycopy(values, 0, t, array.length, values.length);
        return (T[]) t;
    }

    public static int[] unboxArray(List<Integer> arr) {
        int[] result = new int[arr.size()];
        for (int i = 0; i < arr.size(); i++) {
            result[i] = arr.get(i);
        }
        return result;
    }

    public static int[] unboxArray(Integer[] arr) {
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

    public static byte[] readAllBytesAndClose(InputStream stream) throws IOException {
        try (stream) {
            return readAllBytes(stream);
        }
    }

    public static byte[] readAllBytes(InputStream stream) throws IOException {
        int len = Integer.MAX_VALUE;

        List<byte[]> bufs = null;
        byte[] result = null;
        int total = 0;
        int remaining = len;
        int n;
        do {
            byte[] buf = new byte[Math.min(remaining, DEFAULT_BUFFER_SIZE)];
            int nread = 0;

            // read to EOF which may read more or less than buffer size
            while ((n = stream.read(buf, nread,
                    Math.min(buf.length - nread, remaining))) > 0) {
                nread += n;
                remaining -= n;
            }

            if (nread > 0) {
                if (MAX_BUFFER_SIZE - total < nread) {
                    throw new OutOfMemoryError("Required array size too large");
                }
                if (nread < buf.length) {
                    buf = Arrays.copyOfRange(buf, 0, nread);
                }
                total += nread;
                if (result == null) {
                    result = buf;
                } else {
                    if (bufs == null) {
                        bufs = new ArrayList<>();
                        bufs.add(result);
                    }
                    bufs.add(buf);
                }
            }
            // if the last call to read returned -1 or the number of bytes
            // requested have been read then break
        } while (n >= 0 && remaining > 0);

        if (bufs == null) {
            if (result == null) {
                return new byte[0];
            }
            return result.length == total ?
                    result : Arrays.copyOf(result, total);
        }

        result = new byte[total];
        int offset = 0;
        remaining = total;
        for (byte[] b : bufs) {
            int count = Math.min(b.length, remaining);
            System.arraycopy(b, 0, result, offset, count);
            offset += count;
            remaining -= count;
        }

        return result;
    }

    public static String encodeStringToHex(String string) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest(string.getBytes(StandardCharsets.UTF_8));

            char[] hexChars = new char[bytes.length * 2];
            for (int j = 0; j < bytes.length; j++) {
                int v = bytes[j] & 0xFF;
                hexChars[j * 2] = HEX_ARRAY[v >>> 4];
                hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
            }
            return new String(hexChars);
        } catch (Exception e) {
            LOGGER.error("Failed to digest and encode string {}", string, e);
            return null;
        }
    }

    public static String encodeHexString(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }
}