package org.watermedia.api.network.patchs;

import com.google.gson.Gson;
import org.watermedia.api.network.patchs.streamable.SAVideo;
import org.watermedia.core.tools.NetTool;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;

public class StreamablePatch extends AbstractPatch {
    private static final String API_URL = "https://api.streamable.com/videos/";
    private static final Gson GSON = new Gson();

    @Override
    public String platform() {
        return "Streamable";
    }

    @Override
    public boolean isValid(URI uri) {
        String host = uri.getHost();
        return host != null && host.equals("streamable.com");
    }

    @Override
    public Result patch(URI uri, Quality prefQuality) throws FixingURLException {
        String videoId = uri.getPath().substring(1);

        try {
            HttpURLConnection connection = NetTool.connectToHTTP(new URI(API_URL + videoId), "GET");
            if (connection.getResponseCode() == 404) throw new NullPointerException("Video doesn't exists");

            try (InputStreamReader is = new InputStreamReader(connection.getInputStream())) {
                SAVideo video = GSON.fromJson(is, SAVideo.class);
                return new Result(new URI(video.files.mp4.url), true, false);
            } finally {
                connection.disconnect();
            }
        } catch (Exception e) {
            throw new FixingURLException(uri, e);
        }
    }
}
