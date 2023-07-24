package me.srrapero720.watermedia.api.images;

import me.srrapero720.watermedia.api.WaterMediaAPI;
import me.srrapero720.watermedia.api.external.GifDecoder;
import me.srrapero720.watermedia.api.external.ThreadUtil;
import me.srrapero720.watermedia.core.MediaCacheCore;
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

import static me.srrapero720.watermedia.WaterMedia.LOGGER;
import static me.srrapero720.watermedia.util.ResourceUtil.USER_AGENT;

public abstract class ImageFetcher extends Thread {
    private static final Marker IT = MarkerFactory.getMarker("FetchPicture");
    private static final Object LOCK = new Object();
    private static final DateFormat FORMAT = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");

    // STATUS
    public static final int MAX_FETCH = 6;
    public static int ACTIVE_FETCH = 0;

    private final URL url;
    public ImageFetcher(String url) {
        this.url = WaterMediaAPI.urlBuildAndPatch(url);
        this.setName("WaterMedia-Picture");
        this.setDaemon(true);
        this.start();
    }

    public static boolean canSeek() { synchronized(LOCK) { return ACTIVE_FETCH < MAX_FETCH; } }

    public abstract void onFailed(Exception e);
    public abstract void onSuccess(ImageRenderer imageRenderer);

    @Override
    public void run() {
        synchronized (LOCK) { ACTIVE_FETCH++; }

        try {
            byte[] data = load(url);
            String type = readType(data);

            try (ByteArrayInputStream in = new ByteArrayInputStream(data)) {
                if (type != null && type.equalsIgnoreCase("gif")) {
                    GifDecoder gif = new GifDecoder();
                    int status = gif.read(in);

                    if (status == GifDecoder.STATUS_OK) {
                        onSuccess(new ImageRenderer(gif));
                    } else {
                        LOGGER.error(IT, "Failed to read gif: {}", status);
                        throw new IOException("");
                    }
                } else {
                    try {
                        BufferedImage image = ImageIO.read(in);
                        if (image != null) {
                            onSuccess(new ImageRenderer(image));
                        }
                    } catch (IOException e1) {
                        LOGGER.error(IT, "Failed to parse BufferedImage from stream", e1);
                        throw e1;
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error(IT, "An exception occurred while loading Waterframes image", e);
            onFailed(e);
            MediaCacheCore.deleteEntry(url.toString());
        }

        synchronized (LOCK) {
            ACTIVE_FETCH--;
        }
    }

    public static byte[] load(URL url) throws IOException, VideoContentException {
        MediaCacheCore.Entry entry = MediaCacheCore.getEntry(url.toString());
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
                        MediaCacheCore.updateEntry(new MediaCacheCore.Entry(url.toString(), freshTag, lastTimestamp, expTimestamp));
                    }
                }
            }

            byte[] data = IOUtils.toByteArray(in);
            if (readType(data) == null) throw new VideoContentException();
            MediaCacheCore.saveFile(url.toString(), tag, lastTimestamp, expTimestamp, data);
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
}
