package me.srrapero720.watermedia.api.network.patches;

import me.srrapero720.watermedia.api.MediaModContext;
import me.srrapero720.watermedia.api.network.URIPatchException;
import me.srrapero720.watermedia.api.uri.MediaSource;

public class DropboxPatch extends AbstractPatch {
    @Override
    public String platform() {
        return "Dropbox (quickfix)";
    }

    @Override
    public boolean validate(MediaSource source) {
        var host = source.getUri().getHost();
        var query = source.getUri().getQuery();
        return host.contains("dropbox.com") && query.equals("dl=0");
    }

    @Override
    public MediaSource patch(MediaSource source, MediaModContext context) throws URIPatchException {
        return null;
    }
}
