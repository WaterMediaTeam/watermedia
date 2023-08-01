package me.srrapero720.watermedia.api.url;

import me.srrapero720.watermedia.api.network.kick.KickAPI;
import me.srrapero720.watermedia.api.network.kick.models.KickChannel;
import me.srrapero720.watermedia.api.network.kick.models.KickVideo;
import retrofit2.Call;
import retrofit2.Response;

import java.net.URL;

public class KickFixer extends AbstractFixer {

    @Override
    public boolean isValid(URL url) {
        return url.getHost().contains("kick.com");
    }

    @Override
    public URL patch(URL url) throws PatchingUrlException {
        super.patch(url);

        if (url.getPath().contains("/video/")) {
             Call<KickVideo> call = KickAPI.NET.getVideoInfo(url.getPath().replace("/video/", ""));
             try {
                 Response<KickVideo> res = call.execute();
                 if (res.isSuccessful() && res.body() != null) return new URL(res.body().url);
             } catch (Exception e) {
                 throw new PatchingUrlException(url.toString(), e);
             }
        } else {
            Call<KickChannel> call = KickAPI.NET.getChannelInfo(url.getPath().replace("/", ""));
            try {
                Response<KickChannel> res = call.execute();
                if (res.isSuccessful() && res.body() != null) return new URL(res.body().url);
            } catch (Exception e) {
                throw new PatchingUrlException(url.toString(), e);
            }
        }

        return null;
    }
}