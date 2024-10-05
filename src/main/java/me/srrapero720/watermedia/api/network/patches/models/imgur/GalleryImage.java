package me.srrapero720.watermedia.api.network.patches.models.imgur;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

@Deprecated
public class GalleryImage {
    @SerializedName("id")
    public String id;

    @SerializedName("title")
    public String title;

    @SerializedName("description")
    public String description;

    @SerializedName("datetime")
    private long date;

    @SerializedName("type")
    public String type;

    @SerializedName("animated")
    public boolean animated;

    @SerializedName("width")
    public int width;

    @SerializedName("height")
    public int height;

    @SerializedName("size")
    public int size;

    @SerializedName("views")
    public int views;

    @SerializedName("comment_count")
    public int commentCount;

    @SerializedName("bandwidth")
    public long bandwidth;

    @SerializedName("vote")
    public String vote;

    @SerializedName("section")
    public String section;

    @SerializedName("account_url")
    public String accountUrl;

    @SerializedName("account_id")
    public int accountId;

    @SerializedName("ups")
    public int ups;

    @SerializedName("downs")
    public int downs;

    @SerializedName("points")
    public int points;

    @SerializedName("score")
    public long score;

    @SerializedName("is_album")
    public boolean album;

    public String getFormattedDate() {
        Date time = new Date(date * 1000);
        return new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(time);
    }
}
