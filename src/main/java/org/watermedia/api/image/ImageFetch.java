package org.watermedia.api.image;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.watermedia.api.cache.CacheAPI;
import org.watermedia.api.image.decoders.GifDecoder;
import org.watermedia.api.network.NetworkAPI;
import org.watermedia.api.network.patchs.AbstractPatch;
import org.watermedia.core.tools.DataTool;
import org.watermedia.core.tools.ThreadTool;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URLConnection;
import java.nio.file.Files;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static org.watermedia.WaterMedia.*;
import static java.net.HttpURLConnection.*;
import static org.watermedia.api.image.ImageAPI.IT;

public class ImageFetch implements Runnable {
    private static final DateFormat FORMAT = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
    private static final ExecutorService EX = Executors.newScheduledThreadPool(ThreadTool.minThreads(), ThreadTool.factory("ImageFetch-Worker", Thread.NORM_PRIORITY + 1));

    public final URI uri;
    public BiConsumer<ImageRenderer, Boolean> successConsumer;
    public BiConsumer<Exception, Boolean> errConsumer;


    public ImageFetch(URI uri) {
        this.uri = uri;
    }

    public ImageFetch setSuccessCallback(BiConsumer<ImageRenderer, Boolean> consumer) {
        this.successConsumer = consumer;
        return this;
    }

    public ImageFetch setErrorCallback(BiConsumer<Exception, Boolean> consumer) {
        this.errConsumer = consumer;
        return this;
    }

    @Override
    public void run() {
        try {
            AbstractPatch.Result patch = NetworkAPI.patch(uri);
            if (patch == null) throw new IllegalArgumentException("Invalid URL");
            if (patch.assumeVideo) throw new VideoTypeException();
            final CacheAPI.Entry cache = CacheAPI.getEntry(uri);

            // READ FROM WHENEVER IT WAS LOCATED
            URLConnection conn = null;
            try {
                int code = 200; // AS EXPECTED
                conn = openConnection(patch.uri, cache);

                // GENERIC
                String type = conn.getContentType();
                if (type == null || !type.startsWith("image/")) throw new NoImageException();

                // HTTP ADDRESS
                if (conn instanceof HttpURLConnection) {
                    HttpURLConnection http = (HttpURLConnection) conn;
                    code = http.getResponseCode();
                    switch (code) {
                        case HTTP_BAD_REQUEST:
                        case HTTP_FORBIDDEN:
                        case HTTP_NOT_FOUND:
                            throw new NoImageException();
                        case HTTP_OK:
                        case HTTP_NOT_MODIFIED:
                            break;
                        default:
                            throw new IllegalStateException("HTTP Server responses an invalid status code: " + code);
                    }
                }

                // NOT MODIFIED SERVER
                if (cache != null && code == HTTP_NOT_MODIFIED) {
                    // JUST REFRESH ENTRY DATA, MAYBE EXPIRATION TIME IS EXTENDED
                    CacheAPI.updateEntry(new CacheAPI.Entry(uri, getEtagOr(conn, cache.getTag()), getLastModificationTime(conn), getExpirationTime(conn)));

                    // CONSUME
                    successConsumer.accept(readImages(cache), true);
                } else { // MODIFIED OR WHATEVER
                    // READ DATA FROM SOURCE
                    InputStream in = conn.getInputStream();
                    byte[] data = DataTool.readAllBytes(in);

                    // STORE CACHE
                    CacheAPI.saveFile(uri, getEtagOr(conn, cache != null ? cache.getTag() : ""), getLastModificationTime(conn), getExpirationTime(conn), data);

                    LOGGER.debug(IT, "Successfully downloaded image from '{}'", uri);

                    // CONSUME
                    successConsumer.accept(readImages(data), false);

                    // CLOSE
                    in.close();
                }
            } catch (Exception e) {
                // READ FROM CACHE AS LAST RESORT
                if (cache == null || !cache.getFile().exists()) {
                    throw e;
                }

                LOGGER.error(IT, "Failed to fetch image, delegating to cache files");

                successConsumer.accept(readImages(cache), true);
            } finally {
                if (conn instanceof HttpURLConnection) ((HttpURLConnection) conn).disconnect();
            }
        } catch (NoImageException | InternalDecoderException e) {
            LOGGER.error(IT, "Invalid image source from '{}'", uri, e);
            errConsumer.accept(e, false);
        } catch (VideoTypeException e) {
            LOGGER.debug(IT, "Detected a video type from '{}'", uri, e);
            errConsumer.accept(e, true);
        } catch (Exception e) {
            LOGGER.error(IT, "Unhandled exception occurred while loading image from '{}'", uri, e);
            errConsumer.accept(e, false);
        } catch (Throwable t) {
            LOGGER.error(IT, "Fatal exception occurred while loading image from '{}'", uri, t);
            errConsumer.accept(new Exception("Fatal exception running image loading", t), false);
        }
    }

    public void start() {
        EX.execute(this);
    }

    public ImageRenderer readImages(CacheAPI.Entry cache) throws Exception {
        try (InputStream in = Files.newInputStream(cache.getFile().toPath())) {
            byte[] data = DataTool.readAllBytes(in);
            return readImages(data);
        }
    }

    public ImageRenderer readImages(byte[] data) throws Exception {
        String type = "";

        try (ImageInputStream stream = ImageIO.createImageInputStream(new ByteArrayInputStream(data))) {
            Iterator<ImageReader> iterator = ImageIO.getImageReaders(stream);

            // IMAGE IO DECODING
            while (iterator.hasNext()) {
                ImageReader reader = iterator.next();

                reader.setInput(stream, false, false);
                int frames = reader.getNumImages(true);

                BufferedImage[] images = new BufferedImage[frames];
                long[] delay = new long[frames];
                long lastDelay = 10; // assume delay is bad placed
                int noDelayFrames = 0;

                // ITERATE ALL FRAMES
                try { // BUT MAY FAIL AND NEXT READER WILL WORK
                    for (int i = 0; i < frames; i++) {
                        IIOMetadata metadata = reader.getImageMetadata(i);
                        String format = metadata.getNativeMetadataFormatName();

                        // ADDRESS FOR GIF READER :) - GET FRAME DELAY
                        type = reader.getFormatName();
                        if (reader.getFormatName().equalsIgnoreCase("gif")) {
                            // FIXME: ImageIO decoding gifs breaks malloc buffers (how the fuck is that even possible)
                            if (true) throw new IllegalArgumentException("Gif format detected, forced-delegation to WM decoders");
                            Node root = metadata.getAsTree(format);
                            Node delayNode = fetchImageNode(root, "GraphicControlExtension");

                            if (delayNode != null) {
                                NamedNodeMap attributes = delayNode.getAttributes();
                                Node delayTimeNode = attributes.getNamedItem("delayTime");
                                int delayTime = Integer.parseInt(delayTimeNode.getNodeValue());

                                // STORE DELAY
                                lastDelay = delay[i] = delayTime * 10L;
                            } else {
                                delay[i] = lastDelay;
                                noDelayFrames++;
                            }
                        }

                        // STORE FRAME
                        images[i] = reader.read(i);
                    }

                    if (noDelayFrames > 0) LOGGER.debug(IT, "Gif decoder reports {} frames without delaay", noDelayFrames);
                } catch (Exception e) {
                    if (type.equalsIgnoreCase("gif"))
                        throw e;
                    LOGGER.error(IT, "Failed to decode image using reader {}({})", reader.getClass().getSimpleName(), reader.getFormatName());
                    LOGGER.debug(IT, "Error: ", e);
                    continue;
                }

                return ImageAPI.renderer(images, delay);
            }

            throw new IOException("ImageFetcher was unable to read the image");
        } catch (Exception e) {
            if (!type.equalsIgnoreCase("gif"))
                throw e;

            LOGGER.error(IT, "Failed to decode gif via ImageIO, delegating to WaterMedia decoders");
            LOGGER.debug(IT, "Error: ", e);

            // IF WE FAILED TO DECODE GIF, DELEGATE TO OUR GIF DECODER (IDC ABOUT THE GIF ERROR)
            GifDecoder gif = new GifDecoder();
            int status = gif.read(data);

            if (status == GifDecoder.STATUS_OK) {
                return ImageAPI.renderer(gif);
            } else {
                throw new InternalDecoderException("Failed to decode gif, status code: " + status);
            }
        }
    }

    private static String getEtagOr(URLConnection conn, String alt) {
        String ETag = conn.getHeaderField("ETag");
        if (ETag == null || ETag.isEmpty()) return alt;
        return ETag;
    }

    private static long getExpirationTime(URLConnection conn) {
        long time;

        // FIRST WAY
        String maxAge = conn.getHeaderField("max-age");
        if (maxAge == null || maxAge.isEmpty()) maxAge = "-1";

        long parsed = DataTool.parseLongOr(maxAge, -1);
        time = parsed == -1 ? -1 : System.currentTimeMillis() + (parsed * 100);
        if (time != -1)
            return time;

        // SECOND WAY
        String expires = conn.getHeaderField("Expires");
        if (expires != null && !expires.isEmpty()) {
            try {
                time = FORMAT.parse(expires).getTime();
            } catch (ParseException | NumberFormatException ignored) {}
        }

        return time;
    }

    private static long getLastModificationTime(URLConnection conn) {
        long time = -1;
        String date = conn.getHeaderField("Last-Modified");
        if (date != null && !date.isEmpty()) {
            try {
                time = FORMAT.parse(date).getTime();
            } catch (ParseException | NumberFormatException ignored) {}
        }

        return time;
    }

    private static Node fetchImageNode(Node root, String nodeName) {
        if (root.getNodeName().equalsIgnoreCase(nodeName))
            return root;

        Node child = root.getFirstChild();
        while (child != null) {
            Node result = fetchImageNode(child, nodeName);
            if (result != null) {
                return result;
            }
            child = child.getNextSibling();
        }
        return null;
    }

    private static URLConnection openConnection(URI uri, CacheAPI.Entry cache) throws IOException {
        URLConnection conn = uri.toURL().openConnection();
        conn.setDefaultUseCaches(false);
        conn.setRequestProperty("Accept", "image/*");
        if (cache != null && cache.getFile().exists()) {
            if (cache.getTag() != null) conn.setRequestProperty("If-None-Match", cache.getTag());
            else if (cache.getTime() != -1) conn.setRequestProperty("If-Modified-Since", FORMAT.format(new Date(cache.getTime())));
        }
        return conn;
    }

    private static class InternalDecoderException extends Exception {
        public InternalDecoderException(String msg) {
            super(msg);
        }
    }
    private static class VideoTypeException extends Exception {}
    private static class NoImageException extends Exception {}
}
