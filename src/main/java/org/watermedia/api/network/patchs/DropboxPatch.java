package org.watermedia.api.network.patchs;

import java.net.URI;

public class DropboxPatch extends AbstractPatch {
    @Override
    public String platform() {
        return "Dropbox";
    }

    @Override
    public boolean isValid(URI uri) {
        String query = uri.getQuery();
        String host = uri.getHost();
        return host != null && host.contains("dropbox.com") && query != null && query.contains("dl=0");
    }

    @Override
    public Result patch(URI uri, Quality preferQuality) throws FixingURLException {
        super.patch(uri, preferQuality);
        try {
            return new Result(new URI(uri.toString().replace("dl=0", "dl=1")), false, false);
        } catch (Exception e) {
            throw new FixingURLException(uri, e);
        }
    }
}