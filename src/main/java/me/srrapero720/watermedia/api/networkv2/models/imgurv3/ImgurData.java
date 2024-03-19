package me.srrapero720.watermedia.api.networkv2.models.imgurv3;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ImgurData<T> {
    @SerializedName("data")
    @Expose
    public T data;
}