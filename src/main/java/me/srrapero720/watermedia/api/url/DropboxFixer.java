package me.srrapero720.watermedia.api.url;

import java.net.URL;

public class DropboxFixer extends URLFixer {
    @Override
    public String platform() {
        return "Dropbox";
    }

    @Override
    public boolean isValid(URL url) {
        String q;
        return url.getHost().contains("dropbox.com") && ((q = url.getQuery()) != null) && q.contains("dl=0");
    }

    @Override
    public Result patch(URL url, Quality preferQuality) throws FixingURLException {
        super.patch(url, preferQuality);
        try {
            return new Result(new URL(url.toString().replace("dl=0", "dl=1")), false, false);
        } catch (Exception e) {
            throw new FixingURLException(url, e);
        }
    }
}