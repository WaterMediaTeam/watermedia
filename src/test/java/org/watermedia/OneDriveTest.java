package org.watermedia;

import com.google.gson.Gson;
import org.watermedia.core.tools.DataTool;
import org.watermedia.core.tools.NetTool;

import java.io.*;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;

public class OneDriveTest {
    static final String TEST_URL = "https://1drv.ms/v/c/712b4802fdf5ce81/EYHO9f0CSCsggHGpIgAAAAAB_MwM8TJR20iTcztk-DvmiQ?e=lafZ9h";

    static final String TOKEN_API = "https://api-badgerp.svc.ms/v1.0/token";
    static final String VIDEO_CONTENT_API = "https://my.microsoftpersonalcontent.com/_api/v2.1/drives/712B4802FDF5CE81/items/712B4802FDF5CE81!8873/content?format=dash&pretranscode=0&transcodeahead=0&part=index&ccat=2&psi=2d91ba10-5c31-4f3e-a200-1630a37fffdd";

    static {
        CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));
    }

    public static String encodedURL() {
        String base64 = Base64.getUrlEncoder().withoutPadding().encodeToString(TEST_URL.getBytes());
        String encodedUrl = "u!" + base64;
        return encodedUrl;
    }

    public static void main(String[] args) {
        AuthToken token = null;
        try (InputStream in = new FileInputStream(new File("ms-token.json"))) {
            token = DataTool.fromJSON(new InputStreamReader(in, StandardCharsets.UTF_8), AuthToken.class);
        } catch (Exception e) {
            WaterMedia.LOGGER.error("ERROR:", e);
        }

        if (token == null || token.expiryTimeUtc.before(new Date())) {
            try {
                if (token != null && token.expiryTimeUtc.before(new Date())) {
                    WaterMedia.LOGGER.info("Token expired");
                }

                byte[] data = "{appId: \"073204aa-c1e0-4e66-a200-e5815a0aa93d\"}".getBytes(StandardCharsets.UTF_8);
                HttpURLConnection conn = NetTool.connectToHTTP(TOKEN_API, "POST");
                conn.setRequestProperty("content-type", "application/json;odata=verbose");
                conn.setRequestProperty("content-length", String.valueOf(data.length));
                conn.setRequestProperty("accept", "application/json;odata=verbose");
                conn.setRequestProperty("Origin", "https://photos.onedrive.com");
                conn.setRequestProperty("Referer", "https://photos.onedrive.com/");
                conn.setRequestProperty("Accept-Encoding", "gzip, deflate, br");
//                conn.setDoOutput(true);
//                conn.getOutputStream().write(data);

                WaterMedia.LOGGER.info("Status {}", conn.getResponseCode());

                if (conn.getResponseCode() != 200) {
                    WaterMedia.LOGGER.error("ERROR: {}", new String(DataTool.readAllBytes(conn.getErrorStream()), StandardCharsets.UTF_8));
                    return;
                }

                String json = new String(DataTool.readAllBytes(conn.getInputStream()), StandardCharsets.UTF_8);

                token = DataTool.fromJSON(json, AuthToken.class);
                WaterMedia.LOGGER.info("Schema {} - Expiry {} - Token {}", token.authScheme, token.expiryTimeUtc, token.token);

                try (OutputStream out = new FileOutputStream(new File("ms-token.json"))) {
                    out.write(json.getBytes(StandardCharsets.UTF_8));
                } catch (Exception e) {
                    WaterMedia.LOGGER.error("ERROR:", e);
                }

            } catch (Exception e) {
                WaterMedia.LOGGER.error("ERROR:", e);
            }
        }

        if (token == null) {
            WaterMedia.LOGGER.error("Token is null");
            return;
        }


        // NOW HERE WE DOWNLOAD THE CONTENT
        // FIXME: On browser it works, here it doesn't, returns a 400 error writting 0 bytes or 411 error with no data and no content-length
        //  IF YOU KNOW HOW TO FIX THIS, PLEASE LET ME KNOW
        try {

            HttpURLConnection conn = NetTool.connectToHTTP("https://my.microsoftpersonalcontent.com/_api/v2.0/shares/" + encodedURL() + "/driveitem?$select=id,parentReference", "POST");
            conn.setRequestProperty("Authorization", token.toString());
            conn.setRequestProperty("content-type", "text/plain;charset=UTF-8");
            conn.setRequestProperty("content-length", "0");
            conn.setRequestProperty("dnt", "1");
            conn.setRequestProperty("Origin", "https://photos.onedrive.com");
            conn.setRequestProperty("Referer", "https://photos.onedrive.com/");
            conn.setRequestProperty("Accept-Encoding", "gzip, deflate, br");
            conn.setRequestProperty("Accept", "application/json;odata=verbose");
            conn.setRequestProperty("Pragma", "no-cache");
            conn.setRequestProperty("Prefer", "autoredeem");
            conn.setRequestProperty("priority", "u=1, i");
            conn.setRequestProperty("sec-ch-ua", "\"Chromium\";v=\"134\", \"Not:A-Brand\";v=\"24\", \"Microsoft Edge\";v=\"134\"");
            conn.setRequestProperty("sec-ch-ua-mobile", "?0");
            conn.setRequestProperty("sec-ch-ua-platform", "\"Windows\"");
            conn.setRequestProperty("lang", "en-US");
            conn.setDoOutput(true);
            conn.getOutputStream().write("".getBytes(StandardCharsets.UTF_8));

            WaterMedia.LOGGER.info("URI: {}", conn.getURL());

            if (conn.getResponseCode() != 200) {
                WaterMedia.LOGGER.error("ERROR: {}", new String(DataTool.readAllBytes(conn.getErrorStream()), StandardCharsets.UTF_8));
                return;
            }

            String res = new String(DataTool.readAllBytes(conn.getInputStream()), StandardCharsets.UTF_8);
            WaterMedia.LOGGER.info("Response: {}", res);
        } catch (Exception e) {
            WaterMedia.LOGGER.error("ERROR:", e);
        }
    }

    public static class AuthToken {
        public String authScheme;
        public Date expiryTimeUtc;
        public String token;

        @Override
        public String toString() {
            return authScheme + " " + token;
        }

        public String toJsonString() {
            return new Gson().toJson(this);
        }
    }
}
