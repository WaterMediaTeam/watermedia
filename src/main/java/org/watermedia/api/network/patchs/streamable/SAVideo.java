package org.watermedia.api.network.patchs.streamable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SAVideo {

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
    public SAFiles files;
}
