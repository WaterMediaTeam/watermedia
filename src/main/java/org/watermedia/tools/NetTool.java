package org.watermedia.tools;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

public class NetTool {
    public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/129.0.0.0 Safari/537.36 Edg/129.0.0.0";

    public static HttpURLConnection connect(URL url, String method) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod(method);
        conn.setRequestProperty("User-Agent", USER_AGENT);
        return conn;
    }

    public static HttpURLConnection connect(URI uri, String method) throws IOException {
        return connect(uri.toURL(), method);
    }

    public static HttpURLConnection connect(String url, String method) throws IOException {
        return connect(new URL(url), method);
    }
}
