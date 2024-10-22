package org.watermedia.api.network.patchs;

import org.watermedia.api.network.patchs.twitch.TwitchAPI;

import java.net.*;

public class TwitchPatch extends AbstractPatch {
    static {
        CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));
    }

    @Override
    public String platform() {
        return "Twitch";
    }

    @Override
    public boolean isValid(URI uri) {
        String host = uri.getHost();
        String path = uri.getPath();
        return host != null && (host.equals("www.twitch.tv") || host.equals("twitch.tv")) && path != null && path.startsWith("/");
    }

    @Override
    public Result patch(URI uri, Quality preferQuality) throws FixingURLException {
        super.patch(uri, preferQuality);
        try {
            String path = uri.getPath();
            if (path.startsWith("/videos/")) {
                return new Result(new URI(TwitchAPI.getVod(path.substring(8)).get(0).getUrl()), true, false);
            }

            return new Result(new URI(TwitchAPI.getStream(path.substring(1)).get(0).getUrl()), true, true);
        } catch (Exception e) {
            throw new FixingURLException(uri, e);
        }
    }
}