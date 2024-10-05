package me.srrapero720.watermedia.api.network.patches.models.streamable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Video {
    @SerializedName("status")
    @Expose
    public int status;

    @SerializedName("percent")
    @Expose
    public int percent;

    @SerializedName("message")
    @Expose
    public String message;

    @SerializedName("files")
    @Expose
    public Media files;

    @SerializedName("thumbnail_url")
    @Expose
    public String thumbnail_url;

    @SerializedName("title")
    @Expose
    public String title;
}