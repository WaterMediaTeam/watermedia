package me.srrapero720.watermedia.api.url;

import java.net.URL;

public class DropboxFixer extends FixerBase {
    @Override
    public boolean isValid(URL url) {
        String q;
        return url.getHost().contains("dropbox.com") && ((q = url.getQuery()) != null) && q.contains("dl=0");
    }

    @Override
    public Result patch(URL url) throws FixingURLException {
        super.patch(url);
        try {
            return new Result(new URL(url.toString().replace("dl=0", "dl=1")), false, false);
        } catch (Exception e) {
            throw new FixingURLException(url, e);
        }
    }
}
