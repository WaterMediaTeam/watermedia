package me.srrapero720.watermedia.api.network.patches.models.imgur;

import com.google.gson.annotations.SerializedName;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Image {

    @SerializedName("id")
    public String id;

    @SerializedName("deletehash")
    public String deleteHash;

    @SerializedName("account_id")
    public int accountId;

    @SerializedName("account_url")
    public String accountUrl;

    @SerializedName("ad_type")
    public int adType;

    @SerializedName("ad_url")
    public String adUrl;

    @SerializedName("title")
    public String title;

    @SerializedName("description")
    public String desc;

    @SerializedName("name")
    public String name;

    @SerializedName("type")
    public String type;

    @SerializedName("width")
    public int width;

    @SerializedName("height")
    public int height;

    @SerializedName("size")
    public int size;

    @SerializedName("views")
    public int views;

    @SerializedName("section")
    public String section;

    @SerializedName("vote")
    public String vote;

    @SerializedName("bandwidth")
    public long bandwidth;

    @SerializedName("animated")
    public boolean anim;

    @SerializedName("favorite")
    public boolean fav;

    @SerializedName("in_gallery")
    public boolean gallery;

    @SerializedName("in_most_viral")
    public boolean viral;

    @SerializedName("has_sound")
    public boolean sound;

    @SerializedName("is_ad")
    public boolean ad;

    @SerializedName("nsfw")
    public String nsfw;

    @SerializedName("link")
    public String link;

//    @SerializedName("tags")
//    public Object[] tags;

    @SerializedName("datetime")
    public long date;

    @SerializedName("mp4")
    public String mp4;

    @SerializedName("hls")
    public String hls;

    public String getFormattedDate() {
        Date time = new java.util.Date(date * 1000);
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(time);
    }
}
