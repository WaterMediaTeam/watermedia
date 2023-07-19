package me.srrapero720.watermedia.api.url.patch;

import com.google.gson.Gson;
import me.srrapero720.watermedia.api.url.URLPatch;
import me.srrapero720.watermedia.api.url.patch.util.twitter.TweetScrapper;

import java.net.URL;

public class TwitterPatch extends URLPatch {

    private static final Gson gson = new Gson();

    @Override
    public boolean isValid(URL url) {
        return (url.getHost().equals("www.twitter.com") || url.getHost().equals("twitter.com")) && url.getPath().matches("/[a-zA-Z0-9_]+/status/[0-9]+");
    }

    @Override
    public String patch(URL url) throws PatchingUrlException {
        super.patch(url);
        try {
            return new TweetScrapper(gson).extractVideo(String.valueOf(url)).get(0);
        } catch (Exception e) {
            throw new PatchingUrlException(url.toString(), e);
        }
    }
}