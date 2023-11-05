package me.srrapero720.watermedia.api.url.fixers;

import me.srrapero720.watermedia.api.network.onedrive.OneDriveUtil;

import java.net.URL;
import java.util.regex.Pattern;

public class OnedriveFixer extends URLFixer {

    private static final Pattern ONE_DRIVE_URL_PATTERN = Pattern.compile("^https://1drv.ms/[a-z]/[a-zA-Z0-9!_-]+$");

    @Override
    public String platform() {
        return "OneDrive";
    }

    @Override
    public boolean isValid(URL url) {
        return ONE_DRIVE_URL_PATTERN.matcher(url.toString()).matches();
    }

    @Override
    public Result patch(URL url, Quality preferQuality) throws FixingURLException {
        super.patch(url, preferQuality);
        try {
            return new Result(OneDriveUtil.getDownloableItem(url.toString()).getUrl(), false, false);
        } catch (Exception e) {
            throw new FixingURLException(url.toString(), e);
        }
    }
}