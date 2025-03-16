package org.watermedia.api.network.patchs;

import org.watermedia.api.network.patchs.onedrive.OneDriveUtil;

import java.net.URI;
import java.util.regex.Pattern;

public class OnedrivePatch extends AbstractPatch {

    private static final Pattern ONE_DRIVE_URL_PATTERN = Pattern.compile("^https://1drv.ms/[a-z]/[a-z]/[a-zA-Z0-9!_-]+$");

    @Override
    public String platform() {
        return "OneDrive";
    }

    @Override
    public boolean isValid(URI uri) {
        String host = uri.getHost();
        if (host == null) return false;

        return host.equals("1drv.ms");
    }

    @Override
    public Result patch(URI uri, Quality preferQuality) throws FixingURLException {
        super.patch(uri, preferQuality);
        try {
            return new Result(new URI(OneDriveUtil.getDownloableItem(uri.toString()).getUrl()), false, false);
        } catch (Exception e) {
            throw new FixingURLException(uri.toString(), e);
        }
    }
}