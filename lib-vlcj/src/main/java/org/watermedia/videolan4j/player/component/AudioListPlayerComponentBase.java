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

package org.watermedia.videolan4j.player.component;

import org.watermedia.videolan4j.media.MediaRef;
import org.watermedia.videolan4j.medialist.MediaList;
import org.watermedia.videolan4j.medialist.MediaListEventListener;
import org.watermedia.videolan4j.player.list.MediaListPlayer;
import org.watermedia.videolan4j.player.list.MediaListPlayerEventListener;
import org.watermedia.videolan4j.factory.MediaPlayerFactory;

/**
 * Base implementation for an audio list player.
 * <p>
 * This class serves to keep the {@link AudioListPlayerComponent} concrete implementation clean and un-cluttered.
 */
abstract class AudioListPlayerComponentBase extends AudioPlayerComponent implements MediaListPlayerEventListener, MediaListEventListener {

    /**
     * Create a media player component.
     *
     * @param mediaPlayerFactory factory used to create the component
     */
    protected AudioListPlayerComponentBase(MediaPlayerFactory mediaPlayerFactory) {
        super(mediaPlayerFactory);
    }

    // === MediaListPlayerEventListener =========================================

    @Override
    public void mediaListPlayerFinished(MediaListPlayer mediaListPlayer) {
    }

    @Override
    public void nextItem(MediaListPlayer mediaListPlayer, MediaRef item) {
    }

    @Override
    public void stopped(MediaListPlayer mediaListPlayer) {
    }

    // === MediaListEventListener ===============================================

    @Override
    public void mediaListWillAddItem(MediaList mediaList, MediaRef item, int index) {
    }

    @Override
    public void mediaListItemAdded(MediaList mediaList, MediaRef item, int index) {
    }

    @Override
    public void mediaListWillDeleteItem(MediaList mediaList, MediaRef item, int index) {
    }

    @Override
    public void mediaListItemDeleted(MediaList mediaList, MediaRef item, int index) {
    }

    @Override
    public void mediaListEndReached(MediaList mediaList) {
    }

}
