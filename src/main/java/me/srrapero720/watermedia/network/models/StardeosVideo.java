package me.srrapero720.watermedia.network.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public abstract class StardeosVideo {
    @SerializedName("id")
    @Expose
    public String id;

    @SerializedName("isLiveStream")
    @Expose
    public boolean isLiveStream;

    @SerializedName("files")
    @Expose
    public List<Files> files;

    public static abstract class Files {
        @SerializedName("fileUrl")
        @Expose
        public String fileUrl;

        @SerializedName("label")
        @Expose
        public int size;

        @SerializedName("alert")
        @Expose
        public boolean alert;
    }
}
