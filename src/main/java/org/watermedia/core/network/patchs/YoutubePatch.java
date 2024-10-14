package org.watermedia.core.network.patchs;

import me.srrapero720.watermedia.api.MediaContext;
import org.watermedia.api.network.MediaURI;
import org.watermedia.core.network.NetworkPatchException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class YoutubePatch extends AbstractPatch {
    private static final Pattern PATTERN = Pattern.compile("(?:youtu\\.be/|youtube\\.com/(?:embed/|v/|shorts/|feeds/api/videos/|watch\\?v=|watch\\?.+&v=))([^/?&#]+)");

    @Override
    public String platform() {
        return "Youtube";
    }

    @Override
    public boolean active(MediaContext context) {
        return true;
    }

    @Override
    public boolean validate(MediaURI source) {
        return PATTERN.matcher(source.toString()).find();
    }

    @Override
    public void patch(MediaContext context, MediaURI source) throws NetworkPatchException {
        Matcher matcher = PATTERN.matcher(source.toString());
        if (!matcher.find()) {
            throw new NetworkPatchException(source, "Invalid Youtube URI");
        }

        throw new NetworkPatchException(source, "Path not implemented yet");
//        return source;
    }

    @Override
    public void test(MediaContext context, String url) {

    }
}
