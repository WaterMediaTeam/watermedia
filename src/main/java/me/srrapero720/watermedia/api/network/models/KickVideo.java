package me.srrapero720.watermedia.api.network.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public abstract class KickVideo {
    @SerializedName("id")
    @Expose
    public int id;

    @SerializedName("live_stream_id")
    @Expose
    public int streamId;

    @SerializedName("source")
    @Expose
    public String url;
}
