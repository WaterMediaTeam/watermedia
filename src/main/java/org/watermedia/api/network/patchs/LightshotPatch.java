package org.watermedia.api.network.patchs;

import org.watermedia.core.tools.DataTool;
import org.watermedia.core.tools.NetTool;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LightshotPatch extends AbstractPatch {
    private static final Pattern HTML_PATTERN = Pattern.compile("<img[^>]*class=\"no-click screenshot-image\"[^>]*src=\"(https://[^\"]+)\"");

    @Override
    public String platform() {
        return "Lightshot";
    }

    @Override
    public boolean isValid(URI uri) {
        String host = uri.getHost();
        return host != null && host.equals("prnt.sc");
    }

    @Override
    public Result patch(URI uri, Quality prefQuality) throws FixingURLException {
        super.patch(uri, prefQuality);
        try {
            String html = connectToLightshot(uri);
            Matcher matcher = HTML_PATTERN.matcher(html);

            if (matcher.find()) {
                return new Result(new URI(matcher.group(1)), false, false);
            }

             throw new NullPointerException("No match was found");
        } catch (Exception e) {
            throw new FixingURLException(uri, e);
        }
    }

    public String connectToLightshot(URI url) throws IOException {
        HttpURLConnection conn = NetTool.connectToHTTP(url, "GET");
        int code = conn.getResponseCode();

        switch (code) {
            case HttpURLConnection.HTTP_NOT_FOUND: throw new NullPointerException("Image was not found");
            case HttpURLConnection.HTTP_FORBIDDEN:
            case HttpURLConnection.HTTP_UNAUTHORIZED: throw new UnsupportedOperationException("Access denied by Lightshot");
            default:
                if (code != HttpURLConnection.HTTP_OK)
                    throw new UnsupportedOperationException("Lightshot responses with a unexpected status code: " + code);
        }

        try (InputStream in = conn.getInputStream()) {
            return new String(DataTool.readAllBytes(in), StandardCharsets.UTF_8);
        } finally {
            conn.disconnect();
        }
    }
}
