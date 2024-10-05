package me.srrapero720.watermedia.api.network.patches.models.kick;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class User {

    @SerializedName("username")
    @Expose
    public String username;

    @SerializedName("profile_pic")
    @Expose
    public String profile_pic;
}
