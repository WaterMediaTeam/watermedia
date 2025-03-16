package org.watermedia.api.network.patchs;

import com.google.gson.annotations.SerializedName;
import org.watermedia.core.tools.DataTool;
import org.watermedia.core.tools.NetTool;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TwitterPatch extends AbstractPatch {
    private static final String API_URL = "https://cdn.syndication.twimg.com/tweet-result?id=%s&token=%s&lang=en";
    private static final String API_KEY = "watermedia-java-x-access-token";
    private static final Pattern ID_PATTERN = Pattern.compile("status/(\\d+)$", Pattern.CASE_INSENSITIVE);

    private static final String __TYPE_T = "Tweet";
    private static final String __TYTE_TOMB = "TweetTombstone";

    @Override
    public String platform() {
        return "Twitter (X)";
    }

    @Override
    public boolean isValid(URI uri) {
        String host = uri.getHost();
        String path = uri.getPath();

        if (host == null || path == null) return false;

        if (path.endsWith("/")) path = path.substring(0, path.length() - 1);

        return (host.equals("www.x.com") || host.equals("x.com") || host.equals("www.twitter.com") || host.equals("twitter.com"))
                && ID_PATTERN.matcher(path).find();
    }

    @Override
    public Result patch(URI uri, Quality prefQuality) throws FixingURLException {
        try {
            String path = uri.getPath();

            if (path.endsWith("/")) path = path.substring(0, path.length() - 1);

            final Matcher m = ID_PATTERN.matcher(path);
            if (!m.find()) throw new Exception("No twitter ID match found");
            final String apiURL = String.format(API_URL, m.group(1), API_KEY);

            final HttpURLConnection conn = NetTool.connectToHTTP(apiURL, "GET");

            int code = conn.getResponseCode();
            switch (code) {
                case HttpURLConnection.HTTP_INTERNAL_ERROR: throw new Exception("Twitter died");
                case HttpURLConnection.HTTP_NOT_FOUND: throw new NullPointerException("Tweet not found");
                case HttpURLConnection.HTTP_FORBIDDEN:
                case HttpURLConnection.HTTP_UNAUTHORIZED:
                        throw new UnsupportedOperationException("Twitter blocked us API access - URL: " + apiURL);
                default:
                    if (code != HttpURLConnection.HTTP_OK)
                        throw new UnsupportedOperationException("Unexpected response from twitter (" + code + ") - URL: " + apiURL);
            }

            try (final InputStream in = conn.getInputStream()) {
                final Tweet tweet = DataTool.fromJSON(new String(DataTool.readAllBytes(in)), Tweet.class);

                if (tweet.typename.equals(__TYTE_TOMB)) {
                    throw new UnsupportedOperationException("Tomb received: " + tweet.tombstone.text);
                }

                if (tweet.mediaDetails == null || tweet.mediaDetails.length == 0) {
                    throw new NullPointerException("No media was detected");
                }

                MediaDetail media = tweet.mediaDetails[0];

                if (media.type.equals("photo")) {
                    return new Result(new URI(media.mediaUrlHttps), false, false);
                } else if (media.type.equals("video")) {
                    return new Result(new URI(media.videoInfo.variants[0].url), true, false);
                } else {
                    throw new UnsupportedOperationException("Unsupported media type was detected");
                }
            } finally {
                conn.disconnect();
            }
        } catch (Exception e) {
            throw new FixingURLException(uri, e);
        }
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
        public User user;

        @SerializedName("mediaDetails")
        public MediaDetail[] mediaDetails;

        @SerializedName("photos")
        public Photo[] photos;

        @SerializedName("video")
        public Video video;

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