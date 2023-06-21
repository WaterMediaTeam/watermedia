package me.srrapero720.watermedia.api.url.patch;

import com.github.kiulian.downloader.YoutubeDownloader;
import com.github.kiulian.downloader.downloader.request.RequestVideoInfo;
import me.srrapero720.watermedia.api.url.URLPatch;
import org.jetbrains.annotations.NotNull;

import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class YoutubePatch extends URLPatch {
    private static final Pattern PATTERN = Pattern.compile("(?:(?:youtube\\.com\\/(?:watch\\?.*v=|user\\/\\S+|(?:v|embed)\\/)|youtu\\.be\\/)([^&\\n?#]+))");

    @Override
    public boolean isValid(@NotNull URL url) {
        return (url.getHost().contains("youtube.com") || url.getHost().contains("youtu.be"));
    }

    @Override
    public String build(@NotNull URL url) throws PatchingUrlException {
        super.build(url);

        Matcher matcher = PATTERN.matcher(url.toString());
        if (matcher.find()) {
            try {
                String videoId = matcher.group(1);
                var videoInfo = new YoutubeDownloader().getVideoInfo(new RequestVideoInfo(videoId)).data();

                // BEST WITH AUDIO
                var bestWithAudio = videoInfo.bestVideoWithAudioFormat();
                if (bestWithAudio != null) return bestWithAudio.url();

                // WITHOUT AUDIO
                var bestWithoutAudio = videoInfo.bestVideoFormat();
                if (bestWithoutAudio != null) return bestWithoutAudio.url();

                // WITHOUT VIDEO
                var bestWithoutVideo = videoInfo.bestAudioFormat();
                if (bestWithoutVideo != null) bestWithoutVideo.url();

                // GIVE IT TO VLC
                return "https://www.youtube.com/watch?v=" + videoId;
            } catch (Exception e) {
                throw new PatchingUrlException(url.toString(), e);
            }
        }

        return null;
    }
}
