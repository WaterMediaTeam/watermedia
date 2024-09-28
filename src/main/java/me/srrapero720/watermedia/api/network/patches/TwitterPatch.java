package me.srrapero720.watermedia.api.network.patches;

import me.srrapero720.watermedia.WaterMedia;
import me.srrapero720.watermedia.api.MediaModContext;
import me.srrapero720.watermedia.api.network.NetworkAPI;
import me.srrapero720.watermedia.api.network.URIPatchException;
import me.srrapero720.watermedia.api.uri.MediaSource;
import me.srrapero720.watermedia.tools.DataTool;
import me.srrapero720.watermedia.tools.NetTool;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TwitterPatch extends AbstractPatch {
    private static final String API_URL = "https://api.x.com/graphql/sCU6ckfHY0CyJ4HFjPhjtg/TweetResultByRestId";
    private static final Pattern TWEET_ID_PATTERN = Pattern.compile("/[a-zA-Z0-9_]+/status/[0-9]+");
    private static final Pattern CLIENT_ID_PATTERN = Pattern.compile("\"guestId\":\\s*\"(\\d+)\"");

    @Override
    public String platform() {
        return "Twitter (X)";
    }

    @Override
    public boolean validate(MediaSource source) {
        var host = source.getUri().getHost();
        var path = source.getUri().getPath();
        return (host.equals("www.x.com") || host.equals("x.com") || host.equals("www.twitter.com") || host.equals("twitter.com"))
                && TWEET_ID_PATTERN.matcher(path).matches();
    }

    @Override
    public MediaSource patch(MediaSource source, MediaModContext context) throws URIPatchException {



        return null;
    }

    // Scraps thre client id only provided on the HTML scripts
    private static String scrapClientId(URI uri) throws URIPatchException, IOException {
        HttpURLConnection conn = NetTool.connect(uri, "GET");
        conn.setRequestProperty("Accept", "text/html");
        conn.setRequestProperty("Cache-Control", "max-age=0");
        conn.setRequestProperty("User-Agent", WaterMedia.USER_AGENT);

        // Error handling
        int code = conn.getResponseCode();
        switch (code) {
            case HttpURLConnection.HTTP_INTERNAL_ERROR -> throw new URIPatchException(uri, "Twitter died");
            case HttpURLConnection.HTTP_NOT_FOUND -> throw new URIPatchException(uri, "Tweet not found");
            case HttpURLConnection.HTTP_FORBIDDEN, HttpURLConnection.HTTP_UNAUTHORIZED ->
                    throw new UnsupportedOperationException("Twitter blocked us access to this tweet");
            default -> {
                if (code != HttpURLConnection.HTTP_OK)
                    throw new UnsupportedOperationException("Unexpected response from twitter, Status code: " + code);
            }
        }

        try (InputStream in = conn.getInputStream()) {
            String html = new String(DataTool.readAllBytes(in), StandardCharsets.UTF_8);
            Matcher matcher = CLIENT_ID_PATTERN.matcher(html);

            if (matcher.hasMatch()) {
                return matcher.group(1);
            } else {
                throw new URIPatchException(uri.toString(), "Client ID is not present");
            }

        } finally {
            conn.disconnect();
        }
    }


    public String getTweetMedia(String tweetId) throws IOException {



        HttpURLConnection opts = NetTool.connect(API_URL + NetworkAPI.encodeQuery(createOptions(tweetId)), "OPTIONS");
        // Send tweet request options
        if (opts.getResponseCode() != HttpURLConnection.HTTP_OK) throw new NullPointerException("Tweet is not available");

        // finish options
        opts.disconnect();


        HttpURLConnection gett = NetTool.connect(API_URL + NetworkAPI.encodeQuery(createTweetRequest(tweetId)), "GET");

        return null;
    }

    // TODO: convert into a object
    private Map<String, Object> createOptions(String tweetId) {
        Map<String, Object> queryParams = new HashMap<>();

        // Variables
        Map<String, Object> variables = new HashMap<>();
        variables.put("tweetId", tweetId);
        variables.put("withCommunity", false);
        variables.put("includePromotedContent", false);
        variables.put("withVoice", false);

        // Features
        Map<String, Object> features = new HashMap<>();
        features.put("creator_subscriptions_tweet_preview_api_enabled", true);
        features.put("communities_web_enable_tweet_community_results_fetch", true);
        features.put("c9s_tweet_anatomy_moderator_badge_enabled", true);
        features.put("articles_preview_enabled", true);
        features.put("responsive_web_edit_tweet_api_enabled", true);
        features.put("graphql_is_translatable_rweb_tweet_is_translatable_enabled", false);
        features.put("view_counts_everywhere_api_enabled", true);
        features.put("longform_notetweets_consumption_enabled", true);
        features.put("responsive_web_twitter_article_tweet_consumption_enabled", true);
        features.put("tweet_awards_web_tipping_enabled", false);
        features.put("creator_subscriptions_quote_tweet_preview_enabled", false);
        features.put("freedom_of_speech_not_reach_fetch_enabled", true);
        features.put("standardized_nudges_misinfo", true);
        features.put("tweet_with_visibility_results_prefer_gql_limited_actions_policy_enabled", true);
        features.put("rweb_video_timestamps_enabled", true);
        features.put("longform_notetweets_rich_text_read_enabled", true);
        features.put("longform_notetweets_inline_media_enabled", true);
        features.put("rweb_tipjar_consumption_enabled", true);
        features.put("responsive_web_graphql_exclude_directive_enabled", true);
        features.put("verified_phone_label_enabled", false);
        features.put("responsive_web_graphql_skip_user_profile_image_extensions_enabled", false);
        features.put("responsive_web_graphql_timeline_navigation_enabled", true);
        features.put("responsive_web_enhance_cards_enabled", false);

        // Fields
        Map<String, Object> fieldToggles = new HashMap<>();
        fieldToggles.put("withArticleRichContentState", true);
        fieldToggles.put("withArticlePlainText", false);
        fieldToggles.put("withGrokAnalyze", false);
        fieldToggles.put("withDisallowedReplyControls", false);

        // Define them
        queryParams.put("variables", variables);
        queryParams.put("features", features);
        queryParams.put("fieldToggles", fieldToggles);

        return queryParams;
    }

    // TODO: convert into a object
    public HashMap<String, Object> createTweetRequest(String tweetId) {
        HashMap<String, Object> queryMap = new HashMap<>();

        // Variables
        HashMap<String, Object> variables = new HashMap<>();
        variables.put("tweetId", tweetId);
        variables.put("withCommunity", false);
        variables.put("includePromotedContent", false);
        variables.put("withVoice", false);

        // Features
        HashMap<String, Object> features = new HashMap<>();
        features.put("creator_subscriptions_tweet_preview_api_enabled", true);
        features.put("communities_web_enable_tweet_community_results_fetch", true);
        features.put("c9s_tweet_anatomy_moderator_badge_enabled", true);
        features.put("articles_preview_enabled", true);
        features.put("responsive_web_edit_tweet_api_enabled", true);
        features.put("graphql_is_translatable_rweb_tweet_is_translatable_enabled", true);
        features.put("view_counts_everywhere_api_enabled", true);
        features.put("longform_notetweets_consumption_enabled", true);
        features.put("responsive_web_twitter_article_tweet_consumption_enabled", true);
        features.put("tweet_awards_web_tipping_enabled", false);
        features.put("creator_subscriptions_quote_tweet_preview_enabled", false);
        features.put("freedom_of_speech_not_reach_fetch_enabled", true);
        features.put("standardized_nudges_misinfo", true);
        features.put("tweet_with_visibility_results_prefer_gql_limited_actions_policy_enabled", true);
        features.put("rweb_video_timestamps_enabled", true);
        features.put("longform_notetweets_rich_text_read_enabled", true);
        features.put("longform_notetweets_inline_media_enabled", true);
        features.put("rweb_tipjar_consumption_enabled", true);
        features.put("responsive_web_graphql_exclude_directive_enabled", true);
        features.put("verified_phone_label_enabled", false);
        features.put("responsive_web_graphql_skip_user_profile_image_extensions_enabled", false);
        features.put("responsive_web_graphql_timeline_navigation_enabled", true);
        features.put("responsive_web_enhance_cards_enabled", false);

        // FieldToggles
        HashMap<String, Object> fieldToggles = new HashMap<>();
        fieldToggles.put("withArticleRichContentState", true);
        fieldToggles.put("withArticlePlainText", false);
        fieldToggles.put("withGrokAnalyze", false);
        fieldToggles.put("withDisallowedReplyControls", false);

        // Define them
        queryMap.put("variables", variables);
        queryMap.put("features", features);
        queryMap.put("fieldToggles", fieldToggles);

        return queryMap;
    }
}
