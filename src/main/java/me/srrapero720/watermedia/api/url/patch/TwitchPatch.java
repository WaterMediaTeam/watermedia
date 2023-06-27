package me.srrapero720.watermedia.api.url.patch;

import me.srrapero720.watermedia.api.url.URLPatch;
import me.srrapero720.watermedia.api.url.patch.util.twitch.TwitchUtil;
import org.jetbrains.annotations.NotNull;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.URL;

// Planned
public class TwitchPatch extends URLPatch {

    static {
        CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));
    }

    @Override
    public boolean isValid(@NotNull URL url) {
        return (url.getHost().equals("www.twitch.tv") || url.getHost().equals("twitch.tv")) && url.getPath().startsWith("/");
    }

    @Override
    public String patch(@NotNull URL url) throws PatchingUrlException {
        super.patch(url);
        try {
            return TwitchUtil.getStream(url.getPath().substring(1)).get(0).getUrl();
        } catch (Exception e) {
            throw new PatchingUrlException(url, e);
        }
    }
}
