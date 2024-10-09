package me.srrapero720.watermedia.api.url.fixers;

import com.google.gson.Gson;
import me.srrapero720.watermedia.api.network.kick.KickChannel;
import me.srrapero720.watermedia.api.network.kick.KickVideo;
import me.srrapero720.watermedia.core.tools.DataTool;
import me.srrapero720.watermedia.core.tools.NetTool;

import java.io.*;
import java.net.*;

public class KickFixer extends URLFixer {
    private static final String API_URL = "https://kick.com/api/v1/";
    private static final Gson GSON = new Gson();

    @Override
    public String platform() {
        return "Kick";
    }

    @Override
    public boolean isValid(URL url) {
        return url.getHost().endsWith(".kick.com") || url.getHost().equals("kick.com");
    }

    @Override
    public Result patch(URL url, Quality prefQuality) throws FixingURLException {
        super.patch(url, prefQuality);

        try {
            if (url.getPath().contains("/video/")) {
                String videoID = url.getPath().replace("/video/", "");
                KickVideo video = getVideoInfo(videoID);
                return new Result(new URL(video.url), true, false);
            } else {
                String streamerName = url.getPath().replace("/", "");
                KickChannel channel = getChannelInfo(streamerName);
                if (channel.livestream == null || !channel.livestream.isStreaming) throw new ConnectException("Streamer is not online");
                return new Result(new URL(channel.url), true, true);
            }
        } catch (Exception e) {
            throw new FixingURLException(url.toString(), e);
        }
    }

    public KickChannel getChannelInfo(String channel) throws IOException {
        try (InputStreamReader in = new InputStreamReader(getInputStream(new URL(API_URL + "channels/" + channel)))) {
            return GSON.fromJson(in, KickChannel.class);
        }
    }

    public KickVideo getVideoInfo(String videoId) throws IOException {
        try (InputStreamReader in = new InputStreamReader(getInputStream(new URL(API_URL + "video/" + videoId)))) {
            return GSON.fromJson(in, KickVideo.class);
        }
    }

    public InputStream getInputStream(URL url) throws IOException {
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
