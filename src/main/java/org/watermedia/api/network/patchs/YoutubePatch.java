package org.watermedia.api.network.patchs;

import com.github.kiulian.downloader.YoutubeDownloader;
import com.github.kiulian.downloader.downloader.client.Client;
import com.github.kiulian.downloader.downloader.client.DefaultClients;
import com.github.kiulian.downloader.downloader.request.RequestVideoInfo;
import com.github.kiulian.downloader.downloader.response.Response;
import com.github.kiulian.downloader.model.videos.VideoDetails;
import com.github.kiulian.downloader.model.videos.VideoInfo;
import com.github.kiulian.downloader.model.videos.formats.AudioFormat;
import com.github.kiulian.downloader.model.videos.formats.VideoFormat;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.watermedia.WaterMedia;
import org.watermedia.api.network.patchs.twitch.StreamQuality;
import org.watermedia.core.tools.NetTool;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.watermedia.WaterMedia.LOGGER;

public class YoutubePatch extends AbstractPatch {
    private static final Marker IT = MarkerManager.getMarker("YoutubePatch");
    private static final YoutubeDownloader DOWNLOADER = new YoutubeDownloader();
    private static final Pattern PATTERN = Pattern.compile("(?:youtu\\.be/|youtube\\.com/(?:embed/|v/|shorts/|feeds/api/videos/|watch\\?v=|watch\\?.+&v=))([^/?&#]+)");
    private static Client WORKING_CLIENT;
    private static boolean WORKING_TESTED;
    private static final ReentrantLock LOCK = new ReentrantLock();

    @Override
    public String platform() {
        return "Youtube";
    }

    @Override
    public boolean isValid(URI uri) {
        return uri.getHost() != null && PATTERN.matcher(uri.toString()).find();
    }

    @Override
    public Result patch(URI uri, Quality preferQuality) throws FixingURLException {
        super.patch(uri, preferQuality);

        if (WORKING_TESTED && WORKING_CLIENT == null) {
            throw new FixingURLException(uri, new UnsupportedOperationException("Youtube cannot be accessed at this moment"));
        }

        Matcher matcher = PATTERN.matcher(uri.toString());
        if (matcher.find()) {
            try {
                int client = 0;
                VideoInfo videoInfo = null;
                Response<VideoInfo> response = null;
                String videoId = matcher.group(1);
                if (!WORKING_TESTED) {
                    if (LOCK.isLocked()) {
                        LOGGER.warn(IT, "Waiting patch for {}, another thread is searching for a working client", uri);
                    }
                    LOCK.lock();
                }

                do {
                    Client c = WORKING_TESTED ? WORKING_CLIENT : DefaultClients.VALUES[client];

                    RequestVideoInfo request = new RequestVideoInfo(videoId).client(c);
                    response = DOWNLOADER.getVideoInfo(request);
                    videoInfo = response.data();

                    if (!WORKING_TESTED) {
                        LOGGER.debug(IT, "Testing '{}'", c.getName());
                    }

                    if (videoInfo != null) {
                        HttpURLConnection conn = NetTool.connectToHTTP(URI.create(response.data().bestVideoFormat().url()), "GET");
                        if (conn.getResponseCode() != 403) {
                            conn.disconnect();
                        } else {
                            videoInfo = null;
                        }
                    }
                } while (!WORKING_TESTED && videoInfo == null && ++client < DefaultClients.VALUES.length);

                if (!WORKING_TESTED) LOCK.unlock();

                if (videoInfo == null) {
                    WORKING_TESTED = true;
                    throw new RuntimeException("Cannot fetch video info", response.error());
                } else if (!WORKING_TESTED) {
                    WORKING_TESTED = true;
                    LOGGER.info(IT, "Found working Youtube client '{}'", DefaultClients.VALUES[client].getName());
                }

                if (WORKING_CLIENT == null) {
                    WORKING_CLIENT = DefaultClients.VALUES[0];
                }

                VideoDetails videoDetails = videoInfo.details();

                if (videoDetails.isLive()) {
                    // LIVE STREAM
                    String ytLivePlaylist = fetchLivePlaylist(videoDetails.liveUrl());
                    if (ytLivePlaylist != null) return new Result(new URI(StreamQuality.parse(ytLivePlaylist).get(0).getUrl()), true, true);
                } else {
                    // BEST WITH ALL
                    VideoFormat bestAll = videoInfo.bestVideoWithAudioFormat();
                    if (bestAll != null) return new Result(new URI(bestAll.url()), true, false);

                    // AUDIO ONLY
                    VideoFormat bestVideo = videoInfo.bestVideoFormat();
                    if (bestVideo != null) {
                        return new Result(new URI(bestVideo.url()), true, false);
                    }

                    // VIDEO ONLY
                    AudioFormat bestAudio = videoInfo.bestAudioFormat();
                    if (bestAudio != null) return new Result(new URI(bestAudio.url()), true, false);
                }

                // VLC shouldn't use LUAC
                return null;
            } catch (Exception e) {
                throw new FixingURLException(uri.toString(), e);
            }
        }

        return null;
    }

    private String fetchLivePlaylist(String url) throws IOException {
        URL apiUrl = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) apiUrl.openConnection();
        conn.setRequestMethod("GET");

        int responseCode = conn.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) return null;

        InputStream inputStream = conn.getInputStream();
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) != -1) {
            result.write(buffer, 0, length);
        }
        return result.toString("UTF-8");
    }
}