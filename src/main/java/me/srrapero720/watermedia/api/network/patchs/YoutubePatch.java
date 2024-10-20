package me.srrapero720.watermedia.api.network.patchs;

import com.github.kiulian.downloader.YoutubeDownloader;
import com.github.kiulian.downloader.downloader.request.RequestVideoInfo;
import com.github.kiulian.downloader.model.videos.VideoDetails;
import com.github.kiulian.downloader.model.videos.VideoInfo;
import com.github.kiulian.downloader.model.videos.formats.AudioFormat;
import com.github.kiulian.downloader.model.videos.formats.VideoFormat;
import me.srrapero720.watermedia.api.network.patchs.twitch.StreamQuality;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class YoutubePatch extends AbstractPatch {
    private static final YoutubeDownloader DOWNLOADER = new YoutubeDownloader();
    private static final Pattern PATTERN = Pattern.compile("(?:youtu\\.be/|youtube\\.com/(?:embed/|v/|shorts/|feeds/api/videos/|watch\\?v=|watch\\?.+&v=))([^/?&#]+)");

    @Override
    public String platform() {
        return "Youtube";
    }

    @Override
    public boolean isValid(URI uri) {
        return (uri.getHost().endsWith("youtube.com") || uri.getHost().endsWith("youtu.be"));
    }

    @Override
    public Result patch(URI uri, Quality preferQuality) throws FixingURLException {
        super.patch(uri, preferQuality);

        Matcher matcher = PATTERN.matcher(uri.toString());
        if (matcher.find()) {
            try {
                String videoId = matcher.group(1);
                VideoInfo videoInfo = DOWNLOADER.getVideoInfo(new RequestVideoInfo(videoId)).data();
                VideoDetails videoDetails = videoInfo.details();

                if (videoDetails.isLive()) {
                    // LIVE STREAM
                    String ytLivePlaylist = fetchLivePlaylist(videoDetails.liveUrl());
                    if (ytLivePlaylist != null) return new Result(new URI(StreamQuality.parse(ytLivePlaylist).get(0).getUrl()), true, true);
                } else {
                    // BEST WITH ALL
                    VideoFormat bestAll = videoInfo.bestVideoWithAudioFormat();
                    if (bestAll != null) return new Result(new URI(bestAll.url()), true, false);

                    // WITHOUT AUDIO
                    VideoFormat bestWithoutAudio = videoInfo.bestVideoFormat();
                    if (bestWithoutAudio != null) {
                        return new Result(new URI(bestWithoutAudio.url()), true, false);
                    }

                    // WITHOUT VIDEO
                    AudioFormat bestWithoutVideo = videoInfo.bestAudioFormat();
                    if (bestWithoutVideo != null) return new Result(new URI(bestWithoutVideo.url()), true, false);

//                    // BEST WITH ALL
//                    VideoFormat bestAll = videoInfo.bestVideoWithAudioFormat();
//
//                    // WITHOUT AUDIO
//                    VideoFormat bestWithoutAudio = videoInfo.bestVideoFormat();
//                    if (bestWithoutAudio != null) {
//                        // WITHOUT VIDEO
//                        AudioFormat bestWithoutVideo = videoInfo.bestAudioFormat();
//                        if (bestWithoutVideo != null) return new Result(new URL(bestWithoutAudio.url()), true, false).setAudioTrack(new URL(bestWithoutVideo.url()));
//
//                        if (bestAll != null) return new Result(new URL(bestAll.url()), true, false);
//
//                        return new Result(new URL(bestWithoutAudio.url()), true, false);
//                    }

                    if (bestAll != null) return new Result(new URI(bestAll.url()), true, false);
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