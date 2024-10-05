package me.srrapero720.watermedia.api.network.patches;

import me.srrapero720.watermedia.api.MediaContext;
import me.srrapero720.watermedia.api.MediaType;
import me.srrapero720.watermedia.api.network.MediaURI;
import me.srrapero720.watermedia.api.network.URIPatchException;
import me.srrapero720.watermedia.api.network.patches.models.kick.Channel;
import me.srrapero720.watermedia.api.network.patches.models.kick.Video;
import me.srrapero720.watermedia.tools.DataTool;
import me.srrapero720.watermedia.tools.NetTool;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;

import static me.srrapero720.watermedia.WaterMedia.LOGGER;

public class KickPatch extends AbstractPatch {
    private static final String API_URL = "https://kick.com/api/v1/";

    @Override
    public String platform() {
        return "Kick";
    }

    @Override
    public boolean validate(MediaURI source) {
        return source.getUri().getHost().equals("kick.com");
    }

    @Override
    public MediaURI patch(MediaURI source, MediaContext context) throws URIPatchException {
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

        return source;
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
}