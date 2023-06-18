package me.srrapero720.watermedia;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import me.lib720.caprica.vlcj.binding.support.runtime.RuntimeUtil;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static me.srrapero720.watermedia.WaterMedia.LOGGER;

public class MediaUtil {

    public static <T> Field getClassField(Class<? super T> from, String name) {
        try {
            final Field f = from.getDeclaredField(name);
            f.setAccessible(true);
            return f;
        } catch (Exception e) {
            throw new RuntimeException("Unable to locate field " + from.getSimpleName() + "." + name, e);
        }
    }

    /**
     * Gets a resource from WaterMedia jar
     * @param path where is located the specific file
     * @return a InputStream with the file.
     */
    public static InputStream resourceAsStream(String path) { return MediaUtil.class.getClassLoader().getResourceAsStream(path); }


    /**
     * Gets a StringArray from a json inside WaterMedia jar resources
     * @param path where is located the specific JSON
     * @return a String[] with the JSON content
     */
    public static String[] getArrayStringFromRes(String path) { return getJsonListFromRes(path).toArray(new String[0]); }

    /**
     * Gets a List[String] from a json inside WaterMedia jar resources
     * @param path where is located the specific JSON
     * @return a List[String] with the JSON content
     */
    public static List<String> getJsonListFromRes(String path) {
        List<String> result = new ArrayList<>();
        try (InputStream stream = resourceAsStream(path); BufferedReader reader = (stream != null) ? new BufferedReader(new InputStreamReader(stream)) : null) {
            if (reader != null) result.addAll(new Gson().fromJson(reader, new TypeToken<List<String>>() {}.getType()));
            else throw new IllegalArgumentException("File not found!");

        } catch (Exception e) { LOGGER.error("Exception trying to read JSON from {}", path, e);
        } finally { result.forEach(LOGGER::debug); }

        return result;
    }

    public static void extractFrom(String originPath, String destinationPath) {
        try (var is = resourceAsStream(originPath)) {
            Path dllDestinationPath = Paths.get(destinationPath);
            if (is != null) {
                Files.createDirectories(dllDestinationPath.getParent());
                Files.copy(is, dllDestinationPath);
            } else {
                LOGGER.error("Resource not found: {}", originPath);
            }
        } catch (FileNotFoundException fnfe) {
            LOGGER.error("Failed to extract from {}, file not found: {}", originPath, fnfe);
        } catch (IOException ioe) {
            LOGGER.error("Failed to extract from {} to {} due to I/O error: {}", originPath, destinationPath, ioe);
        } catch (Exception e) {
            LOGGER.error("Failed to extract from {} to {} due to unexpected error: {}", originPath, destinationPath, e);
        }
    }

    public static void deleteFrom(String destinationPath) {
        try {
            Path path = Paths.get(destinationPath);
            if (Files.exists(path)) Files.delete(path);
        } catch (Exception e) {
            LOGGER.error("Failed to delete from {} due to unexpected error", destinationPath, e);
        }
    }

    /**
     * Figure out current architecture and OS
     * @return os-arch simplified
     */
    public static String getOsArch() {
        String arch = System.getProperty("os.arch");
        if ((arch.equals("amd64") || arch.equals("x86_64"))) {
            if (RuntimeUtil.isWindows()) return "win-x64";
            if (RuntimeUtil.isMac()) return "mac-x64";
            if (RuntimeUtil.isNix()) return "nix-x64";
        } else if (arch.equals("arm64")) {
            if (RuntimeUtil.isWindows()) return "win-arm64";
            if (RuntimeUtil.isMac()) return "mac-arm64";
            if (RuntimeUtil.isNix()) return "nix-arm64";
        }
        return "dummy";
    }
}
