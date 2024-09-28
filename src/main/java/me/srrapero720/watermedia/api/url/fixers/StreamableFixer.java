package me.srrapero720.watermedia.api.url.fixers;

import com.google.gson.Gson;
import me.srrapero720.watermedia.api.network.streamable.SAVideo;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class StreamableFixer extends URLFixer {
    private static final String API_URL = "https://api.streamable.com/videos/";
    private static final Gson GSON = new Gson();

    @Override
    public String platform() {
        return "Streamable";
    }

    @Override
    public boolean isValid(URL url) {
        return url.getHost().equals("streamable.com");
    }

    @Override
    public Result patch(URL url, Quality prefQuality) throws FixingURLException {
        String videoId = url.getPath().substring(1);

        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(API_URL + videoId).openConnection();
            if (connection.getResponseCode() == 404) throw new NullPointerException("Video doesn't exists");

            try (InputStreamReader is = new InputStreamReader(connection.getInputStream())) {
                SAVideo video = GSON.fromJson(is, SAVideo.class);
                return new Result(new URL(video.files.mp4.url), true, false);
            }

        } catch (Exception e) {
            throw new FixingURLException(url, e);
        }
    }
}
