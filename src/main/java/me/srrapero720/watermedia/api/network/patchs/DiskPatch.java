package me.srrapero720.watermedia.api.network.patchs;

import me.srrapero720.watermedia.WaterMedia;

import java.io.File;
import java.net.URI;

public class DiskPatch extends AbstractPatch {
    @Override
    public String platform() {
        return "Local";
    }

    @Override
    public boolean isValid(URI uri) {
        return uri.getAuthority().equals("water");
    }

    @Override
    public Result patch(URI uri, Quality prefQuality) throws FixingURLException {
        super.patch(uri, prefQuality);

        try {
            if (uri.getAuthority().equals("water")) {
                switch (uri.getHost()) {
                    case "local.wm":
                        return new AbstractPatch.Result(new File("").toPath().resolve(uri.getPath().substring(1)).toUri(), false, false);
                    case "user.wm":
                        break; // NO-OP for now, requires security checks
                    case "tmp.wm":
                        return new AbstractPatch.Result(WaterMedia.getLoader().tempDir().resolve(uri.getPath().substring(1)).toUri(), false, false);
                }
            }

            throw new IllegalArgumentException("invalid water protocol");
        } catch (Exception e) {
            throw new FixingURLException(uri, e);
        }
    }
}
