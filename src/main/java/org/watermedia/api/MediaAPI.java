package org.watermedia.api;

import me.srrapero720.watermedia.api.MediaContext;
import org.watermedia.WaterMedia;
import org.watermedia.api.media.*;
import org.watermedia.api.network.MRL;
import org.watermedia.api.network.MediaRequest;
import org.watermedia.videolan4j.binding.internal.libvlc_instance_t;

public class MediaAPI implements WaterMediaAPI {

    public static MediaRequest load(MRL mrl, MediaContext context, MediaPlayer.Quality quality) {
        return new MediaRequest(mrl, context, quality);
    }

    public static boolean ffmpeg() {
        return false; // TODO: implement
    }

    public static boolean videolan() {
        return false; // TODO: implement
    }

    public static libvlc_instance_t videoLanFactory() {
        return null; // TODO: implement
    }


    @Override
    public Priority priority() {
        return Priority.NORMAL;
    }

    @Override
    public boolean prepare(WaterMedia.ILoader loader) throws Exception {
        return false;
    }

    @Override
    public void start(WaterMedia.ILoader loader) throws Exception {

    }

    @Override
    public void release() {
    }
}