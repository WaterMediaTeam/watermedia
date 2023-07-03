package me.srrapero720.watermedia.api;

import me.lib720.caprica.vlcj.factory.MediaPlayerFactory;
import me.lib720.caprica.vlcj.factory.discovery.NativeDiscovery;
import me.srrapero720.watermedia.Util;
import me.srrapero720.watermedia.api.images.RenderablePicture;
import me.srrapero720.watermedia.api.url.URLPatch;
import me.srrapero720.watermedia.api.url.patch.*;
import me.srrapero720.watermedia.api.video.VideoLANPlayer;
import me.srrapero720.watermedia.api.external.ThreadUtil;
import me.srrapero720.watermedia.core.videolan.VideoLAN;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import static me.srrapero720.watermedia.WaterMedia.LOGGER;

import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class WaterMediaAPI {
    private static final Marker IT = MarkerFactory.getMarker("WaterMediaAPI");
    public static final RenderablePicture LOADING_GIF = new RenderablePicture(Util.getGifFromResources("/images/loading.gif"));
    public static final RenderablePicture VLC_FAILED = new RenderablePicture(Util.getImageFromResources("/images/vlc_win_failed.png"));
    public static final RenderablePicture VLC_FAILED_INSTALL = new RenderablePicture(Util.getImageFromResources("/images/vlc_otheros_failed.png"));

    private static final List<URLPatch> URL_PATCHERS = new ArrayList<>(List.of(
            new YoutubePatch(),
            new TwitchPatch(),
            new KickPatch(),
            new DrivePatch(),
            new TwitterPatch(),
            new OnedrivePatch(),
            new DropboxPatch())
    );

    /**
     * 1 seconds in Minecraft equals 20 ticks
     * 20x50 equals 1000ms (1 sec)
     *
     * @param ticks Minecraft Ticks
     * @return ticks converted to MS
     */
    public static long gameTicksToMs(int ticks) { return ticks * 50L; }

    /**
     * 1000ms (1 sec) equals 20 ms in Minecraft
     * 1000/50 equals 20 Ticks (1 sec)
     *
     * @param ms Time in milliseconds
     * @return Milliseconds converted to Ticks
     */
    public static int msToGameTicks(long ms) { return (int) (ms / 50L); }

    /**
     * Check if any String is a valid URL
     * @param url the URL in a string
     * @return if is valid.
     */
    public static boolean isURLValid(String url) { return ThreadUtil.tryAndReturn(defaultVar -> { new URL(url); return true; }, false); }


    /**
     * Creates your own URLPatch and register it to WaterMediaAPI
     * @param patch All patches you want to Use
     */
    public static void registerURLPatch(@NotNull URLPatch ...patch) {
        for (final URLPatch p: patch) {
            LOGGER.warn(IT, "Registered new URLPatch: {}", p.getClass().getSimpleName());
            URL_PATCHERS.add(p);
        }
    }

    /**
     * This method is used by default on {@link VideoLANPlayer#start(CharSequence, String[])}
     * Is not recommended external usages
     * @param url Media URL to patch
     * @return Media URL patched to be fully compatible with VLC (static resource)
     */
    public static String urlPatch(String url) {
        return ThreadUtil.tryAndReturn(defaultVar -> {
            for (var compat: URL_PATCHERS) if (compat.isValid(new URL(url))) return compat.patch(new URL(url));
            return defaultVar;
        }, e -> LOGGER.error(IT, "Exception occurred trying to run patchNonStaticUrl", e), url);
    }

    /**
     * Use your own VLCArgs at your own risk
     * By default this method makes a ReleaseHook to release everything after close Minecraft
     * Suggestion: Use the same VLC arguments for logging but with other filename
     * Example: <pre> "--logfile", "logs/vlc/mymod-latest.log",</pre>
     * @param vlcArgs arguments to make another VLC instance
     * @return a PlayerFactory to create custom VLC players. {@link VideoLANPlayer} can accept factory for new instances
     */
    public static MediaPlayerFactory createVLCFactory(String[] vlcArgs) {
        var discovery = new NativeDiscovery();
        if (discovery.discover()) {
            var factory = new MediaPlayerFactory(discovery, vlcArgs);
            LOGGER.info(IT, "New instance of VLC loaded from '{}' with the next args:\n{}", discovery.discoveredPath(), Arrays.toString(vlcArgs));
            Runtime.getRuntime().addShutdownHook(new Thread(factory::release));
            return factory;
        }

        LOGGER.error(IT, "VLC was not found on your system.");
        return null;
    }

    /**
     * Check if VLC is loaded and ready to be used on {@link VideoLANPlayer} or to make
     * a new {@link MediaPlayerFactory} instance
     * @return if is reddy or not
     */
    public static boolean isVLCReady() { return VideoLAN.defaultFactory() != null; }

    /**
     * Created by CreativeMD
     * @param image picture to process
     * @param width picture width
     * @param height picture height
     * @return textureID
     */
    public static int genGLTexture(BufferedImage image, int width, int height) {
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
}
