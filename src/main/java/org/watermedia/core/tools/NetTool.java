package org.watermedia.core.tools;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;

public class NetTool {
    public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/134.0.0.0 Safari/537.36 Edg/134.0.0.0";

    public static URLConnection connectToAny(URI uri, String method) throws IOException {
        return connectToAny(uri.toURL(), method);
    }

    public static URLConnection connectToAny(URL url, String method) throws IOException {
        URLConnection conn = url.openConnection();
        conn.setRequestProperty("User-Agent", USER_AGENT);
        if (conn instanceof HttpURLConnection) {
            HttpURLConnection connn = (HttpURLConnection) conn;
            connn.setRequestMethod(method);
        }
        return conn;
    }

    public static HttpURLConnection connectToHTTP(URL url, String method) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod(method);
        conn.setRequestProperty("User-Agent", USER_AGENT);
        conn.setRequestProperty("Cache-Control", "no-cache");
        return conn;
    }

    public static HttpURLConnection connectToHTTP(URI uri, String method) throws IOException {
        return connectToHTTP(uri.toURL(), method);
    }

    public static HttpURLConnection connectToHTTP(String url, String method) throws IOException {
        return connectToHTTP(new URL(url), method);
    }
}