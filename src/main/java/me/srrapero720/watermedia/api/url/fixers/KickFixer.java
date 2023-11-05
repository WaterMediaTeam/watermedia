package me.srrapero720.watermedia.api.url.fixers;

import me.srrapero720.watermedia.api.network.kick.KickAPI;
import me.srrapero720.watermedia.api.network.kick.models.KickChannel;
import me.srrapero720.watermedia.api.network.kick.models.KickVideo;
import retrofit2.Call;
import retrofit2.Response;

import java.net.URL;

public class KickFixer extends URLFixer {

    @Override
    public String platform() {
        return "Kick.com";
    }

    @Override
    public boolean isValid(URL url) {
        return url.getHost().contains("kick.com");
    }

    @Override
    public Result patch(URL url, Quality preferQuality) throws FixingURLException {
        super.patch(url, preferQuality);

        if (url.getPath().contains("/video/")) {
             try {
                 Call<KickVideo> call = KickAPI.NET.getVideoInfo(url.getPath().replace("/video/", ""));
                 Response<KickVideo> res = call.execute();
                 if (res.isSuccessful() && res.body() != null) return new Result(res.body().url, true, false);
             } catch (Exception e) {
                 throw new FixingURLException(url.toString(), e);
             }
        } else {
            try {
                Call<KickChannel> call = KickAPI.NET.getChannelInfo(url.getPath().replace("/", ""));
                Response<KickChannel> res = call.execute();
                if (res.isSuccessful() && res.body() != null) return new Result(res.body().url, true, true);
            } catch (Exception e) {
                throw new FixingURLException(url.toString(), e);
            }
        }

        return null;
    }
}