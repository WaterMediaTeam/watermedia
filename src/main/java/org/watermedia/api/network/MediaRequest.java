package org.watermedia.api.network;

import me.srrapero720.watermedia.api.MediaContext;
import me.srrapero720.watermedia.api.player.IMediaPlayer;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.watermedia.api.NetworkAPI;
import org.watermedia.api.media.MediaPlayer;
import org.watermedia.tools.ThreadTool;

import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.net.HttpURLConnection.*;
import static org.watermedia.WaterMedia.LOGGER;

public class MediaRequest {
    private static final Marker IT = MarkerManager.getMarker(NetworkAPI.class.getSimpleName());
    private static final ExecutorService EXECUTOR = Executors.newScheduledThreadPool(ThreadTool.minThreads(), ThreadTool.factory("MediaRequest-Worker", Thread.NORM_PRIORITY + 1));
    private static final DateFormat FORMAT = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
    private static final int MAX_REDIRECTS = 5;

    private final MRL mrl;
    private final MediaContext context;
    private final MediaPlayer.Quality quality;

    /**
     * Creates a new MediaRequest instance
     * It first detects if MRL can be (re)patchable, then performs the request.
     * By default, the {@link IMediaPlayer MediaPlayer} selects the
     * first available source in the most-closer quality to the desired one
     * @param mrl the MRL instance
     * @param defSelectedQuality default selected quality, null means highest quality
     * @see IMediaPlayer IMediaPlayer
     * @see MediaPlayer.Quality Quality
     *
     */
    public MediaRequest(MRL mrl, MediaContext context, MediaPlayer.Quality defSelectedQuality) {
        this.mrl = mrl;
        this.context = context;
        this.quality = defSelectedQuality;
    }

    public void start() {
        EXECUTOR.execute(this::run);
    }

    private void run() {
        if (!mrl.patched()) {
            NetworkAPI.patchMRL(mrl);
        }
        try {
            this.start$run();
        } catch (IOException e) {

        }
    }

    private void start$run() throws IOException {
        // CACHE CHECK

        // REQUEST
        List<MRL.Source> source = ;
        URLConnection conn = source.get(this.context, this.quality).toURL().openConnection();

        List<MRL.Source> sources = mrl.getSources();
        boolean softfail = sources.size() > 1;
        for (MRL.Source src: sources) {
            try {
                conn = src.toURL().openConnection();
                break;
            } catch (IOException e) {
                LOGGER.warn(IT, "Failed to connect to '{}'", src);
            }
        }

        // REDIRECT SUPPORT
        if (conn instanceof HttpURLConnection http) {
            int code = http.getResponseCode();

            int redirects = 0;
            while (code == HttpURLConnection.HTTP_MOVED_TEMP || code == HttpURLConnection.HTTP_MOVED_PERM) {
                String location = http.getHeaderField("Location");

                if (redirects >= MAX_REDIRECTS)
                    throw new ConnectException("Too many redirects!");
                if (location == null)
                    throw new IOException("Redirection response but no Location header");
                else
                    LOGGER.warn(IT, "Redirecting to '{}' ({})", location, redirects);

                // OVERRIDES OLD CONNECTIONS AND STATUS CODE
                conn = new URL(location).openConnection();
                if (conn instanceof HttpURLConnection newHttp) {
                    http = newHttp;
                    code = http.getResponseCode();
                    continue;
                }
                redirects++;
            }

            // COMMON ERROR CODES
            switch (code) {
                case HTTP_BAD_REQUEST -> throw new ConnectException("400 Bad request");
                case HTTP_FORBIDDEN,
                     HTTP_UNAUTHORIZED -> throw new ConnectException("403 Forbidden");
                case HTTP_NOT_FOUND -> throw new ConnectException("404 Not found");
                case HTTP_LENGTH_REQUIRED -> throw new ConnectException("411 Length required");
                case HTTP_PRECON_FAILED -> throw new ConnectException("412 Precondition failed");
                case HTTP_INTERNAL_ERROR -> throw new ConnectException("500 Internal Server error");
                case HTTP_UNAVAILABLE -> throw new ConnectException("503 Service unavailable");
                default -> {
                    if (code != HttpURLConnection.HTTP_OK) throw new ConnectException("Unexpected response code: " + code);
                }
            }
        }

        try (InputStream in = conn.getInputStream()) {
            // CACHE STORE


            // CREATE MEDIASOURCE?
        }

    }

    private record CacheEntry(URI uri, String tag, String  ext, Date requestedDate, Date expirationDate) {
        public boolean expired() {
            return expirationDate != null && System.currentTimeMillis() > expirationDate.getTime();
        }
    }
}
