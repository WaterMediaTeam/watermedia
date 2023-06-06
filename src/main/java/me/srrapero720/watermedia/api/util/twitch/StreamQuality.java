package me.srrapero720.watermedia.api.util.twitch;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

public class StreamQuality {
    private int bandwidth;
    private String resolution;
    private String codecs;
    private String video;
    private String url;

    public void setResolution(String resolution) {
        this.resolution = resolution;
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

    public void setVideo(String video) {
        this.video = video;
    }

    public String getCodecs() {
        return codecs;
    }

    public int getBandwidth() {
        return bandwidth;
    }

    public String getResolution() {
        return resolution;
    }

    public String getUrl() {
        return url;
    }

    public String getVideo() {
        return video;
    }

    @Override
    public String toString() {
        return String.format("Bandwidth: %d, Resolution: %s, Codecs: %s, Video: %s, URL: %s",
                bandwidth, resolution, codecs, video, url);
    }

    public static List<StreamQuality> parse(String streamResponse) {
        String[] lines = streamResponse.split("\n");
        List<StreamQuality> streamQualities = new ArrayList<>();
        StreamQuality currentQuality = null;

        for (String line : lines) {
            Matcher matcher = TwitchApiConstants.QUALITY_PATTERN.matcher(line);
            if (matcher.matches()) {
                currentQuality = new StreamQuality();
                currentQuality.setBandwidth(Integer.parseInt(matcher.group(1)));
                currentQuality.setResolution(matcher.group(2));
                currentQuality.setCodecs(matcher.group(3));
                currentQuality.setVideo(matcher.group(4));
            } else if (TwitchApiConstants.URL_PATTERN.matcher(line).matches()) {
                if (currentQuality != null) {
                    currentQuality.setUrl(line);
                    streamQualities.add(currentQuality);
                    currentQuality = null;
                }
            }
        }

        return streamQualities;
    }
}