package me.srrapero720.watermedia.api.network.patchs;

import com.google.gson.Gson;
import me.srrapero720.watermedia.api.network.patchs.kick.KickChannel;
import me.srrapero720.watermedia.api.network.patchs.kick.KickVideo;
import me.srrapero720.watermedia.core.tools.DataTool;
import me.srrapero720.watermedia.core.tools.NetTool;

import java.io.*;
import java.net.*;

public class KickPatch extends AbstractPatch {
    private static final String API_URL = "https://kick.com/api/v1/";
    private static final Gson GSON = new Gson();

    @Override
    public String platform() {
        return "Kick";
    }

    @Override
    public boolean isValid(URI uri) {
        return uri.getHost().endsWith(".kick.com") || uri.getHost().equals("kick.com");
    }

    @Override
    public Result patch(URI uri, Quality prefQuality) throws FixingURLException {
        super.patch(uri, prefQuality);

        try {
            if (uri.getPath().contains("/video/")) {
                String videoID = uri.getPath().replace("/video/", "");
                KickVideo video = getVideoInfo(videoID);
                return new Result(new URI(video.url), true, false);
            } else {
                String streamerName = uri.getPath().replace("/", "");
                KickChannel channel = getChannelInfo(streamerName);
                if (channel.livestream == null || !channel.livestream.isStreaming) throw new ConnectException("Streamer is not online");
                return new Result(new URI(channel.url), true, true);
            }
        } catch (Exception e) {
            throw new FixingURLException(uri.toString(), e);
        }
    }

    public KickChannel getChannelInfo(String channel) throws Exception {
        try (InputStreamReader in = new InputStreamReader(getInputStream(new URI(API_URL + "channels/" + channel)))) {
            return GSON.fromJson(in, KickChannel.class);
        }
    }

    public KickVideo getVideoInfo(String videoId) throws Exception {
        try (InputStreamReader in = new InputStreamReader(getInputStream(new URI(API_URL + "video/" + videoId)))) {
            return GSON.fromJson(in, KickVideo.class);
        }
    }

    public InputStream getInputStream(URI url) throws IOException {
        HttpURLConnection connection = NetTool.connect(url, "GET");
        try {
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new ConnectException(String.format("Server url %s response with status code (%s): %s", url, connection.getResponseCode(), connection.getResponseMessage()));
            }
            return new ByteArrayInputStream(DataTool.readAllBytes(connection.getInputStream()));
        } finally {
            connection.disconnect();
        }
    }
}
