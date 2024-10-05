package me.srrapero720.watermedia.api.network.patches.models.twich;

import com.google.gson.annotations.SerializedName;

public class GQLData {
    @SerializedName(value = "videoPlaybackAccessToken", alternate = "streamPlaybackAccessToken")
    public Token accessToken;

    public static class Token {
        @SerializedName("signature")
        public String signature;

        @SerializedName("value")
        public String value;
    }
}