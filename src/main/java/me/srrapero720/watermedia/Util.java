package me.srrapero720.watermedia;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import me.lib720.caprica.vlcj.binding.support.runtime.RuntimeUtil;
import me.srrapero720.watermedia.api.external.GifDecoder;
import me.srrapero720.watermedia.api.external.ThreadUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import static me.srrapero720.watermedia.WaterMedia.LOGGER;

public class Util {
    private static final Marker IT = MarkerFactory.getMarker("Util");

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
    public static InputStream resourceAsStream(String path, ClassLoader from) { return from.getResourceAsStream(path); }

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

        } catch (Exception e) { LOGGER.error(IT, "Exception trying to read JSON from {}", path, e);}

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
            LOGGER.error(IT, "Failed to extract from {} to {} due to unexpected error: {}", originPath, destinationPath, e);
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

    /**
     * Created by CreativeMD
     * @param image picture to process
     * @param width picture width
     * @param height picture height
     * @return textureID
     */
    public static int preRender(BufferedImage image, int width, int height) {
        int[] pixels = new int[width * height];
        image.getRGB(0, 0, width, height, pixels, 0, width);
        boolean alpha = false;

        if (image.getColorModel().hasAlpha()) for (int pixel : pixels)
            if ((pixel >> 24 & 0xFF) < 0xFF) {
                alpha = true;
                break;
            }

        int bytesPerPixel = alpha ? 4 : 3;
        var buffer = BufferUtils.createByteBuffer(width * height * bytesPerPixel);
        for (int pixel : pixels) {
            buffer.put((byte) ((pixel >> 16) & 0xFF)); // Red
            buffer.put((byte) ((pixel >> 8) & 0xFF)); // Green
            buffer.put((byte) (pixel & 0xFF)); // Blue
            if (alpha) buffer.put((byte) ((pixel >> 24) & 0xFF)); // Alpha
        }
        buffer.flip();

        int textureID = GL11.glGenTextures(); //Generate texture ID
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID); // Bind texture ID
//        RenderSystem.bindTexture(textureID); // unsafe for other versions - Bind texture ID

        //Setup wrap mode
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);

        //Setup texture scaling filtering
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);

        if (!alpha) GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, GL11.GL_ONE);

        // prevents random crash, when values are too high it causes a jvm crash, caused weird behavior when game is paused
        GL11.glPixelStorei(GL11.GL_UNPACK_ROW_LENGTH, GL11.GL_ZERO);
        GL11.glPixelStorei(GL11.GL_UNPACK_SKIP_PIXELS, GL11.GL_ZERO);
        GL11.glPixelStorei(GL11.GL_UNPACK_SKIP_ROWS, GL11.GL_ZERO);

        //Send texel data to OpenGL
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, alpha ? GL11.GL_RGBA8 : GL11.GL_RGB8, width, height, 0, alpha ? GL11.GL_RGBA : GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, buffer);

        //Return the texture ID so we can bind it later again
        return textureID;
    }

    public static boolean integrityCheck(InputStream sourceStream, File targetFile) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");

            byte[] sourceDigest = calculateDigest(sourceStream, md);
            byte[] targetDigest = calculateDigest(new FileInputStream(targetFile), md);

            boolean equal = MessageDigest.isEqual(sourceDigest, targetDigest);
            if (!equal) LOGGER.error(IT, "Integrity check failed: File '{}' no match with the current", targetFile.toPath());

            return equal;
        } catch (Exception e) { LOGGER.error(IT, "Integrity check failed: Exception occurred on file '{}'", targetFile.toPath(), e); }

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

    public static String getOsBinExtension() {
        if (RuntimeUtil.isWindows()) return ".dll";
        if (RuntimeUtil.isMac()) return ".dylib";
        if (RuntimeUtil.isNix()) return ".os";
        return "";
    }

    public static String getUserAgentBasedOnOS() {
//        var winLegacy = "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:25.0) Gecko/20100101 Firefox/25.0";
        //        var macEdge = "Mozilla/5.0 (Macintosh; Intel Mac OS X 13_3_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/113.0.0.0 Safari/537.36 Edg/112.0.1722.71";
//        var linuxFirefox = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Ubuntu Chromium/113.0.0.0 Chrome/113.0.0.0 Safari/537.36";
        return "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/112.0.0.0 Safari/537.36 Edg/112.0.1722.68";
    }
}
