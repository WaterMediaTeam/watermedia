package me.srrapero720.watermedia.api.network.patches;

import me.srrapero720.watermedia.api.MediaContext;
import me.srrapero720.watermedia.api.network.MediaURI;
import me.srrapero720.watermedia.api.network.URIPatchException;

public class DropboxPatch extends AbstractPatch {
    @Override
    public String platform() {
        return "Dropbox (quickfix)";
    }

    @Override
    public boolean validate(MediaURI source) {
        var host = source.getUri().getHost();
        var query = source.getUri().getQuery();
        return host.contains("dropbox.com") && query.equals("dl=0");
    }

    @Override
    public MediaURI patch(MediaURI source, MediaContext context) throws URIPatchException {
        return null;
    }
}
