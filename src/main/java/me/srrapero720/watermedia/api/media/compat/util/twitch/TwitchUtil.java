package me.srrapero720.watermedia.api.media.compat.util.twitch;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TwitchUtil {

    private static final Gson gson = new Gson();

    public static List<StreamQuality> getStream(String stream) throws IOException, StreamNotFound {
        String apiUrl = buildApiUrl(stream);
        return StreamQuality.parse(performGetRequest(apiUrl));
    }

    private static String buildApiUrl(String stream) throws IOException {
        JsonElement response = post(stream);
        String signature = extractFromResponse(response, "signature");
        String value = extractFromResponse(response, "value");
        return String.format(TwitchApiConstants.TTV_API_URL_TEMPLATE, stream)
                + buildUrlParameters(signature, value);
    }

    private static String extractFromResponse(JsonElement response, String key) {
        return response.getAsJsonObject().get("data").getAsJsonObject().get("streamPlaybackAccessToken").getAsJsonObject().get(key).getAsString();
    }

    private static String buildUrlParameters(String signature, String value) {
        value = URLEncoder.encode(value, StandardCharsets.UTF_8);
        return String.format("%%3Fallow_source=true&acmb=e30%%3D&allow_audio_only=true&fast_bread=true&playlist_include_framerate=true&reassignments_supported=true&player_backend=mediaplayer&supported_codecs=vp09,avc1&p=1234567890&play_session_id=1b0c77f72af01d4db1f993803dacd90f&cdm=wm&player_version=1.18.0&player_type=embed&sig=%s&token=%s", signature, value);
    }

    private static String performGetRequest(String apiUrl) throws IOException, StreamNotFound {
        HttpURLConnection conn = initializeConnection(apiUrl, "GET");
        conn.setRequestProperty("x-donate-to", "https://ttv.lol/donate");

        int responseCode = conn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_NOT_FOUND)
            throw new StreamNotFound("Stream not found");
        return (responseCode == HttpURLConnection.HTTP_OK) ?
                new String(conn.getInputStream().readAllBytes()) :
                new String(conn.getErrorStream().readAllBytes());
    }


    private static JsonElement post(String streamer) throws IOException {
        HttpURLConnection conn = initializeConnection(TwitchApiConstants.GRAPH_QL_URL, "POST");
        conn.setDoOutput(true);
        conn.setRequestProperty("Client-ID", TwitchApiConstants.CLIENT_ID);
        conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:86.0) Gecko/20100101 Firefox/86.0");
        conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");

        try (OutputStream os = conn.getOutputStream()) {
            os.write(buildJsonString(streamer).getBytes(StandardCharsets.UTF_8));
        }

        return JsonParser.parseString(new String(conn.getInputStream().readAllBytes()));
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
     * @param streamer The streamer to get the stream from.
     * @return The built JSON string.
     */
    private static String buildJsonString(String streamer) {
        // Variables mapping
        Map<String, Object> variables = new HashMap<>();
        variables.put("isLive", true);
        variables.put("login", streamer);
        variables.put("playerType", "site");
        variables.put("vodID", "");
        variables.put("isVod", false);

        // Main JSON mapping
        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("operationName", "PlaybackAccessToken_Template");
        jsonMap.put("query", "query PlaybackAccessToken_Template($login: String!, $isLive: Boolean!, $vodID: ID!, $isVod: Boolean!, $playerType: String!) {  streamPlaybackAccessToken(channelName: $login, params: {platform: \"web\", playerBackend: \"mediaplayer\", playerType: $playerType}) @include(if: $isLive) {    value    signature    __typename  }  videoPlaybackAccessToken(id: $vodID, params: {platform: \"web\", playerBackend: \"mediaplayer\", playerType: $playerType}) @include(if: $isVod) {    value    signature    __typename  }}");
        jsonMap.put("variables", variables);

        return gson.toJson(jsonMap);
    }
}
