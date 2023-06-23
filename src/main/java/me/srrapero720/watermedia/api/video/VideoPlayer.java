package me.srrapero720.watermedia.api.video;

import me.srrapero720.watermedia.api.WaterMediaAPI;
import me.srrapero720.watermedia.api.video.events.common.*;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public abstract class VideoPlayer {
    protected String url;
    public VideoPlayer() {}

    protected void compat(String url) {
        var compat = WaterMediaAPI.patchUrl(url);
        if (compat != null) this.url = compat;
        else this.url = url;
    }

    public void start(@NotNull CharSequence url) { compat(url.toString()); }
    public void prepare(@NotNull CharSequence url) { compat(url.toString()); }
    public abstract void play();
    public abstract void pause();
    public abstract void setPauseMode(boolean isPaused);
    public abstract void stop();


    /* seekTo */
    public abstract void seekTo(long time);
    public abstract void seekFastTo(long ticks);
    public abstract void seekGameTicksTo(int ticks);
    public abstract void seekGameTickFastTo(int ticks);

    public abstract long getDuration();
    public abstract int getGameTickDuration();

    public abstract long getTime();

    public abstract int getGameTickTime();

    public abstract boolean isSeekable();
    public abstract void setRepeatMode(boolean repeatMode);
    public abstract boolean getRepeatMode();

    public abstract boolean isPlaying();
    public abstract boolean isValid();

    public abstract Dimension getDimensions();

    public abstract void setSpeed(float rate);
    public abstract void fastFoward();
    public abstract void rewind();
    public synchronized void fastBackward() { rewind(); }

    public abstract void setVolume(int volume);
    public abstract int getVolume();

    public abstract void mute();

    public abstract void unmute();

    public abstract void setMuteMode(boolean mode);

    public abstract boolean isStream();

    public abstract void release();

    public static class EventManager<P extends VideoPlayer> {
        private MediaFinishEvent<P> mediaFinish = null;
        private MediaPauseEvent<P> mediaPause = null;
        private MediaResumeEvent<P> mediaResume = null;
        private MediaStoppedEvent<P> mediaStopped = null;
        private MediaTimeChangedEvent<P> mediaTimeChanged = null;
        private MediaVolumeUpdateEvent<P> mediaVolumeUpdate = null;
        private PlayerBuffer.ProgressEvent<P> playerBufferStart = null;
        private PlayerBuffer.EndEvent<P> playerBufferEnd = null;
        private PlayerExceptionEvent<P> playerException = null;
        private PlayerPreparingEvent<P> playerPreparing = null;
        private PlayerReadyEvent<P> playerReady = null;
        private PlayerStartedEvent<P> playerStarted = null;

        public void setMediaFinishEvent(MediaFinishEvent<P> ev) { mediaFinish = ev; }
        public void setMediaPauseEvent(MediaPauseEvent<P> ev) { mediaPause = ev; }
        public void setMediaResumeEvent(MediaResumeEvent<P> ev) { mediaResume = ev; }
        public void setMediaStoppedEvent(MediaStoppedEvent<P> ev) { mediaStopped = ev; }
        public void setMediaTimeChangedEvent(MediaTimeChangedEvent<P> ev) { mediaTimeChanged = ev; }
        public void setMediaVolumeUpdate(MediaVolumeUpdateEvent<P> ev) { mediaVolumeUpdate = ev; }
        public void setPlayerBufferProgressEvent(PlayerBuffer.ProgressEvent<P> ev) { playerBufferStart = ev; }
        public void setPlayerBufferEndEvent(PlayerBuffer.EndEvent<P> ev) { playerBufferEnd = ev; }
        public void setPlayerExceptionEvent(PlayerExceptionEvent<P> ev) { playerException = ev; }
        public void setPlayerPreparingEvent(PlayerPreparingEvent<P> ev) { playerPreparing = ev; }
        public void setPlayerReadyEvent(PlayerReadyEvent<P> ev) { playerReady = ev; }
        public void setPlayerStartedEvent(PlayerStartedEvent<P> ev) { playerStarted = ev; }


        protected void callMediaFinishEvent(P player, MediaFinishEvent.EventData o) { if (mediaFinish != null) mediaFinish.call(player, o); }
        protected void callMediaPauseEvent(P player, MediaPauseEvent.EventData o) { if (mediaPause != null) mediaPause.call(player, o); }
        protected void callMediaResumeEvent(P player, MediaResumeEvent.EventData o) { if (mediaResume != null) mediaResume.call(player, o); }
        protected void callMediaStoppedEvent(P player, MediaStoppedEvent.EventData o) { if (mediaStopped != null) mediaStopped.call(player, o); }
        protected void callMediaTimeChangedEvent(P player, MediaTimeChangedEvent.EventData o) { if (mediaTimeChanged != null) mediaTimeChanged.call(player, o); }
        protected void callMediaVolumeUpdate(P player, MediaVolumeUpdateEvent.EventData o) { if (mediaVolumeUpdate != null) mediaVolumeUpdate.call(player, o); }
        protected void callPlayerBufferProgressEvent(P player, PlayerBuffer.EventProgressData o) { if (playerBufferStart != null) playerBufferStart.call(player, o); }
        protected void callPlayerBufferEndEvent(P player, PlayerBuffer.EventEndData o) { if (playerBufferEnd != null) playerBufferEnd.call(player, o); }
        protected void callPlayerExceptionEvent(P player, PlayerExceptionEvent.EventData o) { if (playerException != null) playerException.call(player, o); }
        protected void callPlayerPreparingEvent(P player, PlayerPreparingEvent.EventData o) { if (playerPreparing != null) playerPreparing.call(player, o); }
        protected void callPlayerReadyEvent(P player, PlayerReadyEvent.EventData o) { if (playerReady != null) playerReady.call(player, o); }
        protected void callPlayerStartedEvent(P player, PlayerStartedEvent.EventData o) { if (playerStarted != null) playerStarted.call(player, o); }
    }
}
