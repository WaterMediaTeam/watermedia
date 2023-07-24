package me.srrapero720.watermedia.api.url.patch.util.twitch;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.srrapero720.watermedia.api.url.patch.util.StreamQuality;
import me.srrapero720.watermedia.util.StreamUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static me.srrapero720.watermedia.util.ResourceUtil.USER_AGENT;

public class TwitchUtil {
    private static final Gson gson = new Gson();

    public static List<StreamQuality> getStream(String stream) throws IOException, StreamNotFound {
        String apiUrl = buildApiUrl(stream, false);
        return StreamQuality.parse(performGetRequest(apiUrl));
    }

    public static List<StreamQuality> getVod(String video) throws IOException, StreamNotFound {
        String apiUrl = buildApiUrl(video, true);
        return StreamQuality.parse(performGetRequest(apiUrl));
    }

    private static String buildApiUrl(String id, boolean isVOD) throws IOException {
        JsonElement response = post(id, isVOD);
        JsonObject accessTokenData = response
                .getAsJsonObject().get("data")
                .getAsJsonObject().get(isVOD ? "videoPlaybackAccessToken" : "streamPlaybackAccessToken")
                .getAsJsonObject();

        String signature = accessTokenData.get("signature").getAsString();
        String value = accessTokenData.get("value").getAsString();

        String url = String.format(isVOD ? TwitchApiConstants.TTV_PLAYLIST_API_URL_TEMPLATE : TwitchApiConstants.TTV_LIVE_API_URL_TEMPLATE, id);
        return url + buildUrlParameters(signature, value);
    }

    private static String buildUrlParameters(String signature, String value) {
        value = URLEncoder.encode(value, StandardCharsets.UTF_8);
        return String.format("%%3Facmb=e30%%3D&allow_source=true&fast_bread=true&player_backend=mediaplayer&playlist_include_framerate=true&reassignments_supported=true&supported_codecs=vp09,avc1&transcode_mode=cbr_v1&cdm=wv&player_version=1.20.0&sig=%s&token=%s", signature, value);
    }

    private static String performGetRequest(String apiUrl) throws IOException, StreamNotFound {
        HttpURLConnection conn = initializeConnection(apiUrl, "GET");
        conn.setRequestProperty("x-donate-to", "https://ttv.lol/donate");

        int responseCode = conn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_NOT_FOUND) throw new StreamNotFound("Stream not found");
        return (responseCode == HttpURLConnection.HTTP_OK) ?
                new String(StreamUtil.readAllBytes(conn.getInputStream())) :
                new String(StreamUtil.readAllBytes(conn.getErrorStream()));
    }

    private static JsonElement post(String id, boolean isVOD) throws IOException {
        HttpURLConnection conn = initializeConnection(TwitchApiConstants.GRAPH_QL_URL, "POST");
        conn.setDoOutput(true);
        conn.setRequestProperty("Client-ID", TwitchApiConstants.CLIENT_ID);
        conn.setRequestProperty("User-Agent", USER_AGENT);
        conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");

        try (OutputStream os = conn.getOutputStream()) {
            os.write(buildJsonString(id, isVOD).getBytes(StandardCharsets.UTF_8));
        }

        return new JsonParser().parse(new String(StreamUtil.readAllBytes(conn.getInputStream())));
    }

    /**
     * Initializes a connection to the given URL with the given request method.
     * @param url The URL to connect to.
     * @param requestMethod The request method to use.
     * @return The initialized connection.
     * @throws IOException If an I/O error occurs.
     */
    private static HttpURLConnection initializeConnection(String url, String requestMethod) throws IOException {
        URL apiUrl = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) apiUrl.openConnection();
        conn.setRequestMethod(requestMethod);
        return conn;
    }

    /**
     * Builds the JSON string to send to the Twitch API.
     * @param id The id of the video stream to get.
     * @param isVOD Whether the video is a VOD or not.
     * @return The built JSON string.
     */
    private static String buildJsonString(String id, boolean isVOD) {
        // Variables mapping
        Map<String, Object> variables = new HashMap<>();
        variables.put("isLive", !isVOD);
        variables.put("isVod", isVOD);
        variables.put("login", !isVOD ? id : "");
        variables.put("vodID", isVOD ? id : "");
        variables.put("playerType", "site");

        // Main JSON mapping
        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("operationName", "PlaybackAccessToken_Template");
        jsonMap.put("query", "query PlaybackAccessToken_Template($login: String!, $isLive: Boolean!, $vodID: ID!, $isVod: Boolean!, $playerType: String!) {  streamPlaybackAccessToken(channelName: $login, params: {platform: \"web\", playerBackend: \"mediaplayer\", playerType: $playerType}) @include(if: $isLive) {    value    signature    __typename  }  videoPlaybackAccessToken(id: $vodID, params: {platform: \"web\", playerBackend: \"mediaplayer\", playerType: $playerType}) @include(if: $isVod) {    value    signature    __typename  }}");
        jsonMap.put("variables", variables);

        return gson.toJson(jsonMap);
    }
}
