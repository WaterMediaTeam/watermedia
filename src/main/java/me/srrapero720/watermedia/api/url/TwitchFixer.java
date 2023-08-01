package me.srrapero720.watermedia.api.url;

import me.srrapero720.watermedia.api.network.twitch.TwitchUtil;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.URL;

// Planned
public class TwitchFixer extends AbstractFixer {

    static {
        CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));
    }

    @Override
    public boolean isValid(URL url) {
        return (url.getHost().equals("www.twitch.tv") || url.getHost().equals("twitch.tv")) && url.getPath().startsWith("/");
    }

    @Override
    public URL patch(URL url) throws PatchingUrlException {
        super.patch(url);
        try {
            String path = url.getPath();
            if (path.startsWith("/videos/")) {
                return new URL(TwitchUtil.getVod(path.substring(8)).get(0).getUrl());
            }

            return new URL(TwitchUtil.getStream(path.substring(1)).get(0).getUrl());
        } catch (Exception e) {
            throw new PatchingUrlException(url, e);
        }
    }
}
