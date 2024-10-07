package me.srrapero720.watermedia.api.network.patchs;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import me.srrapero720.watermedia.api.MediaContext;
import me.srrapero720.watermedia.api.MediaType;
import me.srrapero720.watermedia.api.network.MediaURI;
import me.srrapero720.watermedia.api.network.URIPatchException;
import me.srrapero720.watermedia.tools.DataTool;
import me.srrapero720.watermedia.tools.NetTool;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;

import static java.net.HttpURLConnection.*;

public class StreamablePatch extends AbstractPatch {
    private static final String API_URL = "https://api.streamable.com/videos/";

    @Override
    public String platform() {
        return "Streamable";
    }

    @Override
    public boolean validate(MediaURI source) {
        return source.getUri().getHost().equals("streamable.com");
    }

    @Override
    public MediaURI patch(MediaURI source, MediaContext context) throws URIPatchException {
        String videoId = source.getUri().getPath().substring(1);
        try {
            HttpURLConnection conn = NetTool.connect(API_URL + videoId, "GET");
            int code = conn.getResponseCode();
            switch (code) {
                case HTTP_NOT_FOUND -> throw new NullPointerException("Video doesn't exists");
                case HTTP_FORBIDDEN, HTTP_UNAUTHORIZED -> throw new NullPointerException("Video isn't public or streamable denied us the access");
                case HTTP_INTERNAL_ERROR, HTTP_UNAVAILABLE, HTTP_BAD_GATEWAY -> throw new NullPointerException("Streamable is not available right now");
                default -> {
                    if (code != HTTP_ACCEPTED) {
                        throw new UnsupportedOperationException("Streamable responses with the unexpected status code: " + code);
                    }
                }
            }

            try (InputStreamReader in = new InputStreamReader(conn.getInputStream())) {
                Video video = DataTool.fromJSON(in, Video.class);

                // METADATA BUILDING
                var metadata = new MediaURI.Metadata(video.title, "", this.platform(), "", new URI(video.thumbnail_url), (long) (video.files.mp4.duration * 1000));

                // PATCH BUILDING
                var patch = new MediaURI.Patch();
                patch.setMetadata(metadata);
                patch.addSource()
                        .setUri(new URI(video.files.mp4.url))
                        .setType(MediaType.VIDEO)
                        .build();

                // PATCH APPLY
                source.apply(patch);

                return source;
            } finally {
                conn.disconnect();
            }
        } catch (Exception e) {
            throw new URIPatchException(source, e);
        }
    }

    public static class File {
        @SerializedName("status")
        @Expose
        public int status;

        @SerializedName("url")
        @Expose
        public String url;

        @SerializedName("framerate")
        @Expose
        public int framerate;

        @SerializedName("width")
        @Expose
        public int width;

        @SerializedName("height")
        @Expose
        public int height;

        @SerializedName("bitrate")
        @Expose
        public int bitrate;

        @SerializedName("size")
        @Expose
        public int size;

        @SerializedName("duration")
        @Expose
        public float duration;
    }

    public static class Media {
        @SerializedName("mp4")
        @Expose
        public File mp4;

        @SerializedName("mp4-mobile")
        @Expose
        public File mp4_mobile;
    }

    public static class Video {
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
        public Media files;

        @SerializedName("thumbnail_url")
        @Expose
        public String thumbnail_url;

        @SerializedName("title")
        @Expose
        public String title;
    }
}
