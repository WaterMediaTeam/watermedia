package me.srrapero720.watermedia.api.network.patches;

import me.srrapero720.watermedia.api.MediaContext;
import me.srrapero720.watermedia.api.network.MediaURI;
import me.srrapero720.watermedia.api.network.URIPatchException;

public class KickPatch extends AbstractPatch {
    private static final String API_URL = "https://kick.com/api/v1/";

    @Override
    public String platform() {
        return "Kick";
    }

    @Override
    public boolean validate(MediaURI source) {
        return source.getUri().getHost().equals("kick.com");
    }

    @Override
    public MediaURI patch(MediaURI source, MediaContext context) throws URIPatchException {
        /*

        try {
            if (dynamicURL.asURL().getPath().contains("/video/")) {
                String videoID = dynamicURL.asURL().getPath().replace("/video/", "");
                KickVideo video = getVideoInfo(videoID);
                return new DynamicURL(video.url, true, false);
            } else {
                String streamerName = dynamicURL.asURL().getPath().replace("/", "");
                KickChannel channel = getChannelInfo(streamerName);
                if (channel.livestream == null || !channel.livestream.isStreaming) throw new ConnectException("Streamer is not online");
                return new DynamicURL(channel.url, true, true);
            }
        } catch (Exception e) {
            throw new PatchingURLException(dynamicURL, e);
        }
         */
        return null;
    }
//
//    public KickChannel getChannelInfo(String channel) throws IOException {
//        try (DynamicRequest request = new DynamicRequest(new DynamicURL(API_URL + "channels/" + channel)); InputStreamReader in = new InputStreamReader(request.getInputStream())) {
//            return DataTool.GSON.fromJson(in, KickChannel.class);
//        }
//    }
//
//    public KickVideo getVideoInfo(String videoId) throws IOException {
//        try (DynamicRequest request = new DynamicRequest(new DynamicURL(API_URL + "video/" + videoId)); InputStreamReader in = new InputStreamReader(request.getInputStream())) {
//            return DataTool.GSON.fromJson(in, KickVideo.class);
//        }
//    }
}
