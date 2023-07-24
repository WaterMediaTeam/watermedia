package me.srrapero720.watermedia.compat;

import me.srrapero720.watermedia.api.url.URLPatch;
import me.srrapero720.watermedia.api.url.patch.util.kick.KickAPI;
import me.srrapero720.watermedia.api.url.patch.util.kick.models.KickChannel;
import me.srrapero720.watermedia.api.url.patch.util.kick.models.KickVideo;
import retrofit2.Call;
import retrofit2.Response;

import java.net.MalformedURLException;
import java.net.URL;

import static me.srrapero720.watermedia.WaterMedia.LOGGER;

public class Kick extends URLPatch {
    public static void main(String[] args) throws MalformedURLException, PatchingUrlException {
        LOGGER.info(new Kick().patch(new URL("https://kick.com/kenji")));
        LOGGER.info(new Kick().patch(new URL("https://kick.com/video/af9f91d4-9cbc-4395-8f35-9ef9f245fb8c")));
    }


    @Override
    public boolean isValid(URL url) {
        return url.getHost().contains("kick.com");
    }

    @Override
    public String patch(URL url) throws PatchingUrlException {
        super.patch(url);

        if (url.getPath().contains("/video/")) {
             Call<KickVideo> call = KickAPI.NET.getVideoInfo(url.getPath().replace("/video/", ""));
             try {
                 Response<KickVideo> res = call.execute();
                 if (res.isSuccessful() && res.body() != null) return res.body().url;
             } catch (Exception e) {
                 LOGGER.error("Detected Kick.com video but Compat failed to get real URL", e);
             }
        } else {
            Call<KickChannel> call = KickAPI.NET.getChannelInfo(url.getPath().replace("/", ""));
            try {
                Response<KickChannel> res = call.execute();
                if (res.isSuccessful() && res.body() != null) return res.body().url;
            } catch (Exception e) {
                LOGGER.error("Detected Kick.com stream but Compat failed to get real URL", e);
            }
        }

        return null;
    }
}