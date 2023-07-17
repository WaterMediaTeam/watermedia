package me.srrapero720.watermedia;

import com.twitter.clientlib.TwitterCredentialsOAuth2;
import com.twitter.clientlib.api.TwitterApi;
import me.srrapero720.watermedia.api.video.VideoLANPlayer;

import java.util.HashSet;
import java.util.Set;

public class Testo {
    public static void t() {
        var player = new VideoLANPlayer(null, null, null);
        player.start("urk");

    }

    TwitterApi apiInstance = new TwitterApi(new TwitterCredentialsOAuth2(
            System.getenv("TWITTER_OAUTH2_CLIENT_ID"),
            System.getenv("TWITTER_OAUTH2_CLIENT_SECRET"),
            System.getenv("TWITTER_OAUTH2_ACCESS_TOKEN"),
            System.getenv("TWITTER_OAUTH2_REFRESH_TOKEN")));

    Set<String> tweetFields = new HashSet<>();
    tweetFields.add("author_id");
    tweetFields.add("id");
    tweetFields.add("created_at");

}
