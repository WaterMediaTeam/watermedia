package me.srrapero720.watermedia.api.url;

import com.google.gson.Gson;
import me.srrapero720.watermedia.api.network.twitch.TwitchUtil;
import me.srrapero720.watermedia.api.network.twitter.TweetScrapper;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.URL;

// TODO: BUSTED
public class TwitterFixer extends FixerBase {

    private static final Gson gson = new Gson();

    static {
        CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));
    }

    @Override
    public boolean isValid(URL url) {
        return (url.getHost().equals("www.twitter.com") || url.getHost().equals("twitter.com")) && url.getPath().matches("/[a-zA-Z0-9_]+/status/[0-9]+");
    }

    @Override
    public Result patch(URL url, Quality preferQuality) throws FixingURLException {
        super.patch(url, preferQuality);
        try {
            return new Result(new URL(new TweetScrapper(gson).extractVideo(String.valueOf(url)).get(0)), true, false);
        } catch (Exception e) {
            throw new FixingURLException(url.toString(), e);
        }
    }
}
