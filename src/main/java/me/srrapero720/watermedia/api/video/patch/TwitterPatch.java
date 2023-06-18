package me.srrapero720.watermedia.api.video.patch;

import com.google.gson.Gson;
import me.srrapero720.watermedia.api.video.patch.util.twitter.TweetScrapper;
import org.jetbrains.annotations.NotNull;

import java.net.URL;

public class TwitterPatch extends AbstractURLPatch {

    private static final Gson gson = new Gson();

    @Override
    public boolean isValid(@NotNull URL url) {
        return (url.getHost().equals("www.twitter.com") || url.getHost().equals("twitter.com")) && url.getPath().matches("/[a-zA-Z0-9_]+/status/[0-9]+");
    }

    @Override
    public String build(@NotNull URL url) throws PatchingUrlException {
        super.build(url);
        try {
            return new TweetScrapper(gson).extractVideo(String.valueOf(url)).get(0);
        } catch (Exception e) {
            throw new PatchingUrlException(url.toString(), e);
        }
    }
}