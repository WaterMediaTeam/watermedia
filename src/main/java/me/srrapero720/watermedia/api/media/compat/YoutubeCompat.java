package me.srrapero720.watermedia.api.media.compat;

import me.srrapero720.watermedia.MediaConfig;
import org.jetbrains.annotations.NotNull;

import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class YoutubeCompat extends CompatVideoUrl {

    private static final Pattern PATTERN = Pattern.compile("(?:(?:youtube\\.com\\/(?:watch\\?.*v=|user\\/\\S+|(?:v|embed)\\/)|youtu\\.be\\/)([^&\\n?#]+))");

    @Override
    public boolean isValid(@NotNull URL url) {
        return (url.getHost().equals("m.youtube.com") || url.getHost().equals("www.youtube.com") || url.getHost().equals("youtu.be")) && PATTERN.matcher(url.toString()).find();
    }

    @Override
    public String build(@NotNull URL url) {
        super.build(url);
        if (isStored(url)) return getStored(url);

        Matcher matcher = PATTERN.matcher(url.toString());
        if (matcher.find()) {
            String videoId = matcher.group(1);
            return storeUrl(url, "https://www.youtube.com/watch?v=" + videoId);
        }

        return null;
    }
}
