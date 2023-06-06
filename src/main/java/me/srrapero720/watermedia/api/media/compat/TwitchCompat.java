package me.srrapero720.watermedia.api.media.compat;

import me.srrapero720.watermedia.api.media.compat.util.twitch.TwitchUtil;
import org.jetbrains.annotations.NotNull;

import java.net.URL;

// Planned
public class TwitchCompat extends CompatVideoUrl {
    @Override
    public boolean isValid(@NotNull URL url) {
        return (url.getHost().equals("www.twitch.tv") || url.getHost().equals("twitch.tv")) && url.getPath().startsWith("/");
    }

    @Override
    public String build(@NotNull URL url) {
        try {
            return TwitchUtil.getStream(url.getPath().substring(1)).get(0).getUrl();
        } catch (Exception e) {
            return null;
        }
    }
}
