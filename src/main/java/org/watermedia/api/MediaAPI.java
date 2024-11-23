package org.watermedia.api;

import org.watermedia.WaterMedia;
import org.watermedia.api.media.ImageSource;
import org.watermedia.api.media.MediaSource;
import org.watermedia.api.network.MRL;

import java.util.ArrayList;
import java.util.List;

public class MediaAPI extends WaterMediaAPI {

    @Override
    public Priority priority() {
        return Priority.NORMAL;
    }

    @Override
    public boolean prepare(WaterMedia.ILoader bootCore) throws Exception {
        return false;
    }

    @Override
    public void start(WaterMedia.ILoader bootCore) throws Exception {

    }

    @Override
    public void release() {
    }
}
