package me.srrapero720.watermedia.api.network.patches.models.streamable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Media {
    @SerializedName("mp4")
    @Expose
    public File mp4;

    @SerializedName("mp4-mobile")
    @Expose
    public File mp4_mobile;
}