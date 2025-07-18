package org.watermedia.api.network.patchs;

import org.watermedia.WaterMedia;
import org.schabi.newpipe.extractor.NewPipe;
import org.schabi.newpipe.extractor.ServiceList;
import org.schabi.newpipe.extractor.stream.AudioStream;
import org.schabi.newpipe.extractor.stream.StreamExtractor;
import org.schabi.newpipe.extractor.stream.StreamType;
import org.schabi.newpipe.extractor.stream.VideoStream;
import org.watermedia.api.network.patchs.twitch.StreamQuality;
import org.watermedia.api.network.patchs.youtube.HttpClientDownloader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class YoutubePatch extends AbstractPatch {
    static {
        NewPipe.init(new HttpClientDownloader());
    }
    private static final Pattern PATTERN = Pattern.compile("(?:youtu\\.be/|youtube\\.com/(?:embed/|v/|shorts/|feeds/api/videos/|watch\\?v=|watch\\?.+&v=))([^/?&#]+)");


    @Override
    public String platform() {
        return "Youtube";
    }

    @Override
    public boolean isValid(URI uri) {
        return uri.getHost() != null && PATTERN.matcher(uri.toString()).find();
    }

    @Override
    public Result patch(URI uri, Quality preferQuality) throws FixingURLException {
        super.patch(uri, preferQuality);
        Matcher matcher = PATTERN.matcher(uri.toString());
        if (matcher.find()) {
            return patchNewpipeExtractor(uri, preferQuality);
        }
        return null;
    }

    private Result patchNewpipeExtractor(URI uri, Quality preferQuality) throws FixingURLException {
        try {
            StreamExtractor extractor = ServiceList.YouTube.getStreamExtractor(uri.toString());
            extractor.fetchPage();

            if(extractor.getStreamType().equals(StreamType.LIVE_STREAM) || extractor.getStreamType().equals(StreamType.AUDIO_LIVE_STREAM)) {
                String ytLivePlaylist = fetchLivePlaylist(extractor.getHlsUrl());
                if (ytLivePlaylist != null) {
                    return new Result(new URI(StreamQuality.parse(ytLivePlaylist).get(0).getUrl()), true, true);
                }
                throw new RuntimeException("Failed to parse live playlist");
            } else {
                VideoStream bestCombined = getBestVideo(extractor.getVideoStreams(), preferQuality);
                VideoStream bestVideo = getBestVideo(extractor.getVideoOnlyStreams(), preferQuality);
                AudioStream bestAudio = getBestAudio(extractor.getAudioStreams());

                if (WaterMedia.YES_SLAVISM.getAsBoolean()) {
                    if (bestVideo != null) { 
                        Result r = new Result(new URI(bestVideo.getContent()), true, false);
                        if (bestVideo != bestCombined) {
                            r.setAudioTrack(new URI(bestAudio.getContent()));
                        }
                        return r;
                    } else if (bestAudio != null) {
                        return new Result(new URI(bestAudio.getContent()), true, false);
                    }
                } else {
                    if (bestCombined != null) return new Result(new URI(bestCombined.getContent()), true, false);
                    if (bestVideo != null) return new Result(new URI(bestVideo.getContent()), true, false);
                    if (bestAudio != null) return new Result(new URI(bestAudio.getContent()), true, false);
                }
                return null;
            }
        } catch(Exception e) {
            throw new FixingURLException(uri.toString(), e);
        }
    }

    private Integer getMaxWidthForQuality(Quality preferQuality) {
        switch(preferQuality != null ? preferQuality : Quality.MIDDLE) {
            case LOWEST:
                return 256;
            case LOW:
                return 426;
            case MIDDLE:
                return 480;
            case HIGH:
                return 1280;
            case HIGHEST:
            default:
                return 1920;
        }
    }

    private Integer getMaxFPSForQuality(Quality preferQuality) {
        switch(preferQuality != null ? preferQuality : Quality.MIDDLE) {
            case HIGHEST:
            case HIGH:
                return 60;
            case MIDDLE:
            case LOW:
            case LOWEST:
            default:
                return 30;
        }
    }

    private VideoStream getBestVideo(List<VideoStream> streams, Quality preferQuality) {
        if(streams == null) return null;

        // Filter to URL streams and sort by quality. Initialize return with lowest quality then incrementally incrase quality up to preferred.
        List<VideoStream> priorityStreams = streams.stream()
            .filter(VideoStream::isUrl)
            .sorted((a,b) -> a.getWidth() >= a.getWidth() && a.getFps() >= b.getFps() ? 1 : -1)
            .collect(Collectors.toList());
        return priorityStreams.stream()
            .filter(s -> s.getWidth() <= getMaxWidthForQuality(preferQuality) && s.getFps() <= getMaxFPSForQuality(preferQuality))
            .reduce((a,b) -> a).orElse(priorityStreams.get(0));
    }

    private AudioStream getBestAudio(List<AudioStream> streams) {
        if(streams == null) return null;
        return streams.stream().filter(s -> s.isUrl()).reduce((current, next) -> {
            return next.getBitrate() > current.getBitrate() ? next : current;
        }).orElse(null);
    }

    private String fetchLivePlaylist(String url) throws IOException {
        URL apiUrl = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) apiUrl.openConnection();
        conn.setRequestMethod("GET");

        int responseCode = conn.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) return null;

        InputStream inputStream = conn.getInputStream();
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) != -1) {
            result.write(buffer, 0, length);
        }
        return result.toString("UTF-8");
    }
}