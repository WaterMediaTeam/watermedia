package me.srrapero720.watermedia.api.url.fixers;

import me.srrapero720.watermedia.api.network.imgurv3.ImgurAPIv3;
import me.srrapero720.watermedia.api.network.imgurv3.models.ImgurAlbumTagData;
import me.srrapero720.watermedia.api.network.imgurv3.models.ImgurData;
import me.srrapero720.watermedia.api.network.imgurv3.models.images.ImgurImage;
import retrofit2.Response;

import java.net.URL;

import static me.srrapero720.watermedia.WaterMedia.LOGGER;
import static me.srrapero720.watermedia.api.url.UrlAPI.IT;

public class ImgurFixerV3 extends URLFixer {
    @Override
    public String platform() {
        return "Imgur";
    }

    @Override
    public boolean isValid(URL url) {
        return url.getHost().equals("imgur.com"); // i.imgur.com is a working url
    }

    @Override
    public Result patch(URL url, Quality prefQuality) throws FixingURLException {
        super.patch(url, prefQuality);
        try {
            String path = url.getPath();
            if (path.endsWith("/")) path = path.substring(0, path.length() - 1);

            // URL DATA
            String[] ps = path.split("/");
            String id = ps[ps.length - 1];
            String tag = ps[ps.length - 2];

            if (path.startsWith("/gallery/") || path.startsWith("/a/")) {
                Response<ImgurData<ImgurAlbumTagData>> res = ImgurAPIv3.NET.getImageFromAlbum(id).execute();
                ImgurAlbumTagData data;
                if (res.isSuccessful() && res.body() != null && (data = res.body().data) != null && !data.images.isEmpty()) {
                    for (ImgurImage image: data.images) {
                        // TODO: add support for multiple entries
                        return new Result(new URL(image.link), image.type.startsWith("video"), false);
                    }
                } else {
                    LOGGER.debug(IT, "Imgur responses with status code: {}", res.code());
                    throw new Exception("Cannot load imgur data, " + res.message());
                }
            } else if (path.startsWith("/t/")) {
                Response<ImgurData<ImgurAlbumTagData>> res = ImgurAPIv3.NET.getImageFromTagGallery(tag, id).execute();
                ImgurAlbumTagData data;
                if (res.isSuccessful() && res.body() != null && (data = res.body().data) != null) {
                    if (data.images != null && !data.images.isEmpty()) {
                        for (ImgurImage image: data.images) {
                            // TODO: add support for multiple entries
                            return new Result(new URL(image.link), image.type.startsWith("video"), false);
                        }
                    } else {
                        LOGGER.debug(IT, "Imgur responses with status code '{}' but with a empty image list", res.code());
                        throw new Exception("Cannot load imgur data, empty list");
                    }
                } else {
                    LOGGER.debug(IT, "Imgur responses with status code: {}", res.code());
                    throw new Exception("Cannot load imgur data, " + res.message());
                }
            } else {
                Response<ImgurData<ImgurImage>> res = ImgurAPIv3.NET.getImage(id).execute();
                ImgurImage data;
                if (res.isSuccessful() && res.body() != null && (data = res.body().data) != null) {
                    return new Result(new URL(data.link), data.type.startsWith("video"), false);
                } else {
                    LOGGER.debug(IT, "Imgur responses with status code: {}", res.code());
                    throw new Exception("Cannot load imgur data, " + res.message());
                }
            }
            throw new Exception("Unreachable code");
        } catch (Exception e) {
            throw new FixingURLException(url, e);
        }
    }
}