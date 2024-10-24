package org.watermedia.api.network.patchs;

import org.watermedia.WaterMedia;

import java.io.File;
import java.net.URI;

public class DiskPatch extends AbstractPatch {
    @Override
    public String platform() {
        return "Local";
    }

    @Override
    public boolean isValid(URI uri) {
        String scheme = uri.getScheme();
        return scheme != null && scheme.equals("water");
    }

    @Override
    public Result patch(URI uri, Quality prefQuality) throws FixingURLException {
        super.patch(uri, prefQuality);

        try {

            if (uri.getScheme().equals("water")) {
                switch (uri.getHost()) {
                    case "local":
                        return new AbstractPatch.Result(new File("").toPath().resolve(uri.getPath().substring(1)).toUri(), false, false);
                    case "user":
                        break; // NO-OP for now, requires security checks
                    case "temp":
                        return new AbstractPatch.Result(WaterMedia.getLoader().tempDir().resolve(uri.getPath().substring(1)).toUri(), false, false);
                }
            }

            throw new IllegalArgumentException("invalid water protocol");
        } catch (Exception e) {
            throw new FixingURLException(uri, e);
        }
    }
}
