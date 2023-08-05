package me.srrapero720.watermedia.api.images;

import me.lib720.madgag.gif.fmsware.GifDecoder;
import me.srrapero720.watermedia.core.MediaStorage;
import me.srrapero720.watermedia.util.ThreadUtil;
import org.apache.commons.io.IOUtils;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static me.srrapero720.watermedia.WaterMedia.LOGGER;
import static me.srrapero720.watermedia.util.AssetsUtil.USER_AGENT;

public class ImageFetch {
    private static final Marker IT = MarkerFactory.getMarker("FetchPicture");
    private static final DateFormat FORMAT = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
    private static final AtomicInteger WK_TH = new AtomicInteger(0);
    private static final ExecutorService EX = Executors.newScheduledThreadPool(ThreadUtil.getMinThreadCount(), r -> {
        Thread t = new Thread(r);
        t.setDaemon(true);
        t.setPriority(Thread.MIN_PRIORITY);
        t.setName("WM-ImageFetch-Worker-" + WK_TH.incrementAndGet());
        return t;
    });

    private final URL url;
    private TaskSuccessful successful;
    private TaskFailed failed;

    public ImageFetch(URL url) { this.url = url; }
    public ImageFetch setOnSuccessCallback(TaskSuccessful task) { successful = task; return this; }
    public ImageFetch setOnFailedCallback(TaskFailed task) { failed = task; return this; }

    public void start() { EX.execute(this::run); }
    private void run() {
        try {
            byte[] data = load(url);
            String type = readType(data);

            try (ByteArrayInputStream in = new ByteArrayInputStream(data)) {
                if (type != null && type.equalsIgnoreCase("gif")) {
                    GifDecoder gif = new GifDecoder();
                    int status = gif.read(in);

                    if (status == GifDecoder.STATUS_OK) {
                        if (successful != null) successful.run(new ImageRenderer(gif));
                    } else {
                        LOGGER.error(IT, "Failed to read gif: {}", status);
                        throw new GifDecodingException();
                    }
                } else {
                    try {
                        BufferedImage image = ImageIO.read(in);
                        if (image != null) {
                            if (successful != null) successful.run(new ImageRenderer(image));
                        }
                    } catch (IOException e1) {
                        LOGGER.error(IT, "Failed to parse BufferedImage from stream", e1);
                        throw e1;
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error(IT, "An exception occurred while loading Waterframes image", e);
            if (failed != null) failed.run(e);
            MediaStorage.deleteEntry(url.toString());
        }
    }

    private static byte[] load(URL url) throws IOException, VideoContentException {
        MediaStorage.Entry entry = MediaStorage.getEntry(url.toString());
        long requestTime = System.currentTimeMillis();
        URLConnection request = url.openConnection();

        int code = -1;

        request.addRequestProperty("User-Agent", USER_AGENT);
        if (request instanceof HttpURLConnection) {
            HttpURLConnection conn = (HttpURLConnection) request;
            if (entry != null && entry.getFile().exists()) {
                if (entry.getTag() != null) conn.setRequestProperty("If-None-Match", entry.getTag());
                else if (entry.getTime() != -1) conn.setRequestProperty("If-Modified-Since", FORMAT.format(new Date(entry.getTime())));
            }
            code = conn.getResponseCode();
        }

        try (InputStream in = request.getInputStream()) {
            if (code == 400 || code == 403) throw new VideoContentException();
            if (code != HttpURLConnection.HTTP_NOT_MODIFIED) {
                String type = request.getContentType();
                if (type == null) throw new ConnectException();
                if (!type.startsWith("image")) throw new VideoContentException();
            }

            String tag = request.getHeaderField("ETag");
            long lastTimestamp, expTimestamp = -1;
            String maxAge = request.getHeaderField("max-age");

            // EXPIRATION GETTER FIRST
            if (maxAge != null && !maxAge.isEmpty())
                expTimestamp = ThreadUtil.tryAndReturn(defaultVar -> requestTime + Long.parseLong(maxAge) * 1000, expTimestamp);

            // EXPIRATION GETTER SECOND WAY
            String expires = request.getHeaderField("Expires");
            if (expires != null && !expires.isEmpty())
                expTimestamp = ThreadUtil.tryAndReturn(defaultVar -> FORMAT.parse(expires).getTime(), expTimestamp);

            // LAST TIMESTAMP
            String lastMod = request.getHeaderField("Last-Modified");
            if (lastMod != null && !lastMod.isEmpty()) {
                lastTimestamp = ThreadUtil.tryAndReturn(defaultVar -> FORMAT.parse(lastMod).getTime(), requestTime);
            } else lastTimestamp = requestTime;

            if (entry != null) {
                String freshTag = entry.getTag();
                if (tag != null && !tag.isEmpty()) freshTag = tag;

                if (code == HttpURLConnection.HTTP_NOT_MODIFIED) {
                    File file = entry.getFile();

                    if (file.exists()) try (FileInputStream fileStream = new FileInputStream(file)) {
                        return IOUtils.toByteArray(fileStream);
                    } finally {
                        MediaStorage.updateEntry(new MediaStorage.Entry(url.toString(), freshTag, lastTimestamp, expTimestamp));
                    }
                }
            }

            byte[] data = IOUtils.toByteArray(in);
            if (readType(data) == null) throw new VideoContentException();
            MediaStorage.saveFile(url.toString(), tag, lastTimestamp, expTimestamp, data);
            return data;
        } finally {
            if (request instanceof HttpURLConnection) ((HttpURLConnection) request).disconnect();
        }
    }

    private static String readType(byte[] input) throws IOException {
        try (InputStream in = new ByteArrayInputStream(input)) {
            return readType(in);
        }
    }

    private static String readType(InputStream input) throws IOException {
        ImageInputStream stream = ImageIO.createImageInputStream(input);
        Iterator<ImageReader> iterator = ImageIO.getImageReaders(stream);

        if (!iterator.hasNext()) return null;

        ImageReader reader = iterator.next();
        if (reader.getFormatName().equalsIgnoreCase("gif")) return "gif";

        ImageReadParam param = reader.getDefaultReadParam();
        reader.setInput(stream, true, true);

        try {
            reader.read(0, param);
        } catch (IOException e) {
            LOGGER.error(IT, "Failed to parse input format", e);
        } finally {
            reader.dispose();
            IOUtils.closeQuietly(stream);
        }
        input.reset();
        return reader.getFormatName();
    }

    public static final class VideoContentException extends Exception {}
    public static final class GifDecodingException extends Exception {}
    public interface TaskSuccessful { void run(ImageRenderer renderer); }
    public interface TaskFailed { void run(Exception e); }
}
