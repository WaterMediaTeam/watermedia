package org.watermedia.core.tools;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ServiceLoader;

public class DataTool {
    public static final Gson GSON = new Gson();
    private static final int DEFAULT_BUFFER_SIZE = 8192;
    private static final int MAX_BUFFER_SIZE = Integer.MAX_VALUE - 8;
    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

    public static String[] concat(String[] arr, String[] arr2) {
        String[] r = new String[arr.length + arr2.length];
        for (int i = 0; i < (arr.length + arr2.length); i++) {
            r[i] = i < arr.length ? arr[i] : arr2[i - arr.length];
        }
        return r;
    }

    public static long sumArr(long[] arr) {
        long r = 0;
        for (long l: arr) {
            r += l;
        }
        return r;
    }

    public static long parseLongOr(String s, long o) {
        try {
            return Long.parseLong(s);
        } catch (NumberFormatException e) {
            return o;
        }
    }

    public static <T> T fromJSON(String s, Type t) {
        return GSON.fromJson(s, t);
    }

    public static <T> T fromJSON(InputStreamReader s, Type t) {
        return GSON.fromJson(s, t);
    }

    public static boolean startsWith(String s, String[] matches) {
        s = s.toLowerCase();
        for (String m: matches) {
            if (s.startsWith(m)) {
                return true;
            }
        }
        return false;
    }

    public static boolean contains(String s, String[] matches) {
        s = s.toLowerCase();
        for (String m: matches) {
            if (s.contains(m)) {
                return true;
            }
        }
        return false;
    }

    public static int[] filterValue(int[] its, int v) {
        int size = 0;
        for (int i : its) if (i != v) size++;

        int[] result = new int[size];

        int pos = 0;
        for (int i : its)
            if (i != v) result[pos++] = i;

        return result;
    }

    public static <T> List<T> toList(ServiceLoader<T> s) {
        List<T> r = new ArrayList<>();
        for (T t: s) r.add(t);
        return r;
    }

    public static byte[] readAllBytes(InputStream stream) throws IOException {
        int len = Integer.MAX_VALUE;
        if (len < 0) {
            throw new IllegalArgumentException("len < 0");
        }

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