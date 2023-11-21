package me.srrapero720.watermedia.tools;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import me.lib720.madgag.gif.fmsware.GifDecoder;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.jar.Manifest;

import static me.srrapero720.watermedia.WaterMedia.LOGGER;

public class JarTool {
    static final Marker IT = MarkerManager.getMarker("Tools");

    public static String readString(String from) throws IOException {
        byte[] bytes = ByteTools.readAllBytes(getResourceInputStream(from));
        return new String(bytes, Charset.defaultCharset());
    }

    public static String[] readArray(String path) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getResourceInputStream(path)))) {
            return new Gson().fromJson(reader, new TypeToken<String[]>() {}.getType());
        }
    }

    public static String[] readArrayAndParse(String path, Map<String, String> values) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getResourceInputStream(path)))) {
            String[] keyset = values.keySet().toArray(new String[0]);
            String[] str = new Gson().fromJson(reader, new TypeToken<String[]>() {}.getType());

            String v;
            for (int i = 0; i < str.length; i++) {
                v = str[i];
                for (int j = 0; j < keyset.length; j++) {
                    str[i] = v.replace("{" + keyset[j] + "}", values.get(keyset[j]));
                }
            }
            return str;
        }
    }

    public static BufferedImage readImage(String path) throws IOException {
        try (InputStream in = getResourceInputStream(path)) {
            BufferedImage image = ImageIO.read(in);
            if (image != null) return image;
        }
        throw new FileNotFoundException("result of BufferedImage was null");
    }

    public static GifDecoder readGif(String path) throws IOException {
        try (BufferedInputStream in = new BufferedInputStream(getResourceInputStream(path))) {
            return new GifDecoder().readOrThrow(in);
        }
    }

    public static boolean copyAsset(String origin, Path dest) {
        try (InputStream is = getResourceInputStream(origin)) {
            if (is == null) throw new FileNotFoundException("Resource was not found in " + origin);

            File destParent = dest.getParent().toFile();
            if (!destParent.exists() && !destParent.mkdirs()) LOGGER.fatal(IT, "Cannot be created parent directories to {}", dest.toString());
            Files.copy(is, dest, StandardCopyOption.REPLACE_EXISTING);
            return true;
        } catch (Exception e) {
            LOGGER.error(IT, "Failed to extract from (JAR) {} to {} due to unexpected error", origin, dest, e);
        }
        return false;
    }

    public static Manifest getOwnManifest() throws IOException {
        try (InputStream is = getResourceInputStream("/META-INF/MANIFEST.MF")) {
            return new Manifest(is);
        }
    }

    // this might have a BAD performance
    public static InputStream getResourceInputStream(String source) {
        InputStream is = getResourceInputStream$byClassLoader(source, JarTool.class.getClassLoader());
        if (is == null) is = getResourceInputStream$byClassLoader(source, Thread.currentThread().getContextClassLoader());
        if (is == null) is = getResourceInputStream$byClassLoader(source, ClassLoader.getSystemClassLoader());
        return is; // InputStream still can be null
    }

    // Internal usage
    private static InputStream getResourceInputStream$byClassLoader(String source, ClassLoader classLoader) {
        InputStream is = classLoader.getResourceAsStream(source);
        if (is == null && source.startsWith("/")) is = classLoader.getResourceAsStream(source.substring(1));
        return is;
    }
}