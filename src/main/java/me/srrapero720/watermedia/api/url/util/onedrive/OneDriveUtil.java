package me.srrapero720.watermedia.api.url.util.onedrive;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;

public class OneDriveUtil {
    private static final String API_URL = "https://api.onedrive.com/v1.0/";

    public static OneDriveItem getDownloableItem(String sharedUrl) throws IOException {
        String url = formatURL(sharedUrl);
        return getItem(url);
    }

    private static String formatURL(String sharedUrl) {
        String base64 = Base64.getUrlEncoder().withoutPadding().encodeToString(sharedUrl.getBytes());
        String encodedUrl = "u!" + base64;

        return API_URL + "shares/" + encodedUrl + "/driveItem";
    }

    private static OneDriveItem getItem(String url) throws IOException {
        URL itemURL = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) itemURL.openConnection();

        if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
            throw new IOException("Failed to get item: " + connection.getResponseMessage());
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        Gson gson = new Gson();

        return gson.fromJson(reader, OneDriveItem.class);
    }
}