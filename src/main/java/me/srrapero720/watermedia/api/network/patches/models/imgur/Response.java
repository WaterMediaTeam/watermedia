package me.srrapero720.watermedia.api.network.patches.models.imgur;

import com.google.gson.annotations.SerializedName;

public class Response<T> {
    @SerializedName("data")
    public T data;

    @SerializedName("success")
    public boolean success;

    @SerializedName("status")
    public int status;
}
