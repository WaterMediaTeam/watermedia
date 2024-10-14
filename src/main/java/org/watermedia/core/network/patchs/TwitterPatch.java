package org.watermedia.core.network.patchs;

import com.google.gson.annotations.SerializedName;
import me.srrapero720.watermedia.api.MediaContext;
import me.srrapero720.watermedia.api.MediaType;
import me.srrapero720.watermedia.api.Quality;
import org.watermedia.api.network.MediaURI;
import org.watermedia.core.network.NetworkPatchException;
import org.watermedia.tools.DataTool;
import org.watermedia.tools.NetTool;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.regex.Pattern;

public class TwitterPatch extends AbstractPatch {
    private static final String API_URL = "https://cdn.syndication.twimg.com/tweet-result?id=%s&token=%s&lang=en";
    private static final String API_KEY = "watermedia-java-x-access-token";
    private static final Pattern ID_PATTERN = Pattern.compile("^/([A-Za-z0-9_]+)/status/(\\d+)$");
    private static final Pattern RES_PATTERN = Pattern.compile("(\\d+)x(\\d+)");

    private static final String __TYPE_T = "Tweet";
    private static final String __TYTE_TOMB = "TweetTombstone";

    @Override
    public String platform() {
        return "Twitter (X)";
    }

    @Override
    public boolean active(MediaContext context) {
        return true;
    }

    @Override
    public boolean validate(MediaURI source) {
        var host = source.getUri().getHost();
        var path = source.getUri().getPath();
        return (host.equals("www.x.com") || host.equals("x.com") || host.equals("www.twitter.com") || host.equals("twitter.com"))
                && ID_PATTERN.matcher(path).matches();
    }

    @Override
    public void patch(MediaContext context, MediaURI source) throws NetworkPatchException {
        try {
            final var m = ID_PATTERN.matcher(source.getUri().getPath());
            if (!m.matches()) throw new Exception("No twitter ID match found");
            final var url = String.format(API_URL, m.group(2), API_KEY);

            final var conn = NetTool.connect(url, "GET");

            int code = conn.getResponseCode();
            switch (code) {
                case HttpURLConnection.HTTP_INTERNAL_ERROR -> throw new Exception("Twitter died");
                case HttpURLConnection.HTTP_NOT_FOUND -> throw new NullPointerException("Tweet not found");
                case HttpURLConnection.HTTP_FORBIDDEN, HttpURLConnection.HTTP_UNAUTHORIZED ->
                        throw new UnsupportedOperationException("Twitter blocked us API access - URL: " + url);
                default -> {
                    if (code != HttpURLConnection.HTTP_OK)
                        throw new UnsupportedOperationException("Unexpected response from twitter (" + code + ") - URL: " + url);
                }
            }

            try (final InputStream in = conn.getInputStream()) {
                final var tweet = DataTool.<Tweet>fromJSON(new String(DataTool.readAllBytes(in)), Tweet.class);
                final var patch = new MediaURI.Patch();

                if (tweet.typename.equals(__TYTE_TOMB)) {
                    throw new UnsupportedOperationException("Tomb received: " + tweet.tombstone.text);
                }

                // METADATA BUILDING
                final var metadata = new MediaURI.Metadata(
                        null,
                        tweet.user.name,
                        this.platform(),
                        tweet.text,
                        new URI(tweet.user.profileImageUrlHttps),
                        tweet.video != null ? tweet.video.durationMs : 0
                );

                for (MediaDetail details: tweet.mediaDetails) {
                    final var mediaSource = patch.addSource().setIsLive(false);

                    switch (details.type) {
                        case "photo" -> {
                            mediaSource.setType(MediaType.IMAGE);
                            mediaSource.setUri(new URI(details.mediaUrlHttps));
                        }
                        case "video" -> {
                            mediaSource.setType(MediaType.VIDEO);
                            for (VideoVariant videoVariant: details.videoInfo.variants) {
                                var matcher = RES_PATTERN.matcher(videoVariant.url);
                                if (videoVariant.url.contains(".mp4") && matcher.find()) {
                                    String width = matcher.group(1);
                                    mediaSource.putQualityIfAbsent(Quality.calculate(Integer.parseInt(width)), new URI(videoVariant.url));
                                    mediaSource.setFallbackUri(new URI(details.mediaUrlHttps));
                                    mediaSource.setFallbackType(MediaType.IMAGE);
                                }
                            }
                        }
                        default -> throw new UnsupportedOperationException("Unsupported media type!");
                    }

                    mediaSource.build();
                }

                source.apply(patch.setMetadata(metadata));
            } finally {
                conn.disconnect();
            }
        } catch (Exception e) {
            throw new NetworkPatchException(source, e);
        }
    }

    @Override
    public void test(MediaContext context, String url) {

    }

    private static class Tweet {
        @SerializedName("__typename")
        public String typename;

        @SerializedName("lang")
        public String lang;

        @SerializedName("favorite_count")
        public int favoriteCount;

        @SerializedName("possibly_sensitive")
        public boolean possiblySensitive;

        @SerializedName("created_at")
        public String createdAt;

        @SerializedName("id_str")
        public String idStr;

        @SerializedName("text")
        public String text;

        @SerializedName("user")
        public TwitterPatch.User user;

        @SerializedName("mediaDetails")
        public TwitterPatch.MediaDetail[] mediaDetails;

        @SerializedName("photos")
        public TwitterPatch.Photo[] photos;

        @SerializedName("video")
        public TwitterPatch.Video video;

        @SerializedName("tombstone")
        public Tombstone tombstone;
    }

    private static class Video {
        @SerializedName("aspectRatio")
        public int[] aspectRatio;

        @SerializedName("contentType")
        public String contentType;

        @SerializedName("durationMs")
        public long durationMs;

        @SerializedName("poster")
        public String poster;

        @SerializedName("variants")
        public VideoVariant[] variants;
    }

    private static class Photo {
        @SerializedName("url")
        public String url;

        @SerializedName("width")
        public int width;

        @SerializedName("height")
        public int height;
    }

    private static class VideoVariant {
        @SerializedName("bitrate")
        public int bitrate;

        @SerializedName("content_type")
        public String contentType;

        @SerializedName("url")
        public String url;
    }

    private static class VideoInfo {
        @SerializedName("duration_millis")
        public int durationMillis;

        @SerializedName("variants")
        public VideoVariant[] variants;
    }

    private static class MediaDetail {
        @SerializedName("display_url")
        public String displayUrl;

        @SerializedName("expanded_url")
        public String expandedUrl;

        @SerializedName("media_url_https")
        public String mediaUrlHttps;

        @SerializedName("type")
        public String type;

        @SerializedName("video_info")
        public VideoInfo videoInfo;
    }

    private static class User {
        @SerializedName("id_str")
        public String idStr;

        @SerializedName("name")
        public String name;

        @SerializedName("profile_image_url_https")
        public String profileImageUrlHttps;

        @SerializedName("screen_name")
        public String screenName;

        @SerializedName("verified")
        public boolean verified;

        @SerializedName("is_blue_verified")
        public boolean isBlueVerified;
    }

    private static class Media {
        @SerializedName("display_url")
        public String displayUrl;

        @SerializedName("expanded_url")
        public String expandedUrl;

        @SerializedName("url")
        public String url;
    }

    private static class Tombstone {
        @SerializedName("text")
        public Text text;
    }

    private static class Text {
        @SerializedName("text")
        public String text;

        @SerializedName("rtl")
        public boolean rtl;
    }
}
