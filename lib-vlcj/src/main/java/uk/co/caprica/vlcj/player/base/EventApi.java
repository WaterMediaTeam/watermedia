/*
 * This file is part of VLCJ.
 *
 * VLCJ is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * VLCJ is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with VLCJ.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright 2009-2019 Caprica Software Limited.
 */

package uk.co.caprica.vlcj.player.base;

import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import uk.co.caprica.vlcj.VideoLan4J;
import uk.co.caprica.vlcj.media.MediaEventListener;
import uk.co.caprica.vlcj.player.base.events.MediaPlayerEvent;

/**
 * Behaviour pertaining to media player events.
 */
public final class EventApi extends BaseApi {

    private final MediaPlayerNativeEventManager eventManager;

    EventApi(MediaPlayer mediaPlayer) {
        super(mediaPlayer);

        eventManager = new MediaPlayerNativeEventManager(libvlcInstance, mediaPlayer);

        // Add event handlers used for internal implementation (ordering here is important)
        addMediaPlayerEventListener(new ResetMediaEventHandler      ());
        addMediaPlayerEventListener(new RepeatPlayEventHandler      ());
        addMediaPlayerEventListener(new MediaPlayerReadyEventHandler());
    }

    /**
     * Add a component to be notified of media player events.
     *
     * @param listener component to notify
     */
    public void addMediaPlayerEventListener(MediaPlayerEventListener listener) {
        // WATERMeDIA's PATCH - start
        // This is needed because using events can be a pain in the ass
        if (!listener.getClass().getSimpleName().equals("WaterMediaPlayerEventListener")) {
            final Marker it = MarkerManager.getMarker("WATERMeDIA");
            VideoLan4J.LOGGER.warn(it,
                    "Adding custom events without know about concurrency can lead in unexpected behaviors and JVM crashes or deadlocks");
            VideoLan4J.LOGGER.warn(it, "consider use WATERMeDIA to avoid any issue.");
        }
        // WATERMeDIA's PATCH - end
        eventManager.addEventListener(listener);
    }

    /**
     * Remove a component that was previously interested in notifications of media player events.
     *
     * @param listener component to stop notifying
     */
    public void removeMediaPlayerEventListener(MediaPlayerEventListener listener) {
        eventManager.removeEventListener(listener);
    }

    /**
     * Add a component to be notified of media events.
     * <p>
     * As the current media changes, this listener will automatically be removed from the previous media and added to
     * the new.
     *
     * @param listener component to notify
     */
    public void addMediaEventListener(MediaEventListener listener) {
        mediaPlayer.media().addPersistentMediaEventListener(listener);
    }

    /**
     * Remove a component that was previously interested in notifications of media events.
     *
     * @param listener component to stop notifying
     */
    public void removeMediaEventListener(MediaEventListener listener) {
        mediaPlayer.media().removePersistentMediaEventListener(listener);
    }

    void raiseEvent(MediaPlayerEvent event) {
        eventManager.raiseEvent(event);
    }

    @Override
    protected void release() {
        eventManager.release();
    }

}