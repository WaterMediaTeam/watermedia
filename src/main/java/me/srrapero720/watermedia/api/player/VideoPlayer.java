package me.srrapero720.watermedia.api.player;

import me.lib720.caprica.vlcj.factory.MediaPlayerFactory;
import me.lib720.caprica.vlcj.player.embedded.videosurface.callback.RenderCallback;
import me.lib720.caprica.vlcj.player.embedded.videosurface.callback.SimpleBufferFormatCallback;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import javax.annotation.Nullable;
import java.awt.*;

@SuppressWarnings("unused")
public class VideoPlayer extends MediaPlayerBase {
    private static final Marker IT = MarkerManager.getMarker("VideoPlayer");

    public VideoPlayer(@Nullable MediaPlayerFactory factory, @Nullable RenderCallback renderCallback, @Nullable SimpleBufferFormatCallback bufferFormatCallback) {
        super(factory, renderCallback, bufferFormatCallback);
    }

    public synchronized Dimension getDimensions() {
        if (raw == null) return null;
        synchronized (this) {
            return raw.mediaPlayer().video().videoDimension();
        }
    }
}

