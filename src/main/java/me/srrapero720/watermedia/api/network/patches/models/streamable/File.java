package me.srrapero720.watermedia.api.network.patches.models.streamable;

import com.google.gson.annotations.*;

public class File {
    @SerializedName("status")
    @Expose
    public int status;

    @SerializedName("url")
    @Expose
    public String url;

    @SerializedName("framerate")
    @Expose
    public int framerate;

    @SerializedName("width")
    @Expose
    public int width;

    @SerializedName("height")
    @Expose
    public int height;

    @SerializedName("bitrate")
    @Expose
    public int bitrate;

    @SerializedName("size")
    @Expose
    public int size;

    @SerializedName("duration")
    @Expose
    public float duration;
}