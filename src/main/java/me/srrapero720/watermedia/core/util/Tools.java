package me.srrapero720.watermedia.core.util;

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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static me.srrapero720.watermedia.WaterMedia.LOGGER;

public class Tools {
    private static final Marker IT = MarkerFactory.getMarker("Util");
    private static final ClassLoader LOADER = Thread.currentThread().getContextClassLoader();
    private static final WaterOs ARCH = getArch();
    public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/112.0.0.0 Safari/537.36 Edg/112.0.1722.68";

    /**
     * Gets a List[String] from a json inside WaterMedia jar resources
     * @param path where is located the specific JSON
     * @return a List[String] with the JSON content
     */
    public static List<String> getJsonListFromRes(String path) {
        List<String> result = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(LOADER.getResourceAsStream(path))))) {
            result.addAll(new Gson().fromJson(reader, new TypeToken<List<String>>() {}.getType()));
        } catch (Exception e) {
            LOGGER.error(IT, "### Exception trying to read JSON from {}", path);
            if (LOGGER.isDebugEnabled()) LOGGER.debug(IT, "### Information", e);
            else LOGGER.error("### Information", e);
        }

        return result;
    }

    public static void extractFrom(String originPath, String destinationPath) {
        try (InputStream is = LOADER.getResourceAsStream(originPath)) {
            Path dllDestinationPath = Paths.get(destinationPath);
            if (is != null) {
                Files.createDirectories(dllDestinationPath.getParent());
                Files.copy(is, dllDestinationPath);
            } else {
                throw new RuntimeException("Resource was not found in " + originPath);
            }
        } catch (Exception e) {
            LOGGER.error(IT, "### Failed to extract from (JAR) {} to {} due to unexpected error", originPath, destinationPath);
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

    public static BufferedImage getImageFromResources(String path) {
        try (InputStream in = LOADER.getResourceAsStream(path)) {
            var image = ImageIO.read(Objects.requireNonNull(in));
            if (image != null) return image;
            else throw new NullPointerException("Image read from WaterMedia resources was NULL");
        } catch (Exception e) {
            throw new IllegalStateException("Failed loading BufferedImage from WaterMedia resources", e);
        }
    }

    public static GifDecoder getGifFromResources(String path) {
        try (InputStream inputStream = LOADER.getResourceAsStream(path); ByteArrayInputStream in = (inputStream != null) ? new ByteArrayInputStream(IOUtils.toByteArray(inputStream)) : null) {
            GifDecoder gif = new GifDecoder();
            int status = gif.read(in);

            if (status == GifDecoder.STATUS_OK) {
                return gif;
            } else {
                LOGGER.error(IT, "Exception reading Gif from {}", path);
                throw new IOException("Failed to read/process gif, status code " + status);
            }
        } catch (Exception e) {
            LOGGER.error("Failed loading GIF from WaterMedia resources", e);
            return null;
        }
    }

    public static boolean integrityFrom(String source, File targetFile) {
        try (InputStream sourceStream = LOADER.getResourceAsStream(source)) {
            MessageDigest md = MessageDigest.getInstance("MD5");

            byte[] sourceDigest = digest(sourceStream, md);
            byte[] targetDigest = digest(new FileInputStream(targetFile), md);
            if (!MessageDigest.isEqual(sourceDigest, targetDigest)) throw new RuntimeException("File no match with the stored one");

            return true;
        } catch (Exception e) {
            LOGGER.error(IT, "Integrity check failed, exception occurred on file '{}'", targetFile.toPath());
            LOGGER.debug(IT, "DETECTED ERROR", e);
        }

        return false;
    }

    private static byte[] digest(InputStream inputStream, MessageDigest md) {
        try (DigestInputStream dis = new DigestInputStream(inputStream, md)) {
            byte[] buffer = new byte[8192];
            while (dis.read(buffer) != -1);

            dis.close();
            return md.digest();
        } catch (Exception e) {
            throw new IllegalStateException("Failed calculating digest", e);
        }
    }

    public static WaterOs getArch() {
        if (ARCH != null) return ARCH;
        String arch = System.getProperty("os.arch");
        if ((arch.equals("amd64") || arch.equals("x86_64"))) {
            if (RuntimeUtil.isWindows()) return WaterOs.WIN_X64;
            if (RuntimeUtil.isMac()) return WaterOs.MAC_X64;
            if (RuntimeUtil.isNix()) return WaterOs.NIX_X64;
        } else if (arch.equals("arm64")) {
            if (RuntimeUtil.isWindows()) return WaterOs.WIN_ARM64;
            if (RuntimeUtil.isMac()) return WaterOs.MAC_ARM64;
            if (RuntimeUtil.isNix()) return WaterOs.NIX_ARM64;
        }
        throw new RuntimeException("Running Minecraft in a unknown arch");
    }

    public enum WaterOs {
        WIN_X64("win", "x64", ".dll", true),
        WIN_ARM64("win", "arm64", ".dll", false),
        MAC_X64("mac", "x64", ".dylib", false),
        MAC_ARM64("mac", "arm64", ".dylib", false),
        NIX_X64("nix", "x64", ".os", false),
        NIX_ARM64("nix", "arm64", ".os", false)
        ;

        public final String OS, ARCH, EXT;
        public final boolean wrapped;
        WaterOs(String os, String arch, String ext, boolean isWrapped) {
            OS = os;
            ARCH = arch;
            EXT = ext;
            wrapped = isWrapped;
        }

        @Override
        public String toString() { return OS + "-" + ARCH; }
    }
}
