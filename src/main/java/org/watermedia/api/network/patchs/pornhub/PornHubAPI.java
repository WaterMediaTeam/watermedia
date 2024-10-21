package org.watermedia.api.network.patchs.pornhub;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PornHubAPI {

    public static List<VideoQuality> getVideo(String url) throws Exception {
        String source = fetchSource(url);
        return processSourceString(source);
    }

    private static List<VideoQuality> processSourceString(String source) {
        List<VideoQuality> videoQualities = new ArrayList<>();
        List<String> matches = new ArrayList<>();
        Pattern pattern = Pattern.compile("(?<=\\*\\/)\\w+");
        Matcher matcher = pattern.matcher(source);
        while (matcher.find()) {
            matches.add(matcher.group());
        }

        List<String> urls = new ArrayList<>();
        for (String match : matches) {
            String regexString = "(?<=" + match + "=\")[^;]+(?=\")";
            Pattern regexPattern = Pattern.compile(regexString);
            Matcher regexMatcher = regexPattern.matcher(source);
            if (regexMatcher.find()) {
                String value = regexMatcher.group().replaceAll("[\" +]", "");

                if (value.startsWith("https")) {
                    if (urls.size() == 4) {
                        break;
                    }
                    urls.add(value);
                } else {
                    urls.set(urls.size() - 1, urls.get(urls.size() - 1) + value);
                }
            }
        }

        for (String x : urls) {
            Pattern resolutionPattern = Pattern.compile("(?<=_|/)\\d*P(?=_)");
            Matcher resolutionMatcher = resolutionPattern.matcher(x);
            if (resolutionMatcher.find()) {
                String resolution = resolutionMatcher.group();
                videoQualities.add(new VideoQuality(resolution, x));
            }
        }

        return videoQualities;
    }

    private static String readResponse(HttpURLConnection conn) throws Exception {
        if (conn.getResponseCode() != 200) {
            throw new Exception("HTTP error code: " + conn.getResponseCode());
        }

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine;
        StringBuffer content = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();
        conn.disconnect();

        return content.toString();
    }

    private static String fetchSource(String url) throws Exception {
        URL urlObj = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) urlObj.openConnection();
        System.out.println(url);
        conn.setRequestMethod("GET");
        return readResponse(conn);
    }
}