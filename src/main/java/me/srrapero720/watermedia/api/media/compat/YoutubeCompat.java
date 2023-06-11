package me.srrapero720.watermedia.api.media.compat;

import com.github.kiulian.downloader.YoutubeDownloader;
import com.github.kiulian.downloader.downloader.request.RequestVideoInfo;
import me.srrapero720.watermedia.MediaConfig;
import org.jetbrains.annotations.NotNull;

import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class YoutubeCompat extends CompatVideoUrl {
    private static final Pattern PATTERN = Pattern.compile("(?:(?:youtube\\.com\\/(?:watch\\?.*v=|user\\/\\S+|(?:v|embed)\\/)|youtu\\.be\\/)([^&\\n?#]+))");

    @Override
    public boolean isValid(@NotNull URL url) {
        return (url.getHost().contains("youtube.com") || url.getHost().contains("youtu.be"));
    }

    @Override
    public String build(@NotNull URL url) {
        super.build(url);

        Matcher matcher = PATTERN.matcher(url.toString());
        if (matcher.find()) {
            String videoId = matcher.group(1);
            return new YoutubeDownloader().getVideoInfo(new RequestVideoInfo(videoId)).data().bestVideoWithAudioFormat().url();
        }

        return null;
    }
}
