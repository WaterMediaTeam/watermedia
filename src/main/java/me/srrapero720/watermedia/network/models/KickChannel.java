package me.srrapero720.watermedia.network.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.Nullable;

public abstract class KickChannel {
    @SerializedName("id")
    @Expose
    public int id;

    @SerializedName("user_id")
    @Expose
    public int userId;

    @SerializedName("slug")
    @Expose
    public String username;

    @SerializedName("playback_url")
    @Expose
    public String url;

    @SerializedName("livestream")
    @Expose
    @Nullable
    public isLive isStreaming;

    public static abstract class isLive {
        @SerializedName("id")
        @Expose
        public int id;

        @SerializedName("is_live")
        @Expose
        public boolean isStreaming;
    }
}
