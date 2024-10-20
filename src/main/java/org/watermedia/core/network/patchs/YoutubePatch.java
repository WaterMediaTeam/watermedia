package org.watermedia.core.network.patchs;

import me.srrapero720.watermedia.api.MediaContext;
import org.watermedia.api.network.MRL;

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
    public boolean validate(MRL source) {
        return PATTERN.matcher(source.toString()).find();
    }

    @Override
    public void patch(MediaContext context, MRL source) throws PatchException {
        Matcher matcher = PATTERN.matcher(source.toString());
        if (!matcher.find()) {
            throw new PatchException(source, "Invalid Youtube URI");
        }

        throw new PatchException(source, "Path not implemented yet");
//        return source;
    }

    @Override
    public void test(MediaContext context, String url) {

    }
}
