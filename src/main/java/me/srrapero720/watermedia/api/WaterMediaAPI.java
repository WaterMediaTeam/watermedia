package me.srrapero720.watermedia.api;

import me.lib720.caprica.vlcj.factory.MediaPlayerFactory;
import me.lib720.caprica.vlcj.factory.discovery.NativeDiscovery;
import me.srrapero720.watermedia.api.loader.IMediaLoader;
import me.srrapero720.watermedia.api.url.*;
import me.srrapero720.watermedia.api.image.ImageRenderer;
import me.srrapero720.watermedia.api.player.VideoPlayer;
import me.srrapero720.watermedia.core.tools.FileTool;
import me.srrapero720.watermedia.core.tools.JarTool;
import me.lib720.watermod.ThreadCore;
import me.srrapero720.watermedia.core.VideoLAN;
import me.srrapero720.watermedia.core.tools.ReflectTool;
import me.srrapero720.watermedia.core.tools.annotations.Experimental;
import me.srrapero720.watermedia.core.tools.annotations.Untested;
import me.srrapero720.watermedia.core.tools.exceptions.ReloadingException;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import static me.srrapero720.watermedia.WaterMedia.LOGGER;

import java.awt.image.BufferedImage;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.*;

public final class WaterMediaAPI {
    private static final Marker IT = MarkerManager.getMarker("API");
    private static final List<URLFixer> URLFIXERS = new ArrayList<>();

    // RESOURCES
    private static ImageRenderer IMG_LOADING;
    public static ImageRenderer img_getLoading() { return IMG_LOADING; }
    private static ImageRenderer IMG_VLC_FAIL;
    public static ImageRenderer img_getFailedVLC() { return IMG_VLC_FAIL; }
    private static ImageRenderer IMG_VLC_FAIL_LAND;
    public static ImageRenderer img_getLandFailedVLC() { return IMG_VLC_FAIL_LAND; }

    public static void init(IMediaLoader loader) throws ReloadingException {
        if (!URLFIXERS.isEmpty()) throw new ReloadingException(IT.getName());

        LOGGER.warn(IT,"Loading {}", URLFixer.class.getSimpleName());
        url_registerFixer(
                new YoutubeFixer(),
                new TwitchFixer(),
                new KickFixer(),
                new DriveFixer(),
                new OnedriveFixer(),
                new DropboxFixer(),
                new TwitterFixer()
        );


        ThreadCore.trySimple(() -> {
            if (IMG_LOADING != null) return;

            LOGGER.info(IT, "Loading image resources in a {} instance", ImageRenderer.class.getSimpleName());
            IMG_LOADING = new ImageRenderer(FileTool.readGif(loader.getProcessDirectory(), "/pictures/loading.gif"));
            IMG_VLC_FAIL = new ImageRenderer(JarTool.readImage(loader.getModuleClassLoader(), "/pictures/videolan/failed.png"));
            IMG_VLC_FAIL_LAND = new ImageRenderer(JarTool.readImage(loader.getModuleClassLoader(), "/pictures/videolan/failed-land.png"));
        }, e -> LOGGER.error(IT, "Failed to load image resources", e));
    }

    /**
     * 1 seconds in Minecraft equals 20 ticks
     * 20x50 equals 1000ms (1 sec)
     *
     * @param ticks Minecraft Tick count
     * @return ticks converted to MS
     */
    public static long math_ticksToMillis(int ticks) { return ticks * 50L; }

    /**
     * 1000ms (1 sec) equals 20 ms in Minecraft
     * 1000/50 equals 20 Ticks (1 sec)
     *
     * @param ms Time in milliseconds
     * @return Milliseconds converted to Ticks
     */
    public static int math_millisToTicks(long ms) { return (int) (ms / 50L); }

    /**
     * Calculates time for rendering a picture based on a 20 tps math
     * 20 tick equals 1 second, depending on given tick time, image delay and duration
     * do more calculations including delta-time and if was in LOOP
     * @param renderer image to get time
     * @param tick game tick (20t = 1s)
     * @param deltaTime time scale
     * @param loop if tick reach max gif duration should restart it or keep last frame
     * @return texture time
     */
    public static long math_textureTime(ImageRenderer renderer, int tick, long deltaTime, boolean loop) {
        long time = tick * 50L + deltaTime;
        long duration = renderer.duration;
        if (duration > 0 && time > duration && loop) time %= duration;
        return time;
    }

    public static int math_colorARGB(int a, int r, int g, int b) { return (a << 24) | (r << 16) | (g << 8) | b; }

    public static int api_getTexture(ImageRenderer renderer, int tick, long deltaTime, boolean loop) {
        return renderer.texture(math_textureTime(renderer, tick, deltaTime, loop));
    }


    /**
     * Check if any String is a valid URL
     * @param url the URL in a string
     * @return if is valid.
     */
    public static boolean url_isValid(String url) { return ThreadCore.tryAndReturn(defaultVar -> { new URL(url); return true; }, false); }


    /**
     * Creates your own URLPatch and register it to WaterMediaAPI
     * @param patch All patches you want to Use
     */
    public static void url_registerFixer(URLFixer...patch) {
        String[] names = new String[patch.length];
        for (int i = 0; i < patch.length; i++) {
            URLFIXERS.add(patch[i]);
            names[i] = patch[i].name();
        }
        LOGGER.warn(IT, "Fixers registered: {}", Arrays.toString(names));
        names = null;
    }

    /**
     * This method is used by default on {@link VideoPlayer#start(CharSequence, String[])}
     * Is not recommended external usages
     * @param stringUrl Media URL to patch
     * @return Media URL patched to be fully compatible with VLC (static resource)
     * @deprecated use {@link #url_fixURL(String)} instead
     */
    @Deprecated
    public static URL url_toURL(String stringUrl) {
        try {
            URL url = new URL(stringUrl);

            return ThreadCore.tryAndReturn(defaultVar -> {
                for (URLFixer compat: URLFIXERS) if (compat.isValid(url)) return compat.patch(url, null).url;
                return defaultVar;
            }, e -> LOGGER.error(IT, "Exception occurred trying to patch URL", e), url);
        } catch (Exception e) {
            LOGGER.error(IT, "Exception occurred instancing URL", e);
        }
        return null;
    }

    public static URLFixer.Result url_fixURL(String str) {
        try {
            URL url = new URL(str);
            return ThreadCore.tryAndReturn(defaultVar -> {
                for (URLFixer compat: URLFIXERS) if (compat.isValid(url)) return compat.patch(url, null);
                return defaultVar;
            }, e -> LOGGER.error(IT, "Exception occurred trying to fix URL", e), new URLFixer.Result(url, false, false));
        } catch (Exception e) {
            LOGGER.error(IT, "Exception occurred instancing URL", e);
        }
        return null;
    }

    /**
     * Fix URL string using URLFixers and returns a bundle data with the URL, type of and qualities
     * @param str URL to fix and convert into URL
     * @param ns This method should use NothingSpecialURLFixer!? be careful with this
     * @return Bundle data with the URL and info
     */
    @Experimental
    @Untested
    public static URLFixer.Result url_fixURL(String str, boolean ns) {
        return null;
    }

    /**
     * Gets all query params from a query
     * Only supports queries from {@link URL#getQuery()}
     * @param query query string
     * @return map with all values
     */
    public static Map<String, String> url_parseQuery(String query) {
        Map<String, String> queryParams = new HashMap<>();
        String[] params = query.split("&");
        for (String param : params) {
            String[] keyValue = param.split("=");
            if (keyValue.length == 2) {
                String key = keyValue[0];
                String value = keyValue[1];
                queryParams.put(key, value);
            }
        }
        return queryParams;
    }

    /**
     * Use your own VLCArgs at your own risk
     * By default this method makes a ReleaseHook to release everything after close Minecraft
     * Suggestion: Use the same VLC arguments for logging but with other filename
     * Example: <pre> "--logfile", "logs/vlc/mymod-latest.log",</pre>
     * @param vlcArgs arguments to make another VLC instance
     * @return a PlayerFactory to create custom VLC players. {@link VideoPlayer} can accept factory for new instances
     */
    public static MediaPlayerFactory vlc_createFactory(String[] vlcArgs) {
        NativeDiscovery discovery = new NativeDiscovery();
        if (discovery.discover()) {
            MediaPlayerFactory factory = new MediaPlayerFactory(discovery, vlcArgs);
            LOGGER.info(IT, "New instance of VLC loaded from '{}' with the next args:\n{}", discovery.discoveredPath(), Arrays.toString(vlcArgs));
            Runtime.getRuntime().addShutdownHook(new Thread(factory::release));
            return factory;
        }

        LOGGER.error(IT, "VLC was not found on your system.");
        return null;
    }

    /**
     * Check if VLC is loaded and ready to be used on {@link VideoPlayer} or to make
     * a new {@link MediaPlayerFactory} instance
     * @return if is reddy or not
     */
    public static boolean vlc_isReady() { return VideoLAN.getFactory() != null; }

    /**
     * Created by CreativeMD
     * @param image picture to process
     * @param width picture width
     * @param height picture height
     * @return textureID
     */
    public static int gl_genTexture(BufferedImage image, int width, int height) {
        int[] pixels = new int[width * height];
        image.getRGB(0, 0, width, height, pixels, 0, width);
        boolean alpha = false;

        if (image.getColorModel().hasAlpha()) for (int pixel : pixels)
            if ((pixel >> 24 & 0xFF) < 0xFF) {
                alpha = true;
                break;
            }

        int bytesPerPixel = alpha ? 4 : 3;
        ByteBuffer buffer = BufferUtils.createByteBuffer(width * height * bytesPerPixel);
        for (int pixel : pixels) {
            buffer.put((byte) ((pixel >> 16) & 0xFF)); // Red
            buffer.put((byte) ((pixel >> 8) & 0xFF)); // Green
            buffer.put((byte) (pixel & 0xFF)); // Blue
            if (alpha) buffer.put((byte) ((pixel >> 24) & 0xFF)); // Alpha
        }

        // FLIP method changes what class type returns in new JAVA versions, in runtime causes a JVM crash by that
        // THIS EXECUTES ByteBuffer#flip
        ReflectTool.executeMethod("flip", buffer.getClass(), buffer);

        int textureID = GL11.glGenTextures(); //Generate texture ID
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID); // Bind texture ID

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

        //Return the texture ID, so we can bind it later again
        return textureID;
    }

    public static int gl_applyBuffer(IntBuffer videoBuffer, int videoTexture, int videoWidth, int videoHeight, boolean firstFrame) {
        GL11.glPixelStorei(GL11.GL_UNPACK_ROW_LENGTH, GL11.GL_ZERO);
        GL11.glPixelStorei(GL11.GL_UNPACK_SKIP_PIXELS, GL11.GL_ZERO);
        GL11.glPixelStorei(GL11.GL_UNPACK_SKIP_ROWS, GL11.GL_ZERO);

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, videoTexture);
        if (firstFrame) {GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, videoWidth, videoHeight, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, videoBuffer);
        } else GL11.glTexSubImage2D(GL11.GL_TEXTURE_2D, 0, 0, 0, videoWidth, videoHeight, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, videoBuffer);
        return videoTexture;
    }
}