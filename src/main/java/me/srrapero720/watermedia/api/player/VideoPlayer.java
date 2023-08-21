package me.srrapero720.watermedia.api.player;

import me.lib720.caprica.vlcj.factory.MediaPlayerFactory;
import me.lib720.caprica.vlcj.player.embedded.videosurface.callback.RenderCallback;
import me.lib720.caprica.vlcj.player.embedded.videosurface.callback.SimpleBufferFormatCallback;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.awt.*;

@SuppressWarnings("unused")
public class VideoPlayer extends BasePlayer {
    private static final Marker IT = MarkerManager.getMarker("VideoPlayer");

    public VideoPlayer(MediaPlayerFactory factory, PlayerThread playerThread, RenderCallback renderCallback, SimpleBufferFormatCallback bufferFormatCallback) {
        super(factory, playerThread, renderCallback, bufferFormatCallback);
    }

    public synchronized Dimension getDimensions() {
        if (raw == null) return null;
        synchronized (this) {
            return raw.mediaPlayer().video().videoDimension();
        }
    }
}

