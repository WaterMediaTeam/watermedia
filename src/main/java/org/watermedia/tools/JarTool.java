package org.watermedia.tools;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import static org.watermedia.WaterMedia.LOGGER;

public class JarTool {
    private static final Marker IT = MarkerManager.getMarker("Tools");

    public static String readString(String from) {
        try (InputStream is = readResourceAsStream(from)) {
            byte[] bytes = DataTool.readAllBytes(is);
            return new String(bytes, Charset.defaultCharset());
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean extract(String origin, Path dest) {
        try (InputStream is = readResourceAsStream(origin)) {
            if (is == null) throw new FileNotFoundException("Resource was not found in " + origin);

            File destParent = dest.getParent().toFile();
            if (!destParent.exists() && !destParent.mkdirs()) LOGGER.fatal(IT, "Cannot be created parent directories to {}", dest.toString());
            Files.copy(is, dest, StandardCopyOption.REPLACE_EXISTING);
            return true;
        } catch (Exception e) {
            LOGGER.error(IT, "Failed to extract from (JAR) '{}' to '{}' due to unexpected error", origin, dest, e);
        }
        return false;
    }

    public static <T> List<T> readList(String path) {
        List<T> result = new ArrayList<>();
        try (InputStreamReader reader = new InputStreamReader(readResourceAsStream(path))) {
            result.addAll(new Gson().fromJson(reader, new TypeToken<List<T>>() {}.getType()));
        } catch (Exception e) {
            LOGGER.error(IT, "Exception trying to read list JSON from {}", path, e);
        }

        return result;
    }

    public static String[] readArray(String path) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(readResourceAsStream(path)))) {
            return new Gson().fromJson(reader, new TypeToken<String[]>() {}.getType());
        } catch (IOException e) {
            LOGGER.error(IT, "Exception trying to read array JSON from {}", path, e);
        }

        return new String[0];
    }

    public static BufferedImage readImage(String path) {
        try (InputStream in = readResourceAsStream(path)) {
            BufferedImage image = ImageIO.read(in);
            if (image != null) return image;
            else throw new FileNotFoundException("result of BufferedImage was null");
        } catch (Exception e) {
            throw new IllegalStateException("Failed loading BufferedImage from resources", e);
        }
    }

    public static InputStream readResourceAsStream(String source) {
        return readResourceAsStream$byClassLoader(source, JarTool.class.getClassLoader()); // InputStream still can be null
    }

    private static InputStream readResourceAsStream$byClassLoader(String source, ClassLoader classLoader) {
        InputStream is = classLoader.getResourceAsStream(source);
        if (is == null && source.startsWith("/")) is = classLoader.getResourceAsStream(source.substring(1));
        return is;
    }

    public static URL readResource(String source) {
        return readResource$byClassLoader(source, JarTool.class.getClassLoader()); // URL still can be null
    }

    private static URL readResource$byClassLoader(String source, ClassLoader classLoader) {
        URL is = classLoader.getResource(source);
        if (is == null && source.startsWith("/")) is = classLoader.getResource(source.substring(1));
        return is;
    }
}