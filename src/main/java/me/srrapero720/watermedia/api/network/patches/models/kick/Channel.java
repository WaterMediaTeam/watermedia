package me.srrapero720.watermedia.api.network.patches.models.kick;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;

public class Channel implements Serializable {

    @SerializedName("id")
    @Expose
    public int id;

    @SerializedName("user_id")
    @Expose
    public int userId;

    @SerializedName("slug")
    @Expose
    public String slug;

    @SerializedName("playback_url")
    @Expose
    public String url;

    @SerializedName("livestream")
    @Expose
    public Livestream livestream;

    @SerializedName("user")
    @Expose
    public User user;

    @SerializedName("offline_banner_image")
    @Expose
    public OfflineBanner offline_banner;

    public static class Livestream implements Serializable {
        @SerializedName("id")
        @Expose
        public int id;

        @SerializedName("slug")
        @Expose
        public String slug;

        @SerializedName("is_live")
        @Expose
        public boolean is_live;

        @SerializedName("duration")
        @Expose
        public long duration;

        @SerializedName("session_title")
        @Expose
        public String title;
    }

    public static class OfflineBanner implements Serializable {

        @SerializedName("src")
        @Expose
        public String src;

        @SerializedName("srcset")
        @Expose
        public String srcset;

        public URI[] getSrcset() throws URISyntaxException {
            String[] urls = srcset.split(" ");
            URI[] result = new URI[urls.length / 2];

            for (int i = 0, j = 0; i < urls.length; i = i + 2, j++) {
                result[j] = new URI(urls[i]);
            }

            return result;
        }
    }
}
