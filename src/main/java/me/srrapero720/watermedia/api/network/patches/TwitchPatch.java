package me.srrapero720.watermedia.api.network.patches;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.srrapero720.watermedia.api.MediaContext;
import me.srrapero720.watermedia.api.network.MediaURI;
import me.srrapero720.watermedia.api.network.NetworkAPI;
import me.srrapero720.watermedia.api.network.StreamQuality;
import me.srrapero720.watermedia.api.network.URIPatchException;
import me.srrapero720.watermedia.tools.DataTool;
import me.srrapero720.watermedia.tools.NetTool;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static me.srrapero720.watermedia.WaterMedia.USER_AGENT;

public class TwitchPatch extends AbstractPatch {
    public static final String GRAPH_QL_URL = "https://gql.twitch.tv/gql";
    public static final String TTV_LIVE_API_URL_TEMPLATE = "https://usher.ttvnw.net/api/channel/hls/%s.m3u8";
    public static final String TTV_PLAYLIST_API_URL_TEMPLATE = "https://usher.ttvnw.net/vod/%s.m3u8";
    public static final String CLIENT_ID = "kimne78kx3ncx6brgo4mv6wki5h1ko";

    @Override
    public String platform() {
        return "Twitch";
    }

    @Override
    public boolean validate(MediaURI source) {
        var uri = source.getUri();
        return uri.getHost().contains("www.twitch.tv") || uri.getHost().contains("twitch.tv") || uri.getPath().startsWith("/");
    }

    @Override
    public MediaURI patch(MediaURI source, MediaContext context) throws URIPatchException {
        var url = source.getUri();
        try {
            if (url.getPath().startsWith("/videos/")) {
                String apiUrl = createURL(url.getPath().substring(8), false);
                StreamQuality.parse(get(apiUrl));
            } else {
                String apiUrl = createURL(url.getPath().substring(1), true);
                StreamQuality.parse(get(apiUrl));
            }
        } catch (Exception e) {
            throw new URIPatchException(source, e);
        }


        return null;
    }

    private static String createURL(String id, boolean isVOD) throws IOException {
        JsonElement response = post(id, isVOD);
        JsonObject accessTokenData = response
                .getAsJsonObject().get("data")
                .getAsJsonObject().get(isVOD ? "videoPlaybackAccessToken" : "streamPlaybackAccessToken")
                .getAsJsonObject();

        String signature = accessTokenData.get("signature").getAsString();
        String value = accessTokenData.get("value").getAsString();

        String url = String.format(isVOD ? TTV_PLAYLIST_API_URL_TEMPLATE : TTV_LIVE_API_URL_TEMPLATE, id);

        return url + getQuery(signature, value);
    }

    private static String getQuery(String signature, String value) {
        final Map<String, String> query = new HashMap<>();

        query.put("acmb", "e30%%3D");
        query.put("allow_source", "true");
        query.put("fast_bread", "true");
        query.put("p", "7370379");
        query.put("play_session_id", "21efcd962e7b3fbc891bac088214aa63");
        query.put("player_backend", "mediaplayer");
        query.put("playlist_include_framerate", "true");
        query.put("reassignments_supported", "true");
        query.put("sig", signature);
        query.put("supported_codecs", "avc1");
        query.put("token", value);
        query.put("transcode_mode", "cbr_v1");
        query.put("cdm", "wv");
        query.put("player_version", "1.21.0");

        return NetworkAPI.encodeQuery(query);
    }

    private static String get(String apiUrl) throws IOException {
        HttpURLConnection conn = NetTool.connect(apiUrl, "GET");
        conn.setRequestProperty("x-donate-to", "https://ttv.lol/donate");

        int code = conn.getResponseCode();
        if (code == HttpURLConnection.HTTP_NOT_FOUND) throw new NullPointerException("Stream not found");

        return new String(DataTool.readAllBytes(code == HttpURLConnection.HTTP_OK ? conn.getInputStream() : conn.getErrorStream()));
    }

    private static JsonElement post(String id, boolean isVOD) throws IOException {
        HttpURLConnection conn = NetTool.connect(GRAPH_QL_URL, "POST");
        conn.setDoOutput(true);
        conn.setRequestProperty("Client-ID", CLIENT_ID);
        conn.setRequestProperty("User-Agent", USER_AGENT);
        conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");

        try (OutputStream os = conn.getOutputStream()) {
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
            jsonMap.put("query", "query PlaybackAccessToken_Template($login: String!, $isLive: Boolean!, $vodID: ID!, $isVod: Boolean!, $playerType: String!) {  streamPlaybackAccessToken(channelName: $login, params: {platform: \"web\", playerBackend: \"mediaplayer\", playerType: $playerType}) @include(if: $isLive) {    value    signature   authorization { isForbidden forbiddenReasonCode }   __typename  }  videoPlaybackAccessToken(id: $vodID, params: {platform: \"web\", playerBackend: \"mediaplayer\", playerType: $playerType}) @include(if: $isVod) {    value    signature    __typename  }}");
            jsonMap.put("variables", variables);

            os.write(DataTool.GSON.toJson(jsonMap).getBytes(StandardCharsets.UTF_8));
        }

        return JsonParser.parseString(new String(DataTool.readAllBytes(conn.getInputStream())));
    }
}
