package org.watermedia.core.network.patchs;

import com.google.gson.JsonParser;
import com.google.gson.annotations.SerializedName;
import me.srrapero720.watermedia.api.MediaContext;
import org.watermedia.api.media.meta.MediaType;
import org.watermedia.api.media.meta.MediaQuality;
import org.watermedia.api.network.MRL;
import org.watermedia.api.NetworkAPI;
import org.watermedia.core.network.NetworkStream;
import org.watermedia.tools.DataTool;
import org.watermedia.tools.NetTool;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static org.watermedia.tools.NetTool.USER_AGENT;

public class TwitchPatch extends AbstractPatch {
    public static final String API_AUTH_URL = "https://gql.twitch.tv/gql";
    public static final String API_STEAM_LIVE = "https://usher.ttvnw.net/api/channel/hls/%s.m3u8";
    public static final String API_STREAM_VOD = "https://usher.ttvnw.net/vod/%s.m3u8";
    public static final String CLIENT_ID = "kimne78kx3ncx6brgo4mv6wki5h1ko";

    @Override
    public String platform() {
        return "Twitch";
    }

    @Override
    public boolean active(MediaContext context) {
        return true;
    }

    @Override
    public boolean validate(MRL source) {
        final var host = source.getUri().getHost();
        final var path = source.getUri().getPath();
        return (host.contains(".twitch.tv") || host.equals("twitch.tv")) && (path.startsWith("/") && path.length() > 5);
    }

    @Override
    public void patch(MediaContext context, MRL source) throws PatchException {
        var path = source.getUri().getPath().substring(1).split("/");
        var video = path[0].equals("videos") && path.length >= 2;
        var id = video ? path[1] : path[0];

        try {
            final GQLData data = getAccessToken(id, video);
            final String url = String.format(video ? API_STREAM_VOD : API_STEAM_LIVE, id) + getQuery(data.accessToken.signature, data.accessToken.value);

            // TODO: METADATA BUILDING
            MRL.Metadata metadata = null; //new MediaURI.Metadata();

            // PATCH BUILDING
            var patch = new MRL.Patch().setMetadata(metadata);

            for (NetworkStream quality: NetworkStream.parse(getStreamString(url))) {
                var uri = new URI(quality.getUrl());
                var sourceBuilder = patch.addSource();
                sourceBuilder.setType(MediaType.VIDEO);
                sourceBuilder.setIsLive(!video);
                sourceBuilder.putQualityIfAbsent(MediaQuality.calculate(quality.getWidth()), q -> uri);
                // TODO: put offline banner as fallback URI
//                sourceBuilder.setFallbackUri(new URI());
//                sourceBuilder.setFallbackType(MediaType.IMAGE);
                sourceBuilder.build();
            }

            source.apply(patch);
        } catch (Exception e) {
            throw new PatchException(source, e);
        }
    }

    @Override
    public void test(MediaContext context, String url) {

    }

    private static String getStreamString(String apiUrl) throws IOException {
        HttpURLConnection conn = NetTool.connect(apiUrl, "GET");
        conn.setRequestProperty("x-donate-to", "https://ttv.lol/donate");

        int code = conn.getResponseCode();
        if (code == HttpURLConnection.HTTP_NOT_FOUND) throw new NullPointerException("Stream not found");

        return new String(DataTool.readAllBytes(code == HttpURLConnection.HTTP_OK ? conn.getInputStream() : conn.getErrorStream()));
    }

    private static GQLData getAccessToken(String id, boolean isVOD) throws IOException {
        HttpURLConnection conn = NetTool.connect(API_AUTH_URL, "POST");
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

        String dataJson = new String(DataTool.readAllBytes(conn.getInputStream()));
        String json = JsonParser.parseString(dataJson).getAsJsonObject().get("data").getAsJsonObject().toString();

        return DataTool.fromJSON(json, GQLData.class);
    }

    private static String getQuery(String signature, String token) {
        final Map<String, Object> query = new HashMap<>();

        query.put("acmb", "e30%%3D");
        query.put("allow_source", true);
        query.put("fast_bread", true);
        query.put("p", "7370379");
        query.put("play_session_id", "21efcd962e7b3fbc891bac088214aa63");
        query.put("player_backend", "mediaplayer");
        query.put("playlist_include_framerate", true);
        query.put("reassignments_supported", true);
        query.put("sig", signature);
        query.put("supported_codecs", "avc1");
        query.put("token", token);
        query.put("transcode_mode", "cbr_v1");
        query.put("cdm", "wv");
        query.put("player_version", "1.21.0");

        return NetworkAPI.encodeQuery(query);
    }

    public static class GQLData {
        @SerializedName(value = "videoPlaybackAccessToken", alternate = "streamPlaybackAccessToken")
        public Token accessToken;

        public static class Token {
            @SerializedName("signature")
            public String signature;

            @SerializedName("value")
            public String value;
        }
    }
}