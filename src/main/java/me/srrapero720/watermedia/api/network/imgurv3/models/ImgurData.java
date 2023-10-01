package me.srrapero720.watermedia.api.network.imgurv3.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ImgurData<T> {
    @SerializedName("data")
    @Expose
    public T data;
}