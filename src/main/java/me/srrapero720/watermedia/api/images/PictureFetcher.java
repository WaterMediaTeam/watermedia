package me.srrapero720.watermedia.api.images;

import me.srrapero720.watermedia.Util;
import me.srrapero720.watermedia.api.WaterMediaAPI;
import me.srrapero720.watermedia.api.external.GifDecoder;
import me.srrapero720.watermedia.api.external.ThreadUtil;
import org.apache.commons.io.IOUtils;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import javax.imageio.ImageIO;
import java.io.*;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static me.srrapero720.watermedia.WaterMedia.LOGGER;

public abstract class PictureFetcher extends Thread {
    private static final Marker IT = MarkerFactory.getMarker("FetchPicture");
    private static final Object LOCK = new Object();
    private static final String USER_AGENT = Util.getUserAgentBasedOnOS();
    private static final DateFormat FORMAT = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");

    // STATUS
    public static final Integer MAX_FETCH = 6;
    public static int ACTIVE_FETCH = 0;

    private final String url;

    /**
     * Creates a new thread instance to fetch pictures.
     * If fetcher founds any picture like gif or images then fires {@link PictureFetcher#onSuccess(RenderablePicture)}
     * otherwise If finds any multimedia content or handles http errors then fires {@link PictureFetcher#onFailed(Exception)}
     * when Exception instance is {@link VideoContentException} fetcher creates a {@link StrongRenderablePicture} referencing a
     * invalid dummy picture for videos (you can check if is a video using {@link RenderablePicture#isVideo()}
     * <br><br>
     * <strong>IMPORTANT:</strong> All {@link RenderablePicture} returned has by default 1 usage, so you don't need to reuse it.
     * @param url URL to fetch if was a picture or any other thing
     */
    public PictureFetcher(String url) {
        this.url = WaterMediaAPI.urlPatch(url);
        this.setName("WaterMedia-Picture");
        this.setDaemon(true);
        this.start();
    }

    public static boolean canSeek() { synchronized(LOCK) { return ACTIVE_FETCH < MAX_FETCH; } }

    public abstract void onFailed(Exception e);
    public abstract void onSuccess(RenderablePicture renderablePicture);

    @Override
    public void run() {
        synchronized (LOCK) { ACTIVE_FETCH++; }

        try {
            var data = load(url);
            var type = readType(data);

            try (var in = new ByteArrayInputStream(data)) {
                if (type != null && type.equalsIgnoreCase("gif")) {
                    var pic = new GifDecoder();
                    var status = pic.read(in);

                    if (status == GifDecoder.STATUS_OK) {
                        fireOnSuccess(this, url, new RenderablePicture(pic).use());
                    } else {
                        LOGGER.error(IT, "Failed to read gif: {}", status);
                        throw new IOException("");
                    }
                } else {
                    try {
                        var pic = ImageIO.read(in);
                        if (pic != null) {
                            fireOnSuccess(this, url, new RenderablePicture(pic).use());
                        }
                    } catch (IOException e1) {
                        LOGGER.error(IT, "Failed to parse BufferedImage from stream", e1);
                        throw e1;
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error(IT, "An exception occurred while loading image", e);
            LocalStorage.deleteEntry(url);
            if (e instanceof VideoContentException) PictureManager.addEntry(url, new StrongRenderablePicture());
            onFailed(e);
        }

        synchronized (LOCK) {
            ACTIVE_FETCH--;
        }
    }

    private static synchronized void fireOnSuccess(PictureFetcher fetcher, String url, RenderablePicture picture) {
        RenderablePicture cached = PictureManager.findAndUse(url);
        if (cached == null) {
            PictureManager.addEntry(url, picture);
            fetcher.onSuccess(picture);
        } else {
            picture.release();
        }
    }

    public static byte[] load(String url) throws IOException, VideoContentException {
        var entry = LocalStorage.getEntry(url);
        var requestTime = System.currentTimeMillis();
        var request = new URL(url).openConnection();

        var code = -1;

        request.addRequestProperty("User-Agent", USER_AGENT);
        if (request instanceof HttpURLConnection conn) {
            if (entry != null && entry.getFile().exists()) {
                if (entry.getTag() != null) conn.setRequestProperty("If-None-Match", entry.getTag());
                else if (entry.getTime() != -1) conn.setRequestProperty("If-Modified-Since", FORMAT.format(new Date(entry.getTime())));
            }
            code = conn.getResponseCode();
        }

        try (InputStream in = request.getInputStream()) {
            if (code == 400 || code == 401 || code == 403) throw new VideoContentException();
            if (code != HttpURLConnection.HTTP_NOT_MODIFIED) {
                var type = request.getContentType();
                if (type == null) throw new ConnectException();
                if (!type.startsWith("image")) throw new VideoContentException();
            }

            var tag = request.getHeaderField("ETag");
            long lastTimestamp, expTimestamp = -1;
            var maxAge = request.getHeaderField("max-age");

            // EXPIRATION GETTER FIRST
            if (maxAge != null && !maxAge.isEmpty())
                expTimestamp = ThreadUtil.tryAndReturn(defaultVar -> requestTime + Long.parseLong(maxAge) * 1000, expTimestamp);

            // EXPIRATION GETTER SECOND WAY
            var expires = request.getHeaderField("Expires");
            if (expires != null && !expires.isEmpty())
                expTimestamp = ThreadUtil.tryAndReturn(defaultVar -> FORMAT.parse(expires).getTime(), expTimestamp);

            // LAST TIMESTAMP
            var lastMod = request.getHeaderField("Last-Modified");
            if (lastMod != null && !lastMod.isEmpty()) {
                lastTimestamp = ThreadUtil.tryAndReturn(defaultVar -> FORMAT.parse(lastMod).getTime(), requestTime);
            } else lastTimestamp = requestTime;

            if (entry != null) {
                var freshTag = entry.getTag();
                if (tag != null && !tag.isEmpty()) freshTag = tag;

                if (code == HttpURLConnection.HTTP_NOT_MODIFIED) {
                    File file = entry.getFile();

                    if (file.exists()) try (var fileStream = new FileInputStream(file)) {
                        return IOUtils.toByteArray(fileStream);
                    } finally {
                        LocalStorage.updateEntry(new LocalStorage.Entry(url, freshTag, lastTimestamp, expTimestamp));
                    }
                }
            }

            byte[] data = IOUtils.toByteArray(in);
            if (readType(data) == null) throw new VideoContentException();
            LocalStorage.saveFile(url, tag, lastTimestamp, expTimestamp, data);
            return data;
        } finally {
            if (request instanceof HttpURLConnection http) http.disconnect();
        }
    }

    private static String readType(byte[] input) throws IOException {
        try (var in = new ByteArrayInputStream(input)) {
            return readType(in);
        }
    }

    private static String readType(InputStream input) throws IOException {
        var stream = ImageIO.createImageInputStream(input);
        var iterator = ImageIO.getImageReaders(stream);

        if (!iterator.hasNext()) return null;

        var reader = iterator.next();
        if (reader.getFormatName().equalsIgnoreCase("gif")) return "gif";

        var param = reader.getDefaultReadParam();
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
