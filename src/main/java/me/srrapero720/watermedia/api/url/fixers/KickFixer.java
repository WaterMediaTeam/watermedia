package me.srrapero720.watermedia.api.url.fixers;

import com.google.gson.Gson;
import me.srrapero720.watermedia.api.network.kick.KickChannel;
import me.srrapero720.watermedia.api.network.kick.KickVideo;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;

public class KickFixer extends URLFixer {
    private static final String API_URL = "https://kick.com/api/v1/";

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
            return new Gson().fromJson(in, KickChannel.class);
        }
    }

    public KickVideo getVideoInfo(String videoId) throws IOException {
        try (InputStreamReader in = new InputStreamReader(getInputStream(new URL(API_URL + "video/" + videoId)))) {
            return new Gson().fromJson(in, KickVideo.class);
        }
    }

    public InputStream getInputStream(URL url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
            String msg = String.format("Server response with status code (%s): %s", connection.getResponseCode(), connection.getResponseMessage());
            connection.setRequestMethod("GET");
            connection.disconnect();
            throw new ConnectException(msg);
        }
        return connection.getInputStream();
    }
}
