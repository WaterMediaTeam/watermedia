package me.srrapero720.watermedia.api.player;

import com.sun.jna.Pointer;
import me.lib720.caprica.vlcj.binding.support.runtime.RuntimeUtil;
import me.lib720.caprica.vlcj.factory.MediaPlayerFactory;
import me.lib720.caprica.vlcj.media.InfoApi;
import me.lib720.caprica.vlcj.media.MediaRef;
import me.lib720.caprica.vlcj.media.MediaType;
import me.lib720.caprica.vlcj.media.TrackType;
import me.lib720.caprica.vlcj.player.base.MediaPlayer;
import me.lib720.caprica.vlcj.player.base.MediaPlayerEventListener;
import me.lib720.caprica.vlcj.player.base.State;
import me.lib720.caprica.vlcj.player.base.callback.AudioCallback;
import me.lib720.caprica.vlcj.player.component.CallbackMediaPlayerComponent;
import me.lib720.caprica.vlcj.player.embedded.videosurface.callback.BufferFormatCallback;
import me.lib720.caprica.vlcj.player.embedded.videosurface.callback.RenderCallback;
import me.lib720.caprica.vlcj.player.embedded.videosurface.callback.SimpleBufferFormatCallback;
import me.srrapero720.watermedia.api.WaterMediaAPI;
import me.srrapero720.watermedia.api.player.events.*;
import me.srrapero720.watermedia.core.VideoLAN;
import me.lib720.watermod.ThreadCore;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static me.srrapero720.watermedia.WaterMedia.LOGGER;

@SuppressWarnings("unused")
public class VideoPlayer extends AbstractPlayer {
    private static final Marker IT = MarkerFactory.getMarker("VideoPlayer");

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

