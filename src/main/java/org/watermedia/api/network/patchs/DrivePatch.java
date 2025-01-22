package org.watermedia.api.network.patchs;

import org.watermedia.WaterMedia;
import org.watermedia.core.tools.DataTool;
import org.watermedia.core.tools.NetTool;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DrivePatch extends AbstractPatch {
    // API-STRATEGY
    private static final String DOWNLOAD_URL = "https://drive.usercontent.google.com/download?id=%s&export=download&authuser=0&acknowledgeAbuse=true";
    private static final String API_URL = "https://www.googleapis.com/drive/v3/files/%s?alt=media&acknowledgeAbuse=true&key=%s";
    private static final String LUCK = new String(Base64.getDecoder().decode(new String(Base64.getDecoder().decode("UVVsNllWTjVRVTVpVFhkWGQyWnhiSE5QWWtoVlEwODFjbUZ6TjBGcmNXaE5UWEphUVZWWg=="), StandardCharsets.UTF_8)), StandardCharsets.UTF_8); // JUST TO AVOID SCRAPPER BOTS
    // REGEX
    private static final Pattern HTML_PATTERN = Pattern.compile("<input[^>]*name=\"([^\"]+)\"[^>]*value=\"([^\"]+)\"", Pattern.DOTALL | Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);


    @Override
    public String platform() {
        return "Google Drive";
    }

    @Override
    public boolean isValid(URI uri) {
        String host = uri.getHost();
        return host != null && host.equals("drive.google.com") && uri.getPath().startsWith("/file/d/");
    }

    @Override
    public Result patch(URI uri, Quality preferQuality) throws FixingURLException {
        super.patch(uri, preferQuality);

        try {
            // PATH GETTER
            final int start = uri.getPath().indexOf("/file/d/") + 8;
            int end = uri.getPath().indexOf('/', start);
            if (end == -1) end = uri.getPath().length();
            String fileID = uri.getPath().substring(start, end);

            return new Result(new URI(String.format(API_URL, fileID, LUCK)), false, false, this::fallback);
        } catch (Exception e) {
            throw new FixingURLException(uri, e);
        }
    }

    private Result fallback(URI uri) throws FixingURLException {
        try {
            String fileId = getFileID(uri.getPath());
            String url = String.format(DOWNLOAD_URL, fileId);

            // FIRST CHECK IF FILE CAN BE DOWNLOADED (WHEN NOT THIS THROWS AN EXCEPTION)
            HttpURLConnection conn = NetTool.connectToHTTP(url, "GET");
            int code = conn.getResponseCode();

            switch (code) {
                case HttpURLConnection.HTTP_NOT_FOUND: throw new NullPointerException("File doesn't exists or isn't available anymore");
                case HttpURLConnection.HTTP_FORBIDDEN:
                case HttpURLConnection.HTTP_UNAUTHORIZED: throw new IllegalAccessException("File has not public accesibility");
                default:
                    if (code != HttpURLConnection.HTTP_OK)
                        throw new UnsupportedOperationException("Google responses with a unexpected status code: " + code);
            }

            if (conn.getContentType().startsWith("text/html")) {
                try (InputStream in = conn.getInputStream()) {
                    String html = new String(DataTool.readAllBytes(in), StandardCharsets.UTF_8);
                    Matcher matcher = HTML_PATTERN.matcher(html);

                    final Map<String, String> form = new HashMap<>();
                    while (matcher.find()) {
                        form.put(matcher.group(1), matcher.group(2));
                    }

                    if (!form.containsKey("uuid"))
                        throw new IllegalAccessException("File");

                    url += "&uuid="+form.get("uuid") + "&at="+form.get("at") + "&confirm=t";
                }
            }

            conn.disconnect();
            return new Result(new URI(url), conn.getContentType().startsWith("video"), false);
        } catch (Exception e) {
            throw new FixingURLException(uri, e);
        }
    }

    private String getFileID(String path) {
        int start = path.indexOf("/file/d/");
        if (start == -1) throw new IllegalArgumentException("URL doesn't mach well-know paths");
        start += 8;

        int end = path.indexOf('/', start);
        if (end == -1) end = path.length();
        return path.substring(start, end);
    }
}