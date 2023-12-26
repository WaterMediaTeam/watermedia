package me.srrapero720.watermedia.api;

import me.lib720.watermod.reflect.ReflectTool;
import me.lib720.watermod.safety.TryCore;
import me.srrapero720.watermedia.WaterMedia;
import me.srrapero720.watermedia.api.image.ImageAPI;
import me.srrapero720.watermedia.api.image.ImageFetch;
import me.srrapero720.watermedia.api.image.ImageRenderer;
import me.srrapero720.watermedia.api.math.MathAPI;
import me.srrapero720.watermedia.api.player.PlayerAPI;
import me.srrapero720.watermedia.api.player.SyncBasePlayer;
import me.srrapero720.watermedia.api.rendering.RenderAPI;
import me.srrapero720.watermedia.api.url.UrlAPI;
import me.srrapero720.watermedia.api.url.fixers.URLFixer;
import me.srrapero720.watermedia.core.VideoLanCore;
import me.srrapero720.watermedia.core.tools.FileTool;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import uk.co.caprica.vlcj.factory.MediaPlayerFactory;

import java.awt.image.BufferedImage;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static me.srrapero720.watermedia.WaterMedia.LOGGER;

@Deprecated()
public final class WaterMediaAPI {
    public static final Marker IT = MarkerManager.getMarker("API");

    /**
     * Use instead {@link ImageAPI#loadingGif()}
     * @return Loading gif instanced in a renderer.
     */
    @Deprecated
    public static ImageRenderer img_getLoading() { return ImageAPI.loadingGif(); }

    /**
     * Use instead {@link ImageAPI#failedVLC()}
     * @return image of failed vlc instanced in a renderer.
     */
    @Deprecated
    public static ImageRenderer img_getFailedVLC() { return ImageAPI.failedVLC(); }

    /**
     * Use instead {@link ImageAPI#failedVLCLandscape()}
     * @return image in 16:9 of failed vlc instanced in a renderer.
     */
    @Deprecated
    public static ImageRenderer img_getLandFailedVLC() { return ImageAPI.failedVLCLandscape(); }

    /**
     * Returns loading gif picture for a specific MODID
     * to set up your custom loading gif you should store it on `/config/watermedia/assets/[modid]/loading.gif`
     * IMPORTANT: this method doesn't cache your picture, ensure you are loading it in a static final context
     * @param modId Identifier of third-party mod
     * @return a {@link ImageRenderer} instance with the right loading gif, if gif cannot be readed then returns watermedia's default
     * @deprecated Use {@link ImageAPI#loadingGif(String)} instead
     */
    @Deprecated
    public static ImageRenderer img_getLoading(String modId) {
        Path processDir = WaterMedia.getInstance().loader().processPath();
        Path modConfig = processDir.resolve("config/watermedia/assets/" + modId + "/loading.gif");

        if (Files.exists(modConfig)) return new ImageRenderer(FileTool.readGif(modConfig.toAbsolutePath()));
        if (modConfig.getParent().toFile().mkdirs()) LOGGER.warn(IT, "Custom loading gif not found, creating directories and returning default one");
        else LOGGER.error(IT, "Custom loading gif not found, directories cannot be created");
        return ImageAPI.loadingGif();
    }

    /**
     * 1 seconds in Minecraft equals 20 ticks
     * 20x50 equals 1000ms (1 sec)
     *
     * @param ticks Minecraft Tick count
     * @return ticks converted to MS
     * @deprecated use instead {@link MathAPI#tickToMs(int)}
     */
    @Deprecated
    public static long math_ticksToMillis(int ticks) { return ticks * 50L; }

    /**
     * 1000ms (1 sec) equals 20 ms in Minecraft
     * 1000/50 equals 20 Ticks (1 sec)
     *
     * @param ms Time in milliseconds
     * @return Milliseconds converted to Ticks
     * @deprecated Use instead {@link MathAPI#msToTick(long)}
     */
    @Deprecated
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
     * @deprecated use instead {@link ImageRenderer#texture(int, long, boolean)}
     */
    @Deprecated
    public static long math_textureTime(ImageRenderer renderer, int tick, long deltaTime, boolean loop) {
        long time = (tick * 50L) + deltaTime;
        long duration = renderer.duration;
        if (duration > 0 && time > duration && loop) time %= duration;
        return time;
    }

    /**
     * Creates a hexadecimal color based on gave params
     * All values needs to be in a range of 0 ~ 255
     * @param a Alpha
     * @param r Red
     * @param g Green
     * @param b Blue
     * @deprecated Use instead {@link MathAPI#getColorARGB(int, int, int, int)}
     * @return HEX color
     */
    @Deprecated
    public static int math_colorARGB(int a, int r, int g, int b) { return (a << 24) | (r << 16) | (g << 8) | b; }

    /**
     * Shot-cut of using {@link ImageRenderer#texture(long)} with {@link #math_textureTime(ImageRenderer, int, long, boolean)}
     * @param renderer instance of image
     * @param tick number of ticks
     * @param deltaTime scale time
     * @param loop overtime resets picture
     * @return texture ID usable on OpenGL
     * @deprecated use instead {@link ImageRenderer#texture(int, long, boolean)}
     */
    @Deprecated
    public static int api_getTexture(ImageRenderer renderer, int tick, long deltaTime, boolean loop) {
        return renderer.texture(math_textureTime(renderer, tick, deltaTime, loop));
    }


    /**
     * Check if String is a valid URL
     * @param url string to check
     * @return if is valid.
     * @deprecated use instead {@link UrlAPI#isValid(String)}
     */
    @Deprecated
    public static boolean url_isValid(String url) { return TryCore.withReturn(defaultVar -> { new URL(url); return true; }, false); }


    /**
     * Creates your own URLPatch and register it to WaterMediaAPI
     * @param patch All patches you want to use
     * @deprecated Use services instead. For more information, go to {@link URLFixer}
     * CLASS IS OBSOLETE and not works
     */
    @Deprecated
    public static void url_registerFixer(URLFixer...patch) {
        for (URLFixer f: patch) LOGGER.error(IT, "Custom fixer '{}' cannot be registered", f.name());
    }

    /**
     * List of all supported platforms by WATERMeDIA.
     * By default, this method doesn't include NothingSpecialFixers
     * @return array of all platforms names unsorted
     * @deprecated use instead {@link UrlAPI#getFixersPlatforms()}
     */
    @Deprecated
    public static String[] url_getFixersPlatform() {return UrlAPI.getFixersPlatforms(false); }

    /**
     * List of all supported platforms by WATERMeDIA
     * @param includeSpecials should list Special fixers too?
     * @return array of all platforms names unsorted
     * @deprecated use instead {@link UrlAPI#getFixersPlatforms(boolean)}
     */
    @Deprecated
    public static String[] url_getFixersPlatform(boolean includeSpecials) { return UrlAPI.getFixersPlatforms(includeSpecials); }

    /**
     * Used by default internally in the API by {@link SyncBasePlayer} and {@link ImageFetch}
     * Recommended usage just in API custom abstraction
     * IMPORTANT: NothingSpecialFixers are disabled on this method
     * @param str String to patch
     * @return result data with URL and type of it
     * @deprecated Use instead {@link UrlAPI#fixURL(String)}
     */
    @Deprecated
    public static URLFixer.Result url_fixURL(String str) {
        return url_fixURL(str, false);
    }


    /**
     * Used by default internally in the API by {@link SyncBasePlayer} and {@link ImageFetch}
     * Recommended usage just in API custom abstraction
     * @param str String to patch
     * @param ns Use NothingSpecial fixers too
     * @return result data with URL and type of it
     * @deprecated Use instead {@link UrlAPI#fixURL(String, boolean)}
     */
    @Deprecated
    public static URLFixer.Result url_fixURL(String str, boolean ns) {
        return UrlAPI.fixURL(str, ns);
    }

    /**
     * Gets all query params from a query
     * Only supports queries from {@link URL#getQuery()}
     * @param query query string
     * @return map with all values
     * @deprecated use instead {@link UrlAPI#parseQuery(String)}
     */
    @Deprecated
    public static Map<String, String> url_parseQuery(String query) {
        return UrlAPI.parseQuery(query);
    }

    /**
     * Gives you the default VLC MediaPlayerFactory created by API
     * @return WATERMeDIA's default MediaPlayerFactory
     * @deprecated Use instead {@link PlayerAPI#getVLCFactory()}
     */
    @Deprecated
    public static MediaPlayerFactory vlc_getFactory() {
        return VideoLanCore.factory();
    }

    /**
     * Use your own VLCArgs at your own risk
     * By default this method makes a ReleaseHook to release everything after close Minecraft
     * Suggestion: Use the same VLC arguments for logging but with other filename
     * Example: <pre> "--logfile", "logs/vlc/mymod-latest.log",</pre>
     * @param vlcArgs arguments to make another VLC instance
     * @return a PlayerFactory to create custom VLC players. {@link SyncBasePlayer} can accept factory for new instances
     * @deprecated No replacement, is better to use WaterMedia's default VLC factory
     *
     */
    @Deprecated
    public static MediaPlayerFactory vlc_createFactory(String[] vlcArgs) {
        return VideoLanCore.init$createFactory(vlcArgs);
    }

    /**
     * Check if VLC is loaded and ready to be used
     * @return if VLC was loaded
     * @deprecated Use instead {@link PlayerAPI#isReady()}
     */
    @Deprecated
    public static boolean vlc_isReady() { return VideoLanCore.factory() != null; }

    /**
     * Created by CreativeMD
     * Creates a new texture id based on a {@link BufferedImage} instance (used internally by {@link ImageRenderer}
     * @param image image to process
     * @param width buffer width (can be image width)
     * @param height buffer height (can be image height)
     * @deprecated use instead {@link RenderAPI#applyBuffer(BufferedImage, int, int)}
     * @return texture id for OpenGL
     */
    @Deprecated
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
        ReflectTool.invoke("flip", buffer.getClass(), buffer);

        int textureID = GL11.glGenTextures(); //Generate texture ID
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID); // Bind texture ID

        //Setup wrap mode
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);

        //Setup texture scaling filtering
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);

        if (!alpha) GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, GL11.GL_ONE);

        // prevents random crash; when values are too high it causes a jvm crash, caused weird behavior when game is paused
        GL11.glPixelStorei(GL11.GL_UNPACK_ROW_LENGTH, GL11.GL_ZERO);
        GL11.glPixelStorei(GL11.GL_UNPACK_SKIP_PIXELS, GL11.GL_ZERO);
        GL11.glPixelStorei(GL11.GL_UNPACK_SKIP_ROWS, GL11.GL_ZERO);

        //Send texel data to OpenGL
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, alpha ? GL11.GL_RGBA8 : GL11.GL_RGB8, width, height, 0, alpha ? GL11.GL_RGBA : GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, buffer);

        //Return the texture ID, so we can bind it later again
        return textureID;
    }

    /**
     * Process a buffer to be used in a OpenGL texture id
     * @param videoBuffer IntBuffer to be processed
     * @param videoTexture texture ID from OpenGL
     * @param videoWidth buffer width
     * @param videoHeight buffer height
     * @param firstFrame if was the first frame
     * @deprecated use instead {@link RenderAPI#applyBuffer(IntBuffer, int, int, int, boolean)}
     * @return same texture ID gave first
     */
    @Deprecated
    public static int gl_applyBuffer(IntBuffer videoBuffer, int videoTexture, int videoWidth, int videoHeight, boolean firstFrame) {
        GL11.glPixelStorei(GL11.GL_UNPACK_ROW_LENGTH, GL11.GL_ZERO);
        GL11.glPixelStorei(GL11.GL_UNPACK_SKIP_PIXELS, GL11.GL_ZERO);
        GL11.glPixelStorei(GL11.GL_UNPACK_SKIP_ROWS, GL11.GL_ZERO);

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, videoTexture);
        if (firstFrame) {GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, videoWidth, videoHeight, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, videoBuffer);
        } else GL11.glTexSubImage2D(GL11.GL_TEXTURE_2D, 0, 0, 0, videoWidth, videoHeight, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, videoBuffer);
        return videoTexture;
    }

    /**
     * Process a buffer to be used in a OpenGL texture id
     * @param videoBuffer ByteBuffer to be processed
     * @param videoTexture texture ID from OpenGL
     * @param videoWidth buffer width
     * @param videoHeight buffer height
     * @param firstFrame if was the first frame
     * @deprecated use instead {@link RenderAPI#applyBuffer(ByteBuffer, int, int, int, boolean)}
     * @return same texture ID gave first
     */
    @Deprecated
    public static int gl_applyBuffer(ByteBuffer videoBuffer, int videoTexture, int videoWidth, int videoHeight, boolean firstFrame) {
        GL11.glPixelStorei(GL11.GL_UNPACK_ROW_LENGTH, GL11.GL_ZERO);
        GL11.glPixelStorei(GL11.GL_UNPACK_SKIP_PIXELS, GL11.GL_ZERO);
        GL11.glPixelStorei(GL11.GL_UNPACK_SKIP_ROWS, GL11.GL_ZERO);

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, videoTexture);
        if (firstFrame) {GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, videoWidth, videoHeight, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, videoBuffer);
        } else GL11.glTexSubImage2D(GL11.GL_TEXTURE_2D, 0, 0, 0, videoWidth, videoHeight, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, videoBuffer);
        return videoTexture;
    }
}