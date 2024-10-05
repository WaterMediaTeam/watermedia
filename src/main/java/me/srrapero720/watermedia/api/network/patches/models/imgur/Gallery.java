package me.srrapero720.watermedia.api.network.patches.models.imgur;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class Gallery {

    @SerializedName("id")
    public String id;

    @SerializedName("title")
    public String title;

    @SerializedName("description")
    public String desc;

    @SerializedName("datetime")
    public long date;

    @SerializedName("cover")
    public String cover;

    @SerializedName("account_url")
    public String accountUrl;

    @SerializedName("account_id")
    public int accountId;

    @SerializedName("privacy")
    public String privacy;

    @SerializedName("layout")
    public String layout;

    @SerializedName("views")
    public int views;

    @SerializedName("link")
    public String link;

    @SerializedName("ups")
    public int ups;

    @SerializedName("downs")
    public int downs;

    @SerializedName("points")
    public int points;

    @SerializedName("score")
    public int score;

    @SerializedName("is_album")
    public boolean album;

    @SerializedName("vote")
    public String vote;

    @SerializedName("comment_count")
    public int commentCount;

    @SerializedName("images_count")
    public int imagesCount;

    @SerializedName("images")
    public Image[] images;

    public String getFormattedDate() {
        Date time = new Date(date * 1000);
        return new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(time);
    }
}
