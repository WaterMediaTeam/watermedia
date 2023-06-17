package me.srrapero720.watermedia.api.video.players.events;

import me.srrapero720.watermedia.api.video.players.Player;
import me.srrapero720.watermedia.api.video.players.events.common.*;

public class EventManager<P extends Player> {
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


    public void callMediaFinishEvent(P player, MediaFinishEvent.EventData o) { if (mediaFinish != null) mediaFinish.call(player, o); }
    public void callMediaPauseEvent(P player, MediaPauseEvent.EventData o) { if (mediaPause != null) mediaPause.call(player, o); }
    public void callMediaResumeEvent(P player, MediaResumeEvent.EventData o) { if (mediaResume != null) mediaResume.call(player, o); }
    public void callMediaStoppedEvent(P player, MediaStoppedEvent.EventData o) { if (mediaStopped != null) mediaStopped.call(player, o); }
    public void callMediaTimeChangedEvent(P player, MediaTimeChangedEvent.EventData o) { if (mediaTimeChanged != null) mediaTimeChanged.call(player, o); }
    public void callMediaVolumeUpdate(P player, MediaVolumeUpdateEvent.EventData o) { if (mediaVolumeUpdate != null) mediaVolumeUpdate.call(player, o); }
    public void callPlayerBufferProgressEvent(P player, PlayerBuffer.EventProgressData o) { if (playerBufferStart != null) playerBufferStart.call(player, o); }
    public void callPlayerBufferEndEvent(P player, PlayerBuffer.EventEndData o) { if (playerBufferEnd != null) playerBufferEnd.call(player, o); }
    public void callPlayerExceptionEvent(P player, PlayerExceptionEvent.EventData o) { if (playerException != null) playerException.call(player, o); }
    public void callPlayerPreparingEvent(P player, PlayerPreparingEvent.EventData o) { if (playerPreparing != null) playerPreparing.call(player, o); }
    public void callPlayerReadyEvent(P player, PlayerReadyEvent.EventData o) { if (playerReady != null) playerReady.call(player, o); }
    public void callPlayerStartedEvent(P player, PlayerStartedEvent.EventData o) { if (playerStarted != null) playerStarted.call(player, o); }
}
