package me.srrapero720.watermedia.tools;

import me.srrapero720.watermedia.WaterMedia;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

public class NetTool {

    public static HttpURLConnection connect(URL url, String method) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod(method);
        conn.setRequestProperty("User-Agent", WaterMedia.USER_AGENT);
        return conn;
    }

    public static HttpURLConnection connect(URI uri, String method) throws IOException {
        return connect(uri.toURL(), method);
    }

    public static HttpURLConnection connect(String url, String method) throws IOException {
        return connect(new URL(url), method);
    }
}
