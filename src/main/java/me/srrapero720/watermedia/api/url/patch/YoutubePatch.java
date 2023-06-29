package me.srrapero720.watermedia.api.url.patch;

import com.github.kiulian.downloader.YoutubeDownloader;
import com.github.kiulian.downloader.downloader.request.RequestVideoInfo;
import me.srrapero720.watermedia.api.url.patch.util.StreamQuality;
import me.srrapero720.watermedia.api.url.URLPatch;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class YoutubePatch extends URLPatch {
    private static final Pattern PATTERN = Pattern.compile("(?:youtu\\.be/|youtube\\.com/(?:embed/|v/|shorts/|feeds/api/videos/|watch\\?v=|watch\\?.+&v=))([\\w-]{11})(?![\\w-])");

    @Override
    public boolean isValid(@NotNull URL url) {
        return (url.getHost().endsWith("youtube.com") || url.getHost().endsWith("youtu.be"));
    }

    @Override
    public String patch(@NotNull URL url) throws PatchingUrlException {
        super.patch(url);

        Matcher matcher = PATTERN.matcher(url.toString());
        if (matcher.find()) {
            try {
                String videoId = matcher.group(1);
                var videoInfo = new YoutubeDownloader().getVideoInfo(new RequestVideoInfo(videoId)).data();
                var videoDetails = videoInfo.details();

                if (videoDetails.isLive()) {
                    // LIVE STREAM
                    String ytLivePlaylist = fetchLivePlaylist(videoDetails.liveUrl());
                    if (ytLivePlaylist != null) return StreamQuality.parse(ytLivePlaylist).get(0).getUrl();
                } else {
                    // BEST WITH AUDIO
                    var bestWithAudio = videoInfo.bestVideoWithAudioFormat();
                    if (bestWithAudio != null) return bestWithAudio.url();

                    // WITHOUT AUDIO
                    var bestWithoutAudio = videoInfo.bestVideoFormat();
                    if (bestWithoutAudio != null) return bestWithoutAudio.url();

                    // WITHOUT VIDEO
                    var bestWithoutVideo = videoInfo.bestAudioFormat();
                    if (bestWithoutVideo != null) return bestWithoutVideo.url();
                }

                // GIVE IT TO VLC
                return "https://www.youtube.com/watch?v=" + videoId;
            } catch (Exception e) {
                throw new PatchingUrlException(url.toString(), e);
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
        return new String(conn.getInputStream().readAllBytes());
    }
}
