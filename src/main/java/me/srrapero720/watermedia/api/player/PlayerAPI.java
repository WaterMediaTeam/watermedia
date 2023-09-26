package me.srrapero720.watermedia.api.player;

import me.srrapero720.watermedia.core.VideoLanCore;
import uk.co.caprica.vlcj.factory.MediaPlayerFactory;

public class PlayerAPI {

    /**
     * Check if PlayerAPI and/or VLC is loaded and ready to be used.
     * Some modules cannot be loaded in some OS, in that case WATERMeDIA can address it and
     * keep still working
     * @return if PlayerAPI and/or VLC was loaded
     */
    public static boolean isReady() { return VideoLanCore.factory() != null; }

    /**
     * Gives you the default VLC MediaPlayerFactory created by API
     * @return WATERMeDIA's default MediaPlayerFactory
     */
    public static MediaPlayerFactory getVLCFactory() { return VideoLanCore.factory(); }
}