package org.watermedia.api.network.patchs;

import org.watermedia.core.tools.DataTool;
import org.watermedia.core.tools.NetTool;

import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MediaFirePatch extends AbstractPatch {
    private static final Pattern PATTERN_POPSOK = Pattern.compile("<a\\s+class=\"input\\s+popsok\"\\s+aria-label=\"Download\\s+file\"\\s+href=\"([^\"]+)\"[^>]*>");
    private static final Pattern PATTERN_DATA_SCRAMBLED = Pattern.compile("data-scrambled-url=\"([^\"]+)\"");

    @Override
    public String platform() {
        return "Mediafire";
    }

    @Override
    public boolean isValid(URI uri) {
        String host = uri.getHost();
        if (host == null) return false;

        String path = uri.getPath();
        if (path == null) return false;

        return host.equals("www.mediafire.com") && path.startsWith("/file/");
    }

    @Override
    public Result patch(URI uri, Quality prefQuality) throws FixingURLException {
        super.patch(uri, prefQuality);

        try {
            HttpURLConnection conn = NetTool.connectToHTTP(uri, "GET");

            int code = conn.getResponseCode();
            switch (code) {
                case HttpURLConnection.HTTP_INTERNAL_ERROR: throw new Exception("MediaFire is on fire (literally)");
                case HttpURLConnection.HTTP_NOT_FOUND: throw new NullPointerException("MediaFire file is on fire (literally)");
                case HttpURLConnection.HTTP_FORBIDDEN:
                case HttpURLConnection.HTTP_UNAUTHORIZED:
                    throw new UnsupportedOperationException("MediaFire placed a firewall to us - URL: " + uri);
                default:
                    if (code != HttpURLConnection.HTTP_OK)
                        throw new UnsupportedOperationException("Unexpected fired response from MediaFire (" + code + ") - URL: " + uri);
            }

            String html = new String(DataTool.readAllBytes(conn.getInputStream()), StandardCharsets.UTF_8);
            Matcher popsokMatcher = PATTERN_POPSOK.matcher(html);
            Matcher dataScrambledMatcher = PATTERN_DATA_SCRAMBLED.matcher(html);

            if (popsokMatcher.find()) {
                return new Result(new URI(popsokMatcher.group(1)), false, false);
            } else if (dataScrambledMatcher.find()) {
                String encoded = popsokMatcher.group(1);
                byte[] decoded = Base64.getDecoder().decode(encoded);
                return new Result(new URI(new String(decoded, StandardCharsets.UTF_8)), false, false);
            } else {
                throw new NullPointerException("No link found in MediaFire page - URL: " + uri);
            }
        } catch (Exception e) {
            throw new FixingURLException(uri.toString(), e);
        }
    }
}