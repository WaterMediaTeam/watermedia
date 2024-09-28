package me.srrapero720.watermedia.api.network.patches;

import me.srrapero720.watermedia.api.MediaModContext;
import me.srrapero720.watermedia.api.uri.MediaSource;
import me.srrapero720.watermedia.api.network.URIPatchException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class YoutubePatch extends AbstractPatch {
    private static final Pattern PATTERN = Pattern.compile("(?:youtu\\.be/|youtube\\.com/(?:embed/|v/|shorts/|feeds/api/videos/|watch\\?v=|watch\\?.+&v=))([^/?&#]+)");

    @Override
    public String platform() {
        return "Youtube";
    }

    @Override
    public boolean validate(MediaSource source) {
        return PATTERN.matcher(source.toString()).find();
    }

    @Override
    public MediaSource patch(MediaSource source, MediaModContext context) throws URIPatchException {
        Matcher matcher = PATTERN.matcher(source.toString());
        if (!matcher.find()) {
            throw new URIPatchException(source, "Invalid Youtube URI");
        }

        throw new URIPatchException(source, "Path not implemented yet");
//        return source;
    }
}
