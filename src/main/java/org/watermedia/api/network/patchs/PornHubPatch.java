package org.watermedia.api.network.patchs;

import org.watermedia.WaterMedia;
import org.watermedia.core.tools.DataTool;
import org.watermedia.core.tools.NetTool;

import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PornHubPatch extends AbstractPatch {
    public static final Pattern PATTERN = Pattern.compile("\\bvar\\s+flashvars_\\d+\\s*=\\s*(\\{[\\s\\S]*?\\});");

    @Override
    public String platform() {
        return "Pornhub";
    }

    @Override
    public boolean isValid(URI uri) {
        String host = uri.getHost();
        String path  = uri.getPath();
        return host != null && host.endsWith("pornhub.com") && path != null && path.startsWith("/view_video.php");
    }

    @Override
    public Result patch(URI uri, Quality prefQuality) throws FixingURLException {
        super.patch(uri, prefQuality);
        try {
            HttpURLConnection conn = NetTool.connectToHTTP(uri, "GET");

            int code = conn.getResponseCode();
            switch (code) {
                case HttpURLConnection.HTTP_INTERNAL_ERROR: throw new Exception("Pornhub died");
                case HttpURLConnection.HTTP_NOT_FOUND: throw new NullPointerException("Porn video not found");
                case HttpURLConnection.HTTP_FORBIDDEN:
                case HttpURLConnection.HTTP_UNAUTHORIZED:
                    throw new UnsupportedOperationException("Pornhub blocked us API access - URL: " + uri);
                default:
                    if (code != HttpURLConnection.HTTP_OK)
                        throw new UnsupportedOperationException("Unexpected response from Pornhub (" + code + ") - URL: " + uri);
            }

            String html = new String(DataTool.readAllBytes(conn.getInputStream()), StandardCharsets.UTF_8);
            Matcher matcher = PATTERN.matcher(html);

            if (!matcher.find()) throw new UnsupportedOperationException("No flashvars found in the page");
            String result = matcher.group();

            result = result.substring(result.indexOf('{'), result.length() - 1);

            FlashVars flashVars = DataTool.fromJSON(result, FlashVars.class);

            String url = flashVars.mediaDefinitions[0].videoUrl;

            for (FlashVars.MediaDefinition mediaDefinition: flashVars.mediaDefinitions) {
                if (mediaDefinition.defaultQuality) {
                    url = mediaDefinition.videoUrl;
                }
            }

            return new Result(new URI(url), true, false);
        } catch (Exception e) {
            throw new FixingURLException(uri.toString(), e);
        }
    }

    public static class FlashVars {
        public int isVR;
        public String experimentId;
        public Object searchEngineData;
        public int maxInitialBufferLength;
        public int disable_sharebar;
        public String htmlPauseRoll;
        public String htmlPostRoll;
        public String autoplay;
        public String autoreplay;
        public String video_unavailable;
        public String pauseroll_url;
        public String postroll_url;
        public String toprated_url;
        public String mostviewed_url;
        public String language;
        public String isp;
        public String geo;
        public String customLogo;
        public boolean trackingTimeWatched;
        public String tubesCmsPrerollConfigType;
        public AdRollConfig[] adRollGlobalConfig;
        public String embedCode;
        public boolean hidePostPauseRoll;
        public String isHD;
        public int video_duration;
        public String actionTags;
        public String link_url;
        public String related_url;
        public String video_title;
        public String image_url;
        public int[] defaultQuality;
        public String vcServerUrl;
        public String service;
        public String mediaPriority;
        public MediaDefinition[] mediaDefinitions;
        public String isVertical;
        public String video_unavailable_country;
        public String mp4_seek;
        public int[] hotspots;
        public Thumbs thumbs;
        public String options;
        public String cdn;
        public int startLagThreshold;
        public int outBufferLagThreshold;
        public String appId;
        public String cdnProvider;
        public NextVideo nextVideo;
        public PlaybackTracking playbackTracking;
        public boolean chromecast;
        public boolean autoFullscreen;

        public static class AdRollConfig {
            public int[] delay;
            public int forgetUserAfter;
            public int onNth;
            public int skipDelay;
            public boolean skippable;
            public boolean vastSkipDelay;
            public String json;
            public String user_accept_language;
            public int startPoint;
            public int maxVideoTimeout;
        }

        public static class MediaDefinition {
            public int group;
            public int height;
            public int width;
            public boolean defaultQuality;
            public String format;
            public String videoUrl;
            public Object quality;
            public boolean remote;
        }

        public static class Thumbs {
            public int samplingFrequency;
            public String type;
            public String cdnType;
            public String urlPattern;
            public String thumbHeight;
            public String thumbWidth;
        }

        public static class NextVideo {
            public String thumb;
            public int duration;
            public String title;
            public int isHD;
            public String nextUrl;
            public String vkey;
            public boolean isJoinPageEntry;
            public Object channelTitle;
            public String views;
            public String viewsText;
            public int rating;
            public String uploaderLink;
            public String badge;
        }

        public static class PlaybackTracking {
            public int app_id;
            public String munged_session_id;
            public int video_id;
            public int video_duration;
            public int video_timestamp;
            public String eventName;
            public String hostname;
            public String watch_session;
            public int sample_size;
        }
    }
}