package org.watermedia.core.network.patchs;

import me.srrapero720.watermedia.api.MediaContext;
import org.watermedia.api.network.MRL;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PornHubPatch extends AbstractPatch {
    private static final Pattern VIDEO_QUALITY_PATTERN = Pattern.compile("(?<=\\*/)\\w+");
    private static final Pattern RESOLUTION_PATTERN = Pattern.compile("(?<=[_/])\\d*P(?=_)");

    @Override
    public String platform() {
        return "PornHub";
    }

    @Override
    public boolean active(MediaContext context) {
        return false;
    }

    @Override
    public boolean validate(MRL source) {
        var host = source.getUri().getHost();
        var path = source.getUri().getPath();
        return (host.contains(".pornhub.com") || host.equals("pornhub.com")) && path.endsWith("/view_video.php");
    }

    @Override
    public void patch(MediaContext context, MRL source) throws PatchException {

        try (InputStream i = null/*DynamicRequest connection = new DynamicRequest(dynamicURL); InputStream reader = connection.getInputStream()*/) {
//            String source = new String(ByteTools.readAllBytes(reader), StandardCharsets.UTF_8);
            String apiSource = "";
            List<String> urls = new ArrayList<>();
            List<VideoQuality> videoQualities = new ArrayList<>();
            List<String> matches = new ArrayList<>();

            // GET QUALITIES
            Matcher matcher = VIDEO_QUALITY_PATTERN.matcher(apiSource);
            while (matcher.find()) {
                matches.add(matcher.group());
            }

            // PARSE QUALITIES
            for (String match: matches) {
                String regexString = "(?<=" + match + "=\")[^;]+(?=\")";
                Pattern regexPattern = Pattern.compile(regexString);
                Matcher regexMatcher = regexPattern.matcher(apiSource);
                if (regexMatcher.find()) {
                    String value = regexMatcher.group().replaceAll("[\" +]", "");

                    if (value.startsWith("https")) {
                        if (urls.size() == 4) {
                            break; // may is not a good option
                        }
                        urls.add(value);
                    } else {
                        urls.set(urls.size() - 1, urls.get(urls.size() - 1) + value);
                    }
                }
            }

            // PARSE RESOLUTIONS
            for (String x: urls) {
                Matcher resolutionMatcher = RESOLUTION_PATTERN.matcher(x);
                if (resolutionMatcher.find()) {
                    String resolution = resolutionMatcher.group();
                    videoQualities.add(new VideoQuality(resolution, x));
                }
            }

//            return new DynamicURL(videoQualities.get(0).getUri(), true, false);
        } catch (Exception e) {
//            throw new PatchingURLException(dynamicURL, e);
        }
    }

    @Override
    public void test(MediaContext context, String url) {

    }

    private record VideoQuality(String resolution, String uri) {

    }
}
