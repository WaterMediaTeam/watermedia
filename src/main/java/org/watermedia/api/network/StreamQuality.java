package org.watermedia.api.network;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class StreamQuality implements Comparable<StreamQuality> {
    private static final Pattern M3U8_STREAM_INF_RE = Pattern.compile("^#EXT-X-STREAM-INF:(.*)");
    private static final Pattern M3U8_INF_VALUE_RE = Pattern.compile("([A-Z-]+)=(?:([^,]+)|\"([^\"]+?)\")");
    private static final Pattern HTTP_URL_RE = Pattern.compile("https?://.*");

    private int bandwidth;
    private int width;
    private int height;
    private int framerate;
    private String codecs;
    private String url;

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setFramerate(int framerate) {
        this.framerate = framerate;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setBandwidth(int bandwidth) {
        this.bandwidth = bandwidth;
    }

    public void setCodecs(String codecs) {
        this.codecs = codecs;
    }

    public void setResolution(String resolution) {
        String[] res = resolution.split("x");
        this.width = Integer.parseInt(res[0]);
        this.height = Integer.parseInt(res[1]);
    }

    public String getCodecs() {
        return codecs;
    }

    public int getBandwidth() {
        return bandwidth;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getPixelDensity() {
        return width * height;
    }

    public int getFramerate() {
        return framerate;
    }

    public String getUrl() {
        return url;
    }

    public static List<StreamQuality> parse(String playlistData) {
        List<StreamQuality> result = new ArrayList<>();
        if (playlistData == null || playlistData.isEmpty()) return result;

        String[] lines = playlistData.split("\n");
        StreamQuality currentQuality = null;

        for (String line: lines) {
            Matcher matcher = M3U8_STREAM_INF_RE.matcher(line);
            if (matcher.matches()) {
                String streamInformation = matcher.group(1);
                currentQuality = new StreamQuality();

                Matcher valueMatcher = M3U8_INF_VALUE_RE.matcher(streamInformation);
                while (valueMatcher.find()) {
                    String key = valueMatcher.group(1);
                    String value = valueMatcher.group(2);
                    if (value == null) value = valueMatcher.group(3);

                    // Note: using `parseFloat` to have a more lax parser which does not panic on "60.000".
                    // Twitch sends framerate using this notation which causes parseInt to throw.
                    switch (key) {
                        case "BANDWIDTH" -> currentQuality.setBandwidth((int) Float.parseFloat(value));
                        case "RESOLUTION" -> currentQuality.setResolution(value);
                        case "CODECS" -> currentQuality.setCodecs(value);
                        case "FRAME-RATE" -> currentQuality.setFramerate((int) Float.parseFloat(value));
                    }
                }
            } else if (HTTP_URL_RE.matcher(line).matches()) {
                if (currentQuality != null) {
                    currentQuality.setUrl(line);
                    result.add(currentQuality);
                    currentQuality = null;
                }
            }
        }

        // Sort them (in reverse) based on their pixel density and framerate
        result.sort((q1, q2) -> {
            int den1 = q1.getPixelDensity();
            int den2 = q2.getPixelDensity();

            int diff = den1 - den2;

            if (diff == 0) diff = q2.framerate - q1.framerate;
            return diff;
        });

        return result;
    }

    @Override
    public int compareTo(StreamQuality streamQuality) {
        int res = streamQuality.width - width;
        if (res == 0) res = streamQuality.framerate - framerate;
        return res;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StreamQuality that)) return false;
        return bandwidth == that.bandwidth && width == that.width && height == that.height && framerate == that.framerate && Objects.equals(codecs, that.codecs) && Objects.equals(url, that.url);
    }

    @Override
    public String toString() {
        return "StreamQuality{" +
                "bandwidth=" + bandwidth +
                ", width=" + width +
                ", height=" + height +
                ", framerate=" + framerate +
                ", codecs='" + codecs + '\'' +
                ", url='" + url + '\'' +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(bandwidth, width, height, framerate, codecs, url);
    }
}
