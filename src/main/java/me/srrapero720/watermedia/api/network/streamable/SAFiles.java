package me.srrapero720.watermedia.api.network.streamable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SAFiles {
    @SerializedName("mp4")
    @Expose
    public SAFile mp4;

    @SerializedName("mp4-mobile")
    @Expose
    public SAFile mp4_mobile;
}
