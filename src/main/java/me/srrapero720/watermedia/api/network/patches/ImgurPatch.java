package me.srrapero720.watermedia.api.network.patches;

import me.srrapero720.watermedia.api.MediaContext;
import me.srrapero720.watermedia.api.MediaType;
import me.srrapero720.watermedia.api.network.MediaURI;
import me.srrapero720.watermedia.api.network.URIPatchException;
import me.srrapero720.watermedia.api.network.patches.models.imgur.Gallery;
import me.srrapero720.watermedia.api.network.patches.models.imgur.Image;
import me.srrapero720.watermedia.api.network.patches.models.imgur.Response;
import me.srrapero720.watermedia.tools.DataTool;
import me.srrapero720.watermedia.tools.NetTool;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;

public class ImgurPatch extends AbstractPatch {
    private static final String API_URL = "https://api.imgur.com/3";
    private static final String API_KEY = "685cdf74b1229b9";

    private static final String IMAGE_URL = API_URL + "/image/%s?client_id=" + API_KEY;
    private static final String GALLERY_URL = API_URL + "/gallery/%s?client_id=" + API_KEY;
    private static final String TAG_GALLERY_URL = API_URL + "/gallery/t/%s/%s?client_id=" + API_KEY;

    @Override
    public String platform() {
        return "Imgur";
    }

    @Override
    public boolean validate(MediaURI source) {
        return source.getUri().getHost().equals("imgur.com"); // i.imgur.com - doesn't need patches
    }

    @Override
    public MediaURI patch(MediaURI source, MediaContext context) throws URIPatchException {
        final var path = source.getUri().getPath();
        final var fragment = source.getUri().getFragment();

        // SPECIAL PARSE
        final boolean gallery = path.startsWith("/gallery/") || path.startsWith("/a/");
        final boolean tagGallery = fragment != null && fragment.contains("#/t/");

        final var pathSplit = path.substring(1).split("/"); // remove first slash and get the path splitted
        final var idSplit = pathSplit[1].split("-"); // i hope imgur do their job and IDs don't contains "-"

        final var tag = fragment != null ? fragment.substring("#/t/".length()) : null;
        final var id = idSplit[idSplit.length - 1];

        try {
            if (gallery) { // open it as a gallery
                final var url = tagGallery ? String.format(TAG_GALLERY_URL, tag, id) : String.format(GALLERY_URL, id);
                final Response<Gallery> res = DataTool.fromJSON(connectResponse(url), Response.class);

                if (res.data == null || res.data.images.length == 0)
                    throw new NullPointerException("Response is successfully but data is null or images are empty");

                // METADATA BUILDING
                var metadata = new MediaURI.Metadata(
                        res.data.title,
                        res.data.accountUrl,
                        this.platform(),
                        DataTool.orElse(res.data.desc, res.data.images[0].desc),
                        null,
                        0
                        );

                // PATCH BUILDING
                var patch = new MediaURI.Patch();
                for (Image image: res.data.images) {
                    patch.addSource()
                            .setUri(new URI(image.link))
                            .setType(MediaType.getByMimetype(image.type))
                            .build();
                }

                patch.setMetadata(metadata);
                source.apply(patch);

            } else { // assume is a simple image
                final var url = String.format(IMAGE_URL, id);
                final Response<Image> res = DataTool.fromJSON(connectResponse(url), Response.class);

                if (res.data == null)
                    throw new NullPointerException("Response is successfully but data is null or images are empty");

                // METADATA BUILDING
                var metadata = new MediaURI.Metadata(
                        res.data.title,
                        res.data.accountUrl,
                        this.platform(),
                        res.data.desc,
                        null,
                        0
                );

                // PATCH BUILDING
                var patch = new MediaURI.Patch();
                patch.addSource()
                        .setUri(new URI(res.data.link))
                        .setType(MediaType.getByMimetype(res.data.type))
                        .build();
                patch.setMetadata(metadata);

                source.apply(patch);
            }
        } catch (Exception e) {
            throw new URIPatchException(source, e);
        }

        return source;
    }

    public String connectResponse(String url) throws IOException {
        HttpURLConnection conn = NetTool.connect(url, "GET");
        int code = conn.getResponseCode();
        switch (code) {
            case HttpURLConnection.HTTP_NOT_FOUND -> throw new NullPointerException("Image or gallery not founded");
            case HttpURLConnection.HTTP_FORBIDDEN, HttpURLConnection.HTTP_UNAUTHORIZED -> throw new UnsupportedOperationException("Access denied by Imgur");
            default -> {
                if (code != HttpURLConnection.HTTP_OK)
                    throw new UnsupportedOperationException("Imgur responses with a unexpected status code: " + code);
            }
        }

        try(InputStream in = conn.getInputStream()) {
            return new String(DataTool.readAllBytes(in), StandardCharsets.UTF_8);
        } finally {
            conn.disconnect();
        }
    }
}
