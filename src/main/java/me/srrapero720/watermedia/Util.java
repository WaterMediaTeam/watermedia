package me.srrapero720.watermedia;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import me.lib720.caprica.vlcj.binding.support.runtime.RuntimeUtil;
import me.srrapero720.watermedia.api.external.GifDecoder;
import me.srrapero720.watermedia.api.external.ThreadUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.reflect.Field;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import com.sun.jna.Platform;

import static me.srrapero720.watermedia.WaterMedia.LOGGER;

public class Util {
    private static final Marker IT = MarkerFactory.getMarker("Util");
    public static final OsArch ARCH = getOsArch();

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
     * Gets a resource classLoader WaterMedia jar
     * @param path where is located the specific file
     * @return a InputStream with the file.
     */
    public static InputStream resourceAsStream(String path, ClassLoader classLoader) {
        try {
            URLConnection connection = classLoader.getResource(path).openConnection();
            return connection.getInputStream();
        } catch (Exception e) {
            LOGGER.error(IT, "### WARNING");
            LOGGER.error(IT, "### CANNOT GET INPUT STREAM FROM OUR JAR RESOURCES, CHECK DEBUG.LOG FOR BETTER INFO");
            if (LOGGER.isDebugEnabled()) LOGGER.debug(IT, "Exception trying to get InputStream", e);
            else LOGGER.error(IT, "Exception trying to get InputStream", e);
        }

        return classLoader.getResourceAsStream(path);
    }

    /**
     * Gets a resource from WaterMedia jar
     * @param path where is located the specific file
     * @return a InputStream with the file.
     */
    public static InputStream resourceAsStream(String path) { return resourceAsStream(path, Util.class.getClassLoader()); }


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

        } catch (Exception e) {
            LOGGER.error(IT, "### Exception trying to read JSON from {}", path);
            if (LOGGER.isDebugEnabled()) LOGGER.debug(IT, "### Information", e);
            else LOGGER.error("### Information", e);
        }

        return result;
    }

    public static List<String> getJsonListFromRes(String path, ClassLoader classLoader) {
        List<String> result = new ArrayList<>();
        try (InputStream stream = resourceAsStream(path, classLoader); BufferedReader reader = (stream != null) ? new BufferedReader(new InputStreamReader(stream)) : null) {
            if (reader != null) result.addAll(new Gson().fromJson(reader, new TypeToken<List<String>>() {}.getType()));
            else throw new IllegalArgumentException("File not found!");

        } catch (Exception e) { LOGGER.error(IT, "Exception trying to read JSON from {}", path, e);}

        return result;
    }

    public static void extractFrom(String originPath, String destinationPath) {
        try (var is = resourceAsStream(originPath)) {
            Path dllDestinationPath = Paths.get(destinationPath);
            if (is != null) {
                Files.createDirectories(dllDestinationPath.getParent());
                Files.copy(is, dllDestinationPath);
            } else {
                throw new RuntimeException("Resource was not found in " + originPath);
            }
        } catch (Exception e) {
            LOGGER.error(IT, "### Failed to extract from {} to {} due to unexpected error", originPath, destinationPath);
            if (LOGGER.isDebugEnabled()) LOGGER.debug(IT, "### Information", e);
            else LOGGER.error("### Information", e);
        }
    }

    public static void deleteFrom(String destinationPath) {
        try {
            Path path = Paths.get(destinationPath);
            if (Files.exists(path)) FileUtils.deleteDirectory(path.toFile());
        } catch (Exception e) {
            LOGGER.error(IT, "Failed to delete from {} due to unexpected error", destinationPath, e);
        }
    }

    public static String readFrom(Path path) {
        return ThreadUtil.tryAndReturn(defaultVar -> Files.exists(path) ? Files.readString(path) : defaultVar, null);
    }

    /**
     * Figure out current architecture and OS
     * @return os-arch simplified
     */
    private static OsArch getOsArch() {
        String arch = Platform.ARCH;
        switch (arch) {
            case "amd64", "x86-64" -> {
                if (RuntimeUtil.isWindows()) return OsArch.WIN_X64;
                if (RuntimeUtil.isMac()) return OsArch.MAC_X64;
                if (RuntimeUtil.isNix()) return OsArch.NIX_X64;
            }
            case "arm64" -> {
                if (RuntimeUtil.isWindows()) return OsArch.WIN_ARM64;
                if (RuntimeUtil.isMac()) return OsArch.MAC_ARM64;
                if (RuntimeUtil.isNix()) return OsArch.NIX_ARM64;
            }
            case "arm", "armel" -> {
                if (RuntimeUtil.isWindows()) return OsArch.WIN_ARM;
                if (RuntimeUtil.isMac()) return OsArch.MAC_ARM;
                if (RuntimeUtil.isNix()) return OsArch.NIX_ARM;
            }
            case "x86" -> {
                if (RuntimeUtil.isWindows()) return OsArch.WIN_X32;
            }

            default -> {
                return OsArch.DUMMY;
            }
        }

        throw new RuntimeException("¿How did you get here?");
    }

    public static BufferedImage getImageFromResources(String path) {
        try (InputStream in = resourceAsStream(path)) {
            var image = ImageIO.read(in);
            if (image != null) return image;
            else throw new NullPointerException("Image read from WaterMedia resources was NULL");
        } catch (Exception e) {
            throw new IllegalStateException("Failed loading BufferedImage from WaterMedia resources", e);
        }
    }

    public static GifDecoder getGifFromResources(String path) {
        try (ByteArrayInputStream in = new ByteArrayInputStream(IOUtils.toByteArray(resourceAsStream(path)))) {
            GifDecoder gif = new GifDecoder();
            int status = gif.read(in);

            if (status == GifDecoder.STATUS_OK) {
                return gif;
            } else {
                LOGGER.error(IT, "Exception reading Gif from {}", path);
                throw new IOException("Failed to read/process gif, status code " + status);
            }
        } catch (Exception e) {
            throw new IllegalStateException("Failed loading gif from WaterMedia resources", e);
        }
    }

    public static boolean integrityCheck(String source, File targetFile) {
        try (InputStream sourceStream = Util.resourceAsStream(source)) {
            MessageDigest md = MessageDigest.getInstance("MD5");

            byte[] sourceDigest = calculateDigest(sourceStream, md);
            byte[] targetDigest = calculateDigest(new FileInputStream(targetFile), md);
            if (!MessageDigest.isEqual(sourceDigest, targetDigest)) throw new RuntimeException("File no match with the stored one");

            return true;
        } catch (Exception e) { LOGGER.error(IT, "Integrity check failed, exception occurred on file '{}'", targetFile.toPath(), e); }

        return false;
    }

    private static byte[] calculateDigest(InputStream inputStream, MessageDigest md) throws IOException {
        try (DigestInputStream dis = new DigestInputStream(inputStream, md)) {
            byte[] buffer = new byte[8192];
            while (dis.read(buffer) != -1);

            dis.close();
            return md.digest();
        } catch (Exception e) {
            throw new IOException("Failed calculating digest", e);
        }
    }

    public enum OsArch {
        WIN_X32("win", "x32", ".dll", false),
        WIN_X64("win", "x64", ".dll", true),
        MAC_X64("mac", "x64", ".dylib", false),
        NIX_X64("nix", "x64", ".os", false),

        WIN_ARM64("win", "arm64", ".dll", false),
        MAC_ARM64("mac", "arm64", ".dylib", false),
        NIX_ARM64("nix", "arm64", ".os", false),

        WIN_ARM("win", "arm", ".dll", false),
        MAC_ARM("mac", "arm", ".dylib", false),
        NIX_ARM("nix", "arm", ".os", false),

        DUMMY("dummy", "dummy", ".dummy", false)
        ;

        public final String OS;
        public final String ARCH;
        public final String EXT;
        public final boolean wrapped;
        OsArch(String os, String arch, String ext, boolean isWrapped) {
            OS = os;
            ARCH = arch;
            EXT = ext;
            wrapped = isWrapped;
        }

        @Override
        public String toString() {
            return OS + "-" + ARCH;
        }
    }

    public static String getUserAgentBasedOnOS() {
        return "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/112.0.0.0 Safari/537.36 Edg/112.0.1722.68";
    }
}
