package me.srrapero720.watermedia.api.url.patches;

import me.srrapero720.watermedia.api.url.URLPatch;
import me.srrapero720.watermedia.api.url.util.onedrive.OneDriveUtil;

import java.net.URL;
import java.util.regex.Pattern;

public class OnedrivePatch extends URLPatch {

    private static final Pattern ONE_DRIVE_URL_PATTERN = Pattern.compile("^https://1drv.ms/[a-z]/[a-zA-Z0-9!_-]+$");

    @Override
    public boolean isValid(URL url) {
        return ONE_DRIVE_URL_PATTERN.matcher(url.toString()).matches();
    }

    @Override
    public URL patch(URL url) throws PatchingUrlException {
        super.patch(url);
        try {
            return new URL(OneDriveUtil.getDownloableItem(url.toString()).getUrl());
        } catch (Exception e) {
            throw new PatchingUrlException(url.toString(), e);
        }
    }
}
