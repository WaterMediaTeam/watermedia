package org.watermedia.core.network.patchs;

import me.srrapero720.watermedia.api.MediaContext;
import org.watermedia.api.network.MRL;
import org.watermedia.core.network.NetworkPatchException;

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
    public boolean validate(MRL source) {
        var host = source.getUri().getHost();
        var query = source.getUri().getQuery();
        return host.contains("dropbox.com") && query.equals("dl=0");
    }

    @Override
    public void patch(MediaContext context, MRL source) throws NetworkPatchException {
        var url = source.getUri().toString();
        try {
            var r = url.replace("dl=0", "dl=1");
            var uri = new URI(r);

            source.apply(new MRL.Patch()
                    .addSource()
                    .setUri(uri)
                    .build()
            );
        } catch (Exception e) {
            throw new NetworkPatchException(source, e);
        }
    }

    @Override
    public void test(MediaContext context, String url) {

    }
}
