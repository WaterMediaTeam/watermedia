package org.watermedia.api.network.patchs;

import com.google.gson.Gson;
import org.watermedia.WaterMedia;
import org.watermedia.api.network.patchs.kick.KickChannel;
import org.watermedia.api.network.patchs.kick.KickVideo;
import org.watermedia.core.tools.DataTool;
import org.watermedia.core.tools.NetTool;

import java.io.*;
import java.net.*;

public class KickPatch extends AbstractPatch {
    private static final String API_URL = "https://kick.com/api/v2/";
    private static final Gson GSON = new Gson();

    @Override
    public String platform() {
        return "Kick";
    }

    @Override
    public boolean isValid(URI uri) {
        String host = uri.getHost();
        return host != null && (host.endsWith(".kick.com") || host.equals("kick.com"));
    }

    @Override
    public Result patch(URI uri, Quality prefQuality) throws FixingURLException {
        super.patch(uri, prefQuality);

        try {
            if (uri.getPath().contains("/videos/")) {
                String[] split = uri.getPath().split("/");
                String videoID = split[split.length - 1];
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
        HttpURLConnection conn = NetTool.connectToHTTP(url, "GET");
        conn.setRequestProperty("User-Agent", WaterMedia.USER_AGENT);
        conn.setRequestProperty("Accept", "application/json");
        try {
            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new ConnectException(String.format("Server url %s response with status code (%s): %s", url, conn.getResponseCode(), conn.getResponseMessage()));
            }
            return new ByteArrayInputStream(DataTool.readAllBytes(conn.getInputStream()));
        } finally {
            conn.disconnect();
        }
    }
}
