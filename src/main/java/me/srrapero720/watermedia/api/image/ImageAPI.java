package me.srrapero720.watermedia.api.image;

import me.lib720.madgag.gif.fmsware.GifDecoder;
import me.srrapero720.watermedia.WaterMedia;
import me.srrapero720.watermedia.api.loader.IMediaLoader;
import me.srrapero720.watermedia.core.tools.FileTool;
import me.srrapero720.watermedia.core.tools.JarTool;
import me.srrapero720.watermedia.core.tools.exceptions.ReInitException;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

import static me.srrapero720.watermedia.WaterMedia.LOGGER;

public class ImageAPI {
    private static final Marker IT = MarkerManager.getMarker("ImageAPI");

    // IMAGE STORAGE
    private static final Map<String, ImageRenderer> LOADING_CACHE = new HashMap<>();
    private static ImageRenderer IMG_LOADING;
    private static ImageRenderer IMG_VLC_FAIL;
    private static ImageRenderer IMG_VLC_FAIL_LAND;

    // VLC
    public static ImageRenderer failedVLC() { return IMG_VLC_FAIL; }
    public static ImageRenderer failedVLCLandscape() { return IMG_VLC_FAIL_LAND; }

    // LOADING GIFs
    public static ImageRenderer loadingGif() { return IMG_LOADING; }
    /**
     * Search for a custom loading gif for a mod, using mod id for unique storage.<br><br>
     * Your picture is stored in memory CACHE, but is preferred to save it in a static and constant
     * variable to skip all method logic
     * @param modId unique identifier (preferred of a mod)
     * @return instance of the picture in a renderer
     */
    public static ImageRenderer loadingGif(String modId) {
        Path processDir = WaterMedia.getInstance().loader().processPath();
        Path modConfig = processDir.resolve("config/watermedia/assets/" + modId + "/loading.gif");

        if (Files.exists(modConfig)) {
            ImageRenderer renderer = LOADING_CACHE.get(modId);
            if (renderer != null) return renderer;

            renderer = imageRenderer(FileTool.readGif(modConfig.toAbsolutePath()));
            LOADING_CACHE.put(modId, renderer);
        }
        if (modConfig.getParent().toFile().mkdirs()) LOGGER.warn(IT, "Custom loading gif not found, creating directories and returning default one");
        else LOGGER.error(IT, "Custom loading gif not found, directories cannot be created");
        return IMG_LOADING;
    }

    /**
     * Gets a cache for a URL
     * if no exists then creates an unready one
     * @param originalURL url of the picture
     * @param renderThreadEx concurrent executor
     * @return cache instance, if url was null or empty then returns an empty instance
     */
    public static ImageCache getCache(String originalURL, Executor renderThreadEx) {
        if (originalURL == null || originalURL.isEmpty()) return ImageCache.EMPTY_INSTANCE;

        ImageCache image = ImageCache.CACHE.get(originalURL);
        image = (image == null) ? new ImageCache(originalURL, renderThreadEx) : image.use();
        ImageCache.CACHE.put(originalURL, image);
        return image;
    }

    /**
     * Reloads all ImageCache instanced
     * This might cause lag
     */
    public static void reloadCache() {
        for (ImageCache imageCache : ImageCache.CACHE.values()) { imageCache.reload(); }
    }

    /**
     * Creates an instance of an ImageRenderer only for static pictures
     * @param image image to use
     * @param absolute disabled flush and release methods, by default false
     *                 this param uses a hacky to make it "optional".
     *                 The first value is relevant.
     *                 By default, false
     * @return built instance
     */
    public static ImageRenderer imageRenderer(BufferedImage image, boolean ...absolute) {
        if (absolute.length != 0 && absolute[0]) return new ImageRenderer(image);
        return new ImageRenderer(image) {
            @Override public void flush() {}
            @Override public void release() {}
        };
    }

    /**
     * Creates an instance of an ImageRenderer only for gifs
     * @param image image to use
     * @param absolute disabled flush and release methods, by default false
     *                 this param uses a hacky to make it "optional".
     *                 The first value is relevant.
     *                 By default, false
     * @return built instance
     */
    public static ImageRenderer imageRenderer(GifDecoder image, boolean ...absolute) {
        if (absolute.length != 0 && absolute[0]) return new ImageRenderer(image);
        return new ImageRenderer(image) {
            @Override public void flush() {}
            @Override public void release() {}
        };
    }

    public static void init(IMediaLoader loader) throws ReInitException {
        if (IMG_LOADING != null) throw new ReInitException("ImageAPI");

        LOGGER.info(IT, "Loading image resources in a {} instance", ImageRenderer.class.getSimpleName());
        ClassLoader cl = ImageAPI.class.getClassLoader();

        IMG_LOADING = imageRenderer(FileTool.readGif(loader.processPath().resolve("config/watermedia/assets/loading.gif")), true);
        IMG_VLC_FAIL = imageRenderer(JarTool.readImage(cl, "/pictures/videolan/failed.png"), true);
        IMG_VLC_FAIL_LAND = imageRenderer(JarTool.readImage(cl, "/pictures/videolan/failed-land.png"), true);
    }
}