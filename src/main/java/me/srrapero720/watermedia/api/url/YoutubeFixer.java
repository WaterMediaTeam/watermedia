package me.srrapero720.watermedia.api.url;

import com.github.kiulian.downloader.YoutubeDownloader;
import com.github.kiulian.downloader.downloader.request.RequestVideoInfo;
import com.github.kiulian.downloader.model.videos.VideoDetails;
import com.github.kiulian.downloader.model.videos.VideoInfo;
import com.github.kiulian.downloader.model.videos.formats.AudioFormat;
import com.github.kiulian.downloader.model.videos.formats.VideoFormat;
import me.srrapero720.watermedia.api.network.twitch.StreamQuality;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class YoutubeFixer extends AbstractFixer {
    private static final Pattern PATTERN = Pattern.compile("(?:youtu\\.be/|youtube\\.com/(?:embed/|v/|shorts/|feeds/api/videos/|watch\\?v=|watch\\?.+&v=))([^/?&#]+)");

    @Override
    public boolean isValid(URL url) {
        return (url.getHost().endsWith("youtube.com") || url.getHost().endsWith("youtu.be"));
    }

    @Override
    public URL patch(URL url) throws PatchingUrlException {
        super.patch(url);

        Matcher matcher = PATTERN.matcher(url.toString());
        if (matcher.find()) {
            try {
                String videoId = matcher.group(1);
                VideoInfo videoInfo = new YoutubeDownloader().getVideoInfo(new RequestVideoInfo(videoId)).data();
                VideoDetails videoDetails = videoInfo.details();

                if (videoDetails.isLive()) {
                    // LIVE STREAM
                    String ytLivePlaylist = fetchLivePlaylist(videoDetails.liveUrl());
                    if (ytLivePlaylist != null) return new URL(StreamQuality.parse(ytLivePlaylist).get(0).getUrl());
                } else {
                    // BEST WITH AUDIO
                    VideoFormat bestWithAudio = videoInfo.bestVideoWithAudioFormat();
                    if (bestWithAudio != null) return new URL(bestWithAudio.url());

                    // WITHOUT AUDIO
                    VideoFormat bestWithoutAudio = videoInfo.bestVideoFormat();
                    if (bestWithoutAudio != null) return new URL(bestWithoutAudio.url());

                    // WITHOUT VIDEO
                    AudioFormat bestWithoutVideo = videoInfo.bestAudioFormat();
                    if (bestWithoutVideo != null) return new URL(bestWithoutVideo.url());
                }

                // GIVE IT TO VLC
                return new URL("https://www.youtube.com/watch?v=" + videoId);
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

        InputStream inputStream = conn.getInputStream();
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) != -1) {
            result.write(buffer, 0, length);
        }
        String response = result.toString("UTF-8");
        return response;
    }
}
