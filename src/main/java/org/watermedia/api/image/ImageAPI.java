package org.watermedia.api.image;

import org.watermedia.WaterMedia;
import org.watermedia.api.WaterMediaAPI;
import org.watermedia.api.image.decoders.GifDecoder;
import org.watermedia.api.math.MathAPI;
import org.watermedia.core.tools.IOTool;
import org.watermedia.core.tools.JarTool;
import org.watermedia.loaders.ILoader;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

import static org.watermedia.WaterMedia.LOGGER;

public class ImageAPI extends WaterMediaAPI {
    public static final Marker IT = MarkerManager.getMarker(ImageAPI.class.getSimpleName());

    // IMAGE STORAGE
    private static final Map<String, ImageRenderer> LOADING_CACHE = new HashMap<>();
    private static ImageRenderer IMG_LOADING;
    private static ImageRenderer IMG_VLC_FAIL;
    private static ImageRenderer IMG_VLC_FAIL_LAND;
    private static ImageRenderer IMG_BLACK;

    private static ImageCache IMG_CACHE_LOADING;
    private static ImageCache IMG_CACHE_VLC_FAIL;
    private static ImageCache IMG_CACHE_VLC_FAIL_LAND;

    // VLC
    public static ImageRenderer failedVLC() { return IMG_VLC_FAIL; }
    public static ImageRenderer failedVLCLandscape() { return IMG_VLC_FAIL_LAND; }

    // VLC
    public static ImageCache cacheFailedVLC() { return IMG_CACHE_VLC_FAIL; }
    public static ImageCache cacheFailedVLCLandscape() { return IMG_CACHE_VLC_FAIL_LAND; }

    // LOADING GIFs
    public static ImageRenderer loadingGif() { return IMG_LOADING; }
    public static ImageRenderer blackPicture() { return IMG_BLACK; }

    public static ImageCache cacheLoadingGif() { return IMG_CACHE_LOADING; }

    /**
     * Search for a custom loading gif for a mod, using mod id for unique storage.<br><br>
     * Your picture is stored in memory CACHE, but is preferred to save it in a static constant
     * variable to skip all IO logic
     * @param modId unique identifier (preferred of a mod)
     * @return instance of the picture in a renderer
     */
    public static ImageRenderer loadingGif(String modId) {
        Path processDir = WaterMedia.getLoader().processDir();
        Path modConfig = processDir.resolve("config/watermedia/assets/" + modId + "/loading.gif");

        if (Files.exists(modConfig)) {
            ImageRenderer renderer = LOADING_CACHE.get(modId);
            if (renderer != null) {
                LOGGER.warn(IT, "Founded cached reference of '{}' loading gif, consider storing it in a STATIC param", modId);
                return renderer;
            }

            renderer = renderer(IOTool.readGif(modConfig.toAbsolutePath()));
            LOADING_CACHE.put(modId, renderer);
            return renderer;
        } else {
            File modConfigParent = modConfig.getParent().toFile();
            if (!modConfigParent.exists()) {
                LOGGER.warn(IT, "Custom loading gif not found, creating directories and returning default one");
                if (!modConfigParent.mkdirs()) LOGGER.error(IT, "Cannot create dir '{}'", modConfigParent.toString());
            } else {
                LOGGER.error(IT, "Founded directories but custom loading gif is missing");
            }
        }
        return IMG_LOADING;
    }

    /**
     * Gets a cache for a URL
     * if no exists then creates an unready one
     * @param uri url of the picture
     * @param renderThreadEx concurrent executor
     * @return cache instance, if url was null or empty then returns also null
     */
    public static ImageCache getCache(URI uri, Executor renderThreadEx) {
        if (uri == null) return null;

        ImageCache image = ImageCache.CACHE.get(uri);
        image = (image == null) ? new ImageCache(uri, renderThreadEx) : image.use();
        ImageCache.CACHE.put(uri, image);
        return image;
    }

    /**
     * Creates a custom ImageCache instance
     * WARNING: this instnace is NOT catched internally, this requires your own catching system
     * @param renderer Previous created image renderer
     * @see ImageRenderer
     * @see ImageAPI#renderer(BufferedImage)
     * @return ImageCache instance with the renderer and no fetch
     */
    public static ImageCache createCache(ImageRenderer renderer) {
        return new ImageCache(renderer);
    }

    /**
     * Creates a custom ImageCache instance
     * WARNING: this instance is NOT cached internally, this requires your own caching system
     * @param image BufferedImage instance to store in the Renderer
     * @param absolute if the renderer should be released running {@link ImageRenderer#release()} nor {@link ImageRenderer#flush()}
     * @return ImageCache instance with the renderer and no fetch
     */
    public static ImageCache createCache(BufferedImage image, boolean absolute) {
        return new ImageCache(renderer(image, absolute));
    }

    /**
     * Reloads all ImageCache instanced
     * This might cause lag
     */
    public static void reloadCache() {
        for (ImageCache imageCache: ImageCache.CACHE.values()) { imageCache.reload(); }
    }

    /**
     * Creates an instance of an ImageRenderer only for static pictures
     * It reads the specified path located in your jar such as "pictures/image.png".
     * Throws an exception if it can't read it
     * @param jarPath path of the picture
     * @param classLoader classloader of your jar, some module system uses isolated classloaders
     * @return ImageRenderer instance
     */
    public static ImageRenderer renderer(String jarPath, ClassLoader classLoader) {
        return renderer(jarPath, classLoader, false);
    }

    /**
     * Creates an instance of an ImageRenderer only for static pictures
     * It reads the specified path located in your jar such as "pictures/image.png".
     * Throws an exception if it can't read it
     * @param jarPath path of the picture
     * @param classLoader classloader of your jar, some module system uses isolated classloaders
     * @param absolute disables flushing and release methods, by default false
     * @return ImageRenderer instance
     */
    public static ImageRenderer renderer(String jarPath, ClassLoader classLoader, boolean absolute) {
        if (jarPath.endsWith(".gif")) {
            return renderer(JarTool.readGif(jarPath, classLoader), absolute);
        } else {
            return renderer(JarTool.readImage(jarPath, classLoader), absolute);
        }
    }


    /**
     * Creates an instance of an ImageRenderer only for static pictures
     * @param image image to use
     * @return built instance
     */
    public static ImageRenderer renderer(BufferedImage image) {
        return renderer(image, false);
    }

    /**
     * Creates an instance of an ImageRenderer only for static pictures
     * @param image image to use
     * @param absolute disables flushing and release methods, by default false
     * @return built instance
     */
    public static ImageRenderer renderer(BufferedImage image, boolean absolute) {
        if (absolute) return new ImageRenderer(image);
        return new ImageRenderer.Absolute(image);
    }

    /**
     * Creates an instance of an ImageRenderer only for static pictures
     * @param image image to use
     * @param delays delays between images
     * @return built instance
     */
    public static ImageRenderer renderer(BufferedImage[] image, long[] delays) {
        return renderer(image, delays, false);
    }

    /**
     * Creates an instance of an ImageRenderer only for static pictures
     * @param image image to use
     * @param delays delays between images
     * @param absolute disabled flush and release methods, by default false
     * @return built instance
     */
    public static ImageRenderer renderer(BufferedImage[] image, long[] delays, boolean absolute) {
        if (absolute) return new ImageRenderer(image, delays);
        return new ImageRenderer.Absolute(image, delays);
    }

    /**
     * Creates an instance of an ImageRenderer only for gifs
     * @param image image to use
     * @return built instance
     */
    public static ImageRenderer renderer(GifDecoder image) {
        return renderer(image, false);
    }

    /**
     * Creates an instance of an ImageRenderer only for gifs
     * @param image image to use
     * @param absolute disabled flush and release methods, by default false
     * @return built instance
     */
    public static ImageRenderer renderer(GifDecoder image, boolean absolute) {
        if (!absolute) return new ImageRenderer(image);
        return new ImageRenderer.Absolute(image);
    }

    private Path loadingGifPath;
    @Override
    public Priority priority() {
        return Priority.HIGH;
    }

    @Override
    public boolean prepare(ILoader bootCore) throws Exception {
        this.loadingGifPath = bootCore.processDir().resolve("config/watermedia/assets/loading.gif");
        if (loadingGifPath.toFile().exists()) {
            LOGGER.info(IT, "Detected custom loading gif... overriding default one");
        } else {
            this.loadingGifPath = null;
        }
        return true;
    }

    @Override
    public void start(ILoader bootCore) throws Exception {
        if (IMG_LOADING != null) {
            // TODO: release images and try again
        }

        LOGGER.info(IT, "Loading image resources in a {} instance", ImageRenderer.class.getSimpleName());

        IMG_LOADING = renderer(loadingGifPath != null ? IOTool.readGif(loadingGifPath) : JarTool.readGif("/pictures/loading.gif"), true);
        IMG_VLC_FAIL = renderer(JarTool.readGif("/pictures/videolan/failed.gif"), true);
        IMG_VLC_FAIL_LAND = renderer(JarTool.readGif("/pictures/videolan/failed-land.gif"), true);

        IMG_CACHE_LOADING = createCache(IMG_LOADING);
        IMG_CACHE_VLC_FAIL = createCache(IMG_VLC_FAIL);
        IMG_CACHE_VLC_FAIL_LAND = createCache(IMG_VLC_FAIL_LAND);

        BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        image.setRGB(0, 0, MathAPI.argb(255, 0, 0, 0));
        IMG_BLACK = renderer(image);

    }

    @Override
    public void release() {
        IMG_LOADING.release();
        IMG_VLC_FAIL.release();
        IMG_VLC_FAIL_LAND.release();
        IMG_BLACK.release();
        IMG_LOADING = null;
        IMG_VLC_FAIL = null;
        IMG_VLC_FAIL_LAND = null;
        IMG_BLACK = null;
    }
}