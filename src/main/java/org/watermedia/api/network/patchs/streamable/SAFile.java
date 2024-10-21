package org.watermedia.api.network.patchs.streamable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SAFile {
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
