package me.srrapero720.watermedia.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import me.lib720.madgag.gif.fmsware.GifDecoder;
import netscape.javascript.JSObject;
import org.apache.commons.io.IOUtils;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static me.srrapero720.watermedia.WaterMedia.LOGGER;

public class AssetsUtil {
    static final Marker IT = MarkerFactory.getMarker("Util");
    public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/112.0.0.0 Safari/537.36 Edg/112.0.1722.68";

    public static void copyAsset(ClassLoader loader, String origin, Path dest) {
        copyAsset(loader, origin, dest.toString());
    }

    public static void copyAsset(ClassLoader loader, String origin, String dest) {
        try (InputStream is = loader.getResourceAsStream(origin)) {
            Path dllDestinationPath = Paths.get(dest);
            if (is == null) throw new FileNotFoundException("Resource was not found in " + origin);

            Files.createDirectories(dllDestinationPath.getParent());
            Files.copy(is, dllDestinationPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            LOGGER.error(IT, "### Failed to extract from (JAR) {} to {} due to unexpected error", origin, dest);
            if (LOGGER.isDebugEnabled()) LOGGER.debug(IT, "### Information", e);
            else LOGGER.error("### Information", e);
        }
    }

    public static String getString(Path from) {
        try {
            byte[] bytes = Files.readAllBytes(from);
            return new String(bytes, Charset.defaultCharset());
        } catch (Exception e) {
            return null;
        }
    }

    public static List<String> getStringList(ClassLoader loader, String path) {
        List<String> result = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(loader.getResourceAsStream(path))))) {
            result.addAll(new Gson().fromJson(reader, new TypeToken<List<String>>() {}.getType()));
        } catch (Exception e) {
            LOGGER.error(IT, "### Exception trying to read JSON from {}", path);
            if (LOGGER.isDebugEnabled()) LOGGER.debug(IT, "### Information", e);
            else LOGGER.error("### Information", e);
        }

        return result;
    }

    public static BufferedImage getImage(ClassLoader loader, String path) {
        try (InputStream in = loader.getResourceAsStream(path)) {
            BufferedImage image = ImageIO.read(Objects.requireNonNull(in));
            if (image != null) return image;
            else throw new FileNotFoundException("Image read from WaterMedia resources was NULL");
        } catch (Exception e) {
            throw new IllegalStateException("Failed loading BufferedImage from WaterMedia resources", e);
        }
    }

    public static GifDecoder getGif(ClassLoader loader, String path) {
        try (InputStream inputStream = loader.getResourceAsStream(path); ByteArrayInputStream in = (inputStream != null) ? new ByteArrayInputStream(IOUtils.toByteArray(inputStream)) : null) {
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

}
