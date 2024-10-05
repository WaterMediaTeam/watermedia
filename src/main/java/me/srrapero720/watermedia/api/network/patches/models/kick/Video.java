package me.srrapero720.watermedia.api.network.patches.models.kick;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Video implements Serializable {
    @SerializedName("id")
    @Expose
    public int id;

    @SerializedName("slug")
    @Expose
    public int name;

    @SerializedName("live_stream_id")
    @Expose
    public int streamId;

    @SerializedName("source")
    @Expose
    public String url;

    @SerializedName("livestream")
    @Expose
    public Livestream livestream;


    public static class Livestream {
        @SerializedName("id")
        @Expose
        public int id;

        @SerializedName("slug")
        @Expose
        public String slug;

        @SerializedName("session_title")
        @Expose
        public String title;

        @SerializedName("is_live")
        @Expose
        public boolean is_live;

        @SerializedName("duration")
        @Expose
        public long duration;

        @SerializedName("channel")
        @Expose
        public Channel channel;

        @SerializedName("thumbnail")
        @Expose
        public String thumbnail;

        public static class Channel {
            @SerializedName("id")
            @Expose
            public int id;

            @SerializedName("slug")
            @Expose
            public String name;
        }
    }
}
