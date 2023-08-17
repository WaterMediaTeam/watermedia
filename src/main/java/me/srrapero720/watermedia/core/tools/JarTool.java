package me.srrapero720.watermedia.core.tools;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import me.lib720.madgag.gif.fmsware.GifDecoder;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static me.srrapero720.watermedia.WaterMedia.LOGGER;

public class JarTool {
    public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/112.0.0.0 Safari/537.36 Edg/112.0.1722.68";
    static final Marker IT = MarkerManager.getMarker("JarUtil");

    public static boolean copyAsset(ClassLoader loader, String origin, String dest) {
        try (InputStream is = readResource(loader, origin)) {
            Path dllDestinationPath = Paths.get(dest);
            if (is == null) throw new FileNotFoundException("Resource was not found in " + origin);

            Files.createDirectories(dllDestinationPath.getParent());
            Files.copy(is, dllDestinationPath, StandardCopyOption.REPLACE_EXISTING);
            return true;
        } catch (Exception e) {
            LOGGER.error(IT, "### Failed to extract from (JAR) {} to {} due to unexpected error", origin, dest);
            if (LOGGER.isDebugEnabled()) LOGGER.debug(IT, "### Information", e);
            else LOGGER.error("### Information", e);
        }
        return false;
    }

    public static List<String> readStringList(ClassLoader loader, String path) {
        List<String> result = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(readResource(loader, path)))) {
            result.addAll(new Gson().fromJson(reader, new TypeToken<List<String>>() {}.getType()));
        } catch (Exception e) {
            LOGGER.error(IT, "### Exception trying to read JSON from {}", path, e);
        }

        return result;
    }

    public static BufferedImage readImage(ClassLoader loader, String path) {
        try (InputStream in = readResource(loader, path)) {
            BufferedImage image = ImageIO.read(Objects.requireNonNull(in));
            if (image != null) return image;
            else throw new FileNotFoundException("Image read from WaterMedia resources was NULL");
        } catch (Exception e) {
            throw new IllegalStateException("Failed loading BufferedImage from WaterMedia resources", e);
        }
    }

    public static GifDecoder readGif(ClassLoader loader, String path) {
        try (InputStream inputStream = readResource(loader, path); ByteArrayInputStream in = (inputStream != null) ? new ByteArrayInputStream(IOUtils.toByteArray(inputStream)) : null) {
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

    public static InputStream readResource(ClassLoader loader, String source) {
        InputStream is = loader.getResourceAsStream(source);
        if (is == null && source.startsWith("/")) is = loader.getResourceAsStream(source.substring(1));
        return is;
    }
}
