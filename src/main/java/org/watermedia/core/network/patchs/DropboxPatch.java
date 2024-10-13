package org.watermedia.core.network.patchs;

import me.srrapero720.watermedia.api.MediaContext;
import org.watermedia.api.network.MediaURI;
import org.watermedia.core.network.URIPatchException;

import java.net.URI;

public class DropboxPatch extends AbstractPatch {
    @Override
    public String platform() {
        return "Dropbox";
    }

    @Override
    public boolean active(MediaContext context) {
        return true;
    }

    @Override
    public boolean validate(MediaURI source) {
        var host = source.getUri().getHost();
        var query = source.getUri().getQuery();
        return host.contains("dropbox.com") && query.equals("dl=0");
    }

    @Override
    public void patch(MediaContext context, MediaURI source) throws URIPatchException {
        var url = source.getUri().toString();
        try {
            var r = url.replace("dl=0", "dl=1");
            var uri = new URI(r);

            source.apply(new MediaURI.Patch()
                    .addSource()
                    .setUri(uri)
                    .build()
            );
        } catch (Exception e) {
            throw new URIPatchException(source, e);
        }
    }

    @Override
    public void test(MediaContext context, String url) {

    }
}
