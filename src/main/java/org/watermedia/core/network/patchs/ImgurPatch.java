package org.watermedia.core.network.patchs;

import com.google.gson.annotations.SerializedName;
import me.srrapero720.watermedia.api.MediaContext;
import me.srrapero720.watermedia.api.MediaType;
import org.watermedia.api.network.MediaURI;
import org.watermedia.api.network.URIPatchException;
import me.srrapero720.watermedia.tools.DataTool;
import me.srrapero720.watermedia.tools.NetTool;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ImgurPatch extends AbstractPatch {
    private static final String API_URL = "https://api.imgur.com/3";
    private static final String API_KEY = "685cdf74b1229b9";

    private static final String IMAGE_URL = API_URL + "/image/%s?client_id=" + API_KEY;
    private static final String GALLERY_URL = API_URL + "/gallery/%s?client_id=" + API_KEY;
    private static final String TAG_GALLERY_URL = API_URL + "/gallery/t/%s/%s?client_id=" + API_KEY;

    @Override
    public String platform() {
        return "Imgur";
    }

    @Override
    public boolean active(MediaContext context) {
        return true;
    }

    @Override
    public boolean validate(MediaURI source) {
        return source.getUri().getHost().equals("imgur.com"); // i.imgur.com - doesn't need patches
    }

    @Override
    public void patch(MediaContext context, MediaURI source) throws URIPatchException {
        final var path = source.getUri().getPath();
        final var fragment = source.getUri().getFragment();

        // SPECIAL PARSE
        final boolean gallery = path.startsWith("/gallery/") || path.startsWith("/a/");
        final boolean tagGallery = fragment != null && fragment.contains("#/t/");

        final var pathSplit = path.substring(1).split("/"); // remove first slash and get the path splitted
        final var idSplit = pathSplit[1].split("-"); // i hope imgur do their job and IDs don't contains "-"

        final var tag = fragment != null ? fragment.substring("#/t/".length()) : null;
        final var id = idSplit[idSplit.length - 1];

        try {
            if (gallery) { // open it as a gallery
                final var url = tagGallery ? String.format(TAG_GALLERY_URL, tag, id) : String.format(GALLERY_URL, id);
                final Response<Gallery> res = DataTool.fromJSON(connectResponse(url), Response.class);

                if (res.data == null || res.data.images.length == 0)
                    throw new NullPointerException("Response is successfully but data is null or images are empty");

                // METADATA BUILDING
                var metadata = new MediaURI.Metadata(
                        res.data.title,
                        res.data.accountUrl,
                        this.platform(),
                        DataTool.orElse(res.data.desc, res.data.images[0].desc),
                        null,
                        0
                        );

                // PATCH BUILDING
                var patch = new MediaURI.Patch();
                for (Image image: res.data.images) {
                    patch.addSource()
                            .setUri(new URI(image.link))
                            .setType(MediaType.getByMimetype(image.type))
                            .build();
                }

                patch.setMetadata(metadata);
                source.apply(patch);

            } else { // assume is a simple image
                final var url = String.format(IMAGE_URL, id);
                final Response<Image> res = DataTool.fromJSON(connectResponse(url), Response.class);

                if (res.data == null)
                    throw new NullPointerException("Response is successfully but data is null or images are empty");

                // METADATA BUILDING
                var metadata = new MediaURI.Metadata(
                        res.data.title,
                        res.data.accountUrl,
                        this.platform(),
                        res.data.desc,
                        null,
                        0
                );

                // PATCH BUILDING
                var patch = new MediaURI.Patch();
                patch.addSource()
                        .setUri(new URI(res.data.link))
                        .setType(MediaType.getByMimetype(res.data.type))
                        .build();
                patch.setMetadata(metadata);

                source.apply(patch);
            }
        } catch (Exception e) {
            throw new URIPatchException(source, e);
        }
    }

    @Override
    public void test(MediaContext context, String url) {

    }

    public String connectResponse(String url) throws IOException {
        HttpURLConnection conn = NetTool.connect(url, "GET");
        int code = conn.getResponseCode();
        switch (code) {
            case HttpURLConnection.HTTP_NOT_FOUND -> throw new NullPointerException("Image or gallery not founded");
            case HttpURLConnection.HTTP_FORBIDDEN, HttpURLConnection.HTTP_UNAUTHORIZED -> throw new UnsupportedOperationException("Access denied by Imgur");
            default -> {
                if (code != HttpURLConnection.HTTP_OK)
                    throw new UnsupportedOperationException("Imgur responses with a unexpected status code: " + code);
            }
        }

        try(InputStream in = conn.getInputStream()) {
            return new String(DataTool.readAllBytes(in), StandardCharsets.UTF_8);
        } finally {
            conn.disconnect();
        }
    }

    public static class Gallery {

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

    public static class Image {

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
            Date time = new Date(date * 1000);
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(time);
        }
    }

    public static class Response<T> {
        @SerializedName("data")
        public T data;

        @SerializedName("success")
        public boolean success;

        @SerializedName("status")
        public int status;
    }
}
