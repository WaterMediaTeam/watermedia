package org.watermedia.api.network.patchs.twitch;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.watermedia.core.tools.NetTool;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class TwitchAPI {
    public static final String GRAPH_QL_URL = "https://gql.twitch.tv/gql";
    public static final String TTV_LIVE_API_URL_TEMPLATE = "https://usher.ttvnw.net/api/channel/hls/%s.m3u8";
    public static final String TTV_PLAYLIST_API_URL_TEMPLATE = "https://usher.ttvnw.net/vod/%s.m3u8";
    public static final String CLIENT_ID = "kimne78kx3ncx6brgo4mv6wki5h1ko";

    private static final Gson gson = new Gson();
    private static final int DEFAULT_BUFFER_SIZE = 8192;
    private static final int MAX_BUFFER_SIZE = Integer.MAX_VALUE - 8;

    public static List<StreamQuality> getStream(String stream) throws IOException, StreamNotFound {
        String apiUrl = buildApiUrl(stream, false);
        return StreamQuality.parse(performGetRequest(apiUrl));
    }

    public static List<StreamQuality> getVod(String video) throws IOException, StreamNotFound {
        String apiUrl = buildApiUrl(video, true);
        return StreamQuality.parse(performGetRequest(apiUrl));
    }

    // BUSTED
    private static String buildApiUrl(String id, boolean isVOD) throws IOException {
        JsonElement response = post(id, isVOD);
        JsonObject accessTokenData = response
                .getAsJsonObject().get("data")
                .getAsJsonObject().get(isVOD ? "videoPlaybackAccessToken" : "streamPlaybackAccessToken")
                .getAsJsonObject();

        String signature = accessTokenData.get("signature").getAsString();
        String value = accessTokenData.get("value").getAsString();

        String url = String.format(isVOD ? TTV_PLAYLIST_API_URL_TEMPLATE : TTV_LIVE_API_URL_TEMPLATE, id);
        return url + buildUrlParameters(signature, value);
    }

    private static String buildUrlParameters(String signature, String value) throws UnsupportedEncodingException {
        value = URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
        return String.format("?acmb=e30%%3D&allow_source=true&fast_bread=true&p=7370379&play_session_id=21efcd962e7b3fbc891bac088214aa63&player_backend=mediaplayer&playlist_include_framerate=true&reassignments_supported=true&sig=%s&supported_codecs=avc1&token=%s&transcode_mode=cbr_v1&cdm=wv&player_version=1.21.0", signature, value);
    }

    private static String performGetRequest(String apiUrl) throws IOException, StreamNotFound {
        HttpURLConnection conn = initializeConnection(apiUrl, "GET");
        conn.setRequestProperty("x-donate-to", "https://ttv.lol/donate");

        int responseCode = conn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_NOT_FOUND) throw new StreamNotFound("Stream not found");
        return (responseCode == HttpURLConnection.HTTP_OK) ?
                new String(readAllBytes(conn.getInputStream())) :
                new String(readAllBytes(conn.getErrorStream()));
    }

    private static JsonElement post(String id, boolean isVOD) throws IOException {
        HttpURLConnection conn = NetTool.connectToHTTP(GRAPH_QL_URL, "POST");
        conn.setDoOutput(true);
        conn.setRequestProperty("Client-ID", CLIENT_ID);
        conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");

        try (OutputStream os = conn.getOutputStream()) {
            os.write(buildJsonString(id, isVOD).getBytes(StandardCharsets.UTF_8));
        }

        return new JsonParser().parse(new String(readAllBytes(conn.getInputStream())));
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

    public static byte[] readAllBytes(InputStream inputStream) throws IOException {
        int len = Integer.MAX_VALUE;
        if (len < 0) {
            throw new IllegalArgumentException("len < 0");
        }

        List<byte[]> bufs = null;
        byte[] result = null;
        int total = 0;
        int remaining = len;
        int n;
        do {
            byte[] buf = new byte[Math.min(remaining, DEFAULT_BUFFER_SIZE)];
            int nread = 0;

            // read to EOF which may read more or less than buffer size
            while ((n = inputStream.read(buf, nread,
                    Math.min(buf.length - nread, remaining))) > 0) {
                nread += n;
                remaining -= n;
            }

            if (nread > 0) {
                if (MAX_BUFFER_SIZE - total < nread) {
                    throw new OutOfMemoryError("Required array size too large");
                }
                if (nread < buf.length) {
                    buf = Arrays.copyOfRange(buf, 0, nread);
                }
                total += nread;
                if (result == null) {
                    result = buf;
                } else {
                    if (bufs == null) {
                        bufs = new ArrayList<>();
                        bufs.add(result);
                    }
                    bufs.add(buf);
                }
            }
            // if the last call to read returned -1 or the number of bytes
            // requested have been read then break
        } while (n >= 0 && remaining > 0);

        if (bufs == null) {
            if (result == null) {
                return new byte[0];
            }
            return result.length == total ?
                    result : Arrays.copyOf(result, total);
        }

        result = new byte[total];
        int offset = 0;
        remaining = total;
        for (byte[] b : bufs) {
            int count = Math.min(b.length, remaining);
            System.arraycopy(b, 0, result, offset, count);
            offset += count;
            remaining -= count;
        }

        return result;
    }

    public static class StreamNotFound extends Exception {
        public StreamNotFound(String message) {
            super(message);
        }
    }
}