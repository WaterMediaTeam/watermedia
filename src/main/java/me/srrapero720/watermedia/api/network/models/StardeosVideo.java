package me.srrapero720.watermedia.api.network.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class StardeosVideo implements Serializable {
    @SerializedName("id")
    @Expose
    public String id;

    @SerializedName("isLiveStream")
    @Expose
    public boolean isLiveStream;

    @SerializedName("files")
    @Expose
    public List<Files> files;

    public static class Files implements Serializable {
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
