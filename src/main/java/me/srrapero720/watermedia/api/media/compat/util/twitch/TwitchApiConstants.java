package me.srrapero720.watermedia.api.media.compat.util.twitch;

import java.util.regex.Pattern;

public class TwitchApiConstants {
    public static final String GRAPH_QL_URL = "https://gql.twitch.tv/gql";
    public static final String TTV_API_URL_TEMPLATE = "https://api.ttv.lol/playlist/%s.m3u8";
    public static final Pattern QUALITY_PATTERN = Pattern.compile("#EXT-X-STREAM-INF:BANDWIDTH=(\\d+),RESOLUTION=(\\d+x\\d+),CODECS=\"([^\"]+)\",VIDEO=\"([^\"]+)\".*");
    public static final Pattern URL_PATTERN = Pattern.compile("https?://.*");
    public static final String CLIENT_ID = "kimne78kx3ncx6brgo4mv6wki5h1ko";

}
