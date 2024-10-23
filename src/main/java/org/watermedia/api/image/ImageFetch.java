package org.watermedia.api.image;

import org.watermedia.api.cache.CacheAPI;
import org.watermedia.api.image.decoders.GifDecoder;
import org.watermedia.api.network.NetworkAPI;
import org.watermedia.api.network.patchs.AbstractPatch;
import org.watermedia.core.tools.DataTool;
import org.watermedia.core.tools.NetTool;
import org.watermedia.core.tools.ThreadTool;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import sun.net.www.protocol.file.FileURLConnection;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.watermedia.WaterMedia.*;
import static java.net.HttpURLConnection.*;

/**
 * Tool to fetch new images from internet
 * stores all loaded pictures in our cache to skip downloading image 2 times
 */
public class ImageFetch {
    private static final Marker IT = MarkerManager.getMarker("ImageAPI");
    private static final DateFormat FORMAT = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
    private static final ExecutorService EX = Executors.newScheduledThreadPool(ThreadTool.minThreads(), ThreadTool.factory("ImageFetch-Worker", Thread.NORM_PRIORITY + 1));

    private final String url;
    private TaskSuccessful successful;
    private TaskFailed failed;

    /**
     * Creates a new Fetch instance.
     * The Task is ASYNC by default.
     * You should concern about concurrency on callbacks
     * @param url string url to fetch
     */
    public ImageFetch(String url) { this.url = url; }

    /**
     * Adds a new success callback
     * @param task callback
     * @return current instance
     */
    public ImageFetch setOnSuccessCallback(TaskSuccessful task) { successful = task; return this; }

    /**
     * Adds a new failed callback
     * @param task callback
     * @return current instance
     */
    public ImageFetch setOnFailedCallback(TaskFailed task) { failed = task; return this; }

    /**
     * Start image fetch
     * result is fired on callbacks
     */
    public void start() { EX.execute(this::run); }
    private void run() {
        try {
            AbstractPatch.Result result = NetworkAPI.patch(url);
            if (result == null) throw new IllegalArgumentException("Invalid URL");
            if (result.assumeVideo) throw new NoPictureException();

            byte[] data = load(url, result.url);
            String type = readType(data);

            try (ByteArrayInputStream in = new ByteArrayInputStream(data)) {
                if (type != null && type.equalsIgnoreCase("gif")) {
                    GifDecoder gif = new GifDecoder();
                    int status = gif.read(in);

                    if (status == GifDecoder.STATUS_OK) {
                        if (successful != null) successful.run(ImageAPI.renderer(gif));
                    } else {
                        LOGGER.error(IT, "Failed to read gif: {}", status);
                        throw new GifDecodingException();
                    }
                } else {
                    try {
                        BufferedImage image = ImageIO.read(in);
                        if (image != null) {
                            if (successful != null) successful.run(ImageAPI.renderer(image));
                        }
                    } catch (IOException e1) {
                        LOGGER.error(IT, "Failed to parse BufferedImage from stream", e1);
                        throw e1;
                    }
                }
            }
        } catch (Exception e) {
            if (!(e instanceof NoPictureException)) {
                LOGGER.error(IT, "An exception occurred while loading image", e);
            }
            if (failed != null) failed.run(e);
            CacheAPI.deleteEntry(url);
        }
    }

    private static byte[] load(String originalUrl, URI uri) throws IOException, NoPictureException {
        CacheAPI.Entry entry = CacheAPI.getEntry(originalUrl);
        long requestTime = System.currentTimeMillis();
        URLConnection conn = NetTool.connectToAny(uri, "GET");
        conn.setDefaultUseCaches(false);
        conn.setRequestProperty("Accept", "image/*");
        if (entry != null && entry.getFile().exists()) {
            if (entry.getTag() != null) conn.setRequestProperty("If-None-Match", entry.getTag());
            else if (entry.getTime() != -1) conn.setRequestProperty("If-Modified-Since", FORMAT.format(new Date(entry.getTime())));
        }

        try (InputStream in = conn.getInputStream()) {
            int code = (conn instanceof HttpURLConnection) ? ((HttpURLConnection) conn).getResponseCode() : 200;
            if (code == HTTP_BAD_REQUEST || code == HTTP_FORBIDDEN) throw new NoPictureException();
            if (code != HTTP_NOT_MODIFIED) {
                String type = conn.getContentType();
                if (type == null) throw new ConnectException();
                if (!type.startsWith("image/")) throw new NoPictureException();
            }

            String tag = conn.getHeaderField("ETag");
            long lastTimestamp, expTimestamp = -1;
            String maxAge = conn.getHeaderField("max-age");

            // EXPIRATION GETTER FIRST
            if (maxAge != null && !maxAge.isEmpty()) {
                long parsed = DataTool.parseLongOr(maxAge, -1);
                if (parsed != -1)
                    expTimestamp = requestTime + Long.parseLong(maxAge) * 100;
            }

            // EXPIRATION GETTER SECOND WAY
            String expires = conn.getHeaderField("Expires");
            if (expires != null && !expires.isEmpty()) {
                try {
                    expTimestamp = FORMAT.parse(expires).getTime();
                } catch (ParseException | NumberFormatException ignored) {}
            }
            // LAST TIMESTAMP
            String lastMod = conn.getHeaderField("Last-Modified");
            if (lastMod != null && !lastMod.isEmpty()) {
                try {
                    lastTimestamp = FORMAT.parse(lastMod).getTime();
                } catch (ParseException | NumberFormatException e) {
                    lastTimestamp = requestTime;
                }
            } else {
                lastTimestamp = requestTime;
            }

            if (entry != null) {
                String freshTag = entry.getTag();
                if (tag != null && !tag.isEmpty()) freshTag = tag;

                if (code == HttpURLConnection.HTTP_NOT_MODIFIED) {
                    File file = entry.getFile();

                    if (file.exists()) try (FileInputStream fileStream = new FileInputStream(file)) {
                        return DataTool.readAllBytes(fileStream);
                    } finally {
                        CacheAPI.updateEntry(new CacheAPI.Entry(originalUrl, freshTag, lastTimestamp, expTimestamp));
                    }
                }
            }


            byte[] data = DataTool.readAllBytes(in);
            CacheAPI.saveFile(originalUrl, tag, lastTimestamp, expTimestamp, data);
            return data;
        } finally {
            if (conn instanceof HttpURLConnection) {
                ((HttpURLConnection) conn).disconnect();
            }
            if (conn instanceof FileURLConnection) {
                ((FileURLConnection) conn).close();
            }
            if (conn instanceof sun.net.www.URLConnection) {
                ((sun.net.www.URLConnection) conn).close();
            }
        }
    }

    private static String readType(byte[] input) throws IOException {
        try (InputStream in = new ByteArrayInputStream(input)) {
            return readType(in);
        }
    }

    private static String readType(InputStream input) throws IOException {
        ImageReader reader = null;
        try(ImageInputStream stream = ImageIO.createImageInputStream(input)) {
            Iterator<ImageReader> iterator = ImageIO.getImageReaders(stream);

            if (!iterator.hasNext()) return null;

            reader = iterator.next();
            if (reader.getFormatName().equalsIgnoreCase("gif")) return "gif";

            ImageReadParam param = reader.getDefaultReadParam();
            reader.setInput(stream, true, true);
            reader.read(0, param);
        } catch (IOException e) {
            LOGGER.error(IT, "Failed to parse input format", e);
        } finally {
            if (reader != null) reader.dispose();
        }
        input.reset();
        if (reader == null) return null;
        return reader.getFormatName();
    }

    public static final class NoPictureException extends Exception {}
    public static final class GifDecodingException extends Exception {}
    public interface TaskSuccessful { void run(ImageRenderer renderer); }
    public interface TaskFailed { void run(Exception e); }
}