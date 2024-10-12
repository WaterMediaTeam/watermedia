package org.watermedia.core.network.patchs;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import me.srrapero720.watermedia.api.MediaContext;
import me.srrapero720.watermedia.api.MediaType;
import org.watermedia.api.network.MediaURI;
import org.watermedia.api.network.URIPatchException;
import me.srrapero720.watermedia.tools.DataTool;
import me.srrapero720.watermedia.tools.NetTool;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

import static org.watermedia.WaterMedia.LOGGER;

public class KickPatch extends AbstractPatch {
    private static final String API_URL = "https://kick.com/api/v1/";

    @Override
    public String platform() {
        return "Kick";
    }

    @Override
    public boolean active(MediaContext context) {
        return true;
    }

    @Override
    public boolean validate(MediaURI source) {
        return source.getUri().getHost().equals("kick.com");
    }

    @Override
    public void patch(MediaContext context, MediaURI source) throws URIPatchException {
        var path = source.getUri().getPath();

        try {
            if (path.contains("/video/")) {
                String[] pathSplit = path.split("/");
                String videoId = pathSplit[pathSplit.length - 1];
                Video video = DataTool.fromJSON(this.connectToKick(API_URL + "video/" + videoId), Video.class);

                // METADATA BUILDING
                var metadata = new MediaURI.Metadata(
                        video.livestream.title,
                        video.livestream.channel.name,
                        this.platform(),
                        "",
                        new URI(video.livestream.thumbnail),
                        video.livestream.duration
                );

                // PATCH BUILDING
                var patch = new MediaURI.Patch();
                patch.setMetadata(metadata);

                // PATCH APPLY
                source.apply(patch.addSource()
                        .setUri(new URI(video.url))
                        .setIsLive(false)
                        .setType(MediaType.VIDEO)
                        // TODO: put offline banner as fallback uri.
                        // .setFallbackUri(new URI(video.livestream.thumbnail))
                        // .setFallbackType(MediaType.IMAGE)
                        .build()
                );
            } else {
                String channelId = path.substring(1);
                Channel ch = DataTool.fromJSON(this.connectToKick(API_URL + "channels/" + channelId), Channel.class);
                if (ch.livestream == null || !ch.livestream.is_live) {
                    LOGGER.warn("Streamer {} is not online", ch.slug);
                }

                // METADATA BUILDING
                var metadata = new MediaURI.Metadata(
                        ch.livestream.title,
                        ch.user.username,
                        this.platform(),
                        ch.livestream.slug,
                        ch.offline_banner.getSrcset()[0],
                        ch.livestream.duration
                );

                // PATCH BUILDING
                var patch = new MediaURI.Patch();
                patch.setMetadata(metadata);

                // PATCH APPLY
                source.apply(patch.addSource()
                        .setUri(new URI(ch.url))
                        .setIsLive(true)
                        .setType(MediaType.VIDEO)
                        .setFallbackUri(ch.offline_banner.getSrcset()[0])
                        .build()
                );
            }
        } catch (Exception e) {
            throw new URIPatchException(source, e);
        }
    }

    @Override
    public void test(MediaContext context, String url) {

    }

    public String connectToKick(String url) throws IOException {
        HttpURLConnection conn = NetTool.connect(url, "GET");
        int code = conn.getResponseCode();

        switch (code) {
            case HttpURLConnection.HTTP_NOT_FOUND -> throw new NullPointerException("Streamer or Vod was not found");
            case HttpURLConnection.HTTP_FORBIDDEN, HttpURLConnection.HTTP_UNAUTHORIZED -> throw new UnsupportedOperationException("Access denied by Kick");
            default -> {
                if (code != HttpURLConnection.HTTP_OK)
                    throw new UnsupportedOperationException("Kick responses with a unexpected status code: " + code);
            }
        }

        try (InputStream in = conn.getInputStream()) {
            return new String(DataTool.readAllBytes(in), StandardCharsets.UTF_8);
        } finally {
            conn.disconnect();
        }
    }

    public static class Channel implements Serializable {

        @SerializedName("id")
        @Expose
        public int id;

        @SerializedName("user_id")
        @Expose
        public int userId;

        @SerializedName("slug")
        @Expose
        public String slug;

        @SerializedName("playback_url")
        @Expose
        public String url;

        @SerializedName("livestream")
        @Expose
        public Livestream livestream;

        @SerializedName("user")
        @Expose
        public User user;

        @SerializedName("offline_banner_image")
        @Expose
        public OfflineBanner offline_banner;

        public static class Livestream implements Serializable {
            @SerializedName("id")
            @Expose
            public int id;

            @SerializedName("slug")
            @Expose
            public String slug;

            @SerializedName("is_live")
            @Expose
            public boolean is_live;

            @SerializedName("duration")
            @Expose
            public long duration;

            @SerializedName("session_title")
            @Expose
            public String title;
        }

        public static class OfflineBanner implements Serializable {

            @SerializedName("src")
            @Expose
            public String src;

            @SerializedName("srcset")
            @Expose
            public String srcset;

            public URI[] getSrcset() throws URISyntaxException {
                String[] urls = srcset.split(" ");
                URI[] result = new URI[urls.length / 2];

                for (int i = 0, j = 0; i < urls.length; i = i + 2, j++) {
                    result[j] = new URI(urls[i]);
                }

                return result;
            }
        }
    }

    public static class User {

        @SerializedName("username")
        @Expose
        public String username;

        @SerializedName("profile_pic")
        @Expose
        public String profile_pic;
    }

    public static class Video implements Serializable {
        @SerializedName("id")
        @Expose
        public int id;

        @SerializedName("slug")
        @Expose
        public int name;

        @SerializedName("live_stream_id")
        @Expose
        public int streamId;

        @SerializedName("source")
        @Expose
        public String url;

        @SerializedName("livestream")
        @Expose
        public Livestream livestream;


        public static class Livestream {
            @SerializedName("id")
            @Expose
            public int id;

            @SerializedName("slug")
            @Expose
            public String slug;

            @SerializedName("session_title")
            @Expose
            public String title;

            @SerializedName("is_live")
            @Expose
            public boolean is_live;

            @SerializedName("duration")
            @Expose
            public long duration;

            @SerializedName("channel")
            @Expose
            public Channel channel;

            @SerializedName("thumbnail")
            @Expose
            public String thumbnail;

            public static class Channel {
                @SerializedName("id")
                @Expose
                public int id;

                @SerializedName("slug")
                @Expose
                public String name;
            }
        }
    }
}