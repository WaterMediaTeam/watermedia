package org.watermedia.core.network.patchs;

import me.srrapero720.watermedia.api.MediaContext;
import org.watermedia.api.media.meta.MediaType;
import org.watermedia.api.network.MRL;
import org.watermedia.core.network.NetworkPatchException;
import org.watermedia.tools.DataTool;
import org.watermedia.tools.NetTool;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

public class LightshotPatch extends AbstractPatch {
    private static final Pattern HTML_PATTERN = Pattern.compile("<img[^>]*class=\"no-click screenshot-image\"[^>]*src=\"(https://[^\"]+)\"");

    @Override
    public String platform() {
        return "Lightshot";
    }

    @Override
    public boolean active(MediaContext context) {
        return true;
    }

    @Override
    public boolean validate(MRL source) {
        var host = source.getUri().getHost();
        return host.equals("prnt.sc");
    }

    @Override
    public void patch(MediaContext context, MRL source) throws NetworkPatchException {
        try {
            var html = connectToLightshot(source.getUri());
            var matcher = HTML_PATTERN.matcher(html);

            if (!matcher.find()) throw new NullPointerException("No match found");

            source.apply(new MRL.Patch()
                    .addSource()
                    .setUri(new URI(matcher.group(1)))
                    .setType(MediaType.IMAGE)
                    .build()
            );
        } catch (Exception e) {
            throw new NetworkPatchException(source, e);
        }
    }

    @Override
    public void test(MediaContext context, String url) {

    }

    public String connectToLightshot(URI url) throws IOException {
        HttpURLConnection conn = NetTool.connect(url, "GET");
        int code = conn.getResponseCode();

        switch (code) {
            case HttpURLConnection.HTTP_NOT_FOUND -> throw new NullPointerException("Image was not found");
            case HttpURLConnection.HTTP_FORBIDDEN, HttpURLConnection.HTTP_UNAUTHORIZED -> throw new UnsupportedOperationException("Access denied by Lightshot");
            default -> {
                if (code != HttpURLConnection.HTTP_OK)
                    throw new UnsupportedOperationException("Lightshot responses with a unexpected status code: " + code);
            }
        }

        try (InputStream in = conn.getInputStream()) {
            return new String(DataTool.readAllBytes(in), StandardCharsets.UTF_8);
        } finally {
            conn.disconnect();
        }
    }
}
