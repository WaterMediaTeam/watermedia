package me.srrapero720.watermedia.api.url;

import me.srrapero720.watermedia.api.network.twitch.TwitchUtil;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.URL;

public class TwitchFixer extends URLFixer {
    static {
        CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));
    }

    @Override
    public String platform() {
        return "Twitch";
    }

    @Override
    public boolean isValid(URL url) {
        return (url.getHost().equals("www.twitch.tv") || url.getHost().equals("twitch.tv")) && url.getPath().startsWith("/");
    }

    @Override
    public Result patch(URL url, Quality preferQuality) throws FixingURLException {
        super.patch(url, preferQuality);
        try {
            String path = url.getPath();
            if (path.startsWith("/videos/")) {
                return new Result(new URL(TwitchUtil.getVod(path.substring(8)).get(0).getUrl()), true, false);
            }

            return new Result(new URL(TwitchUtil.getStream(path.substring(1)).get(0).getUrl()), true, true);
        } catch (Exception e) {
            throw new FixingURLException(url, e);
        }
    }
}