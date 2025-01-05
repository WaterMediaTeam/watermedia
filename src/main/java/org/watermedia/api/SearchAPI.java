package org.watermedia.api;

import org.watermedia.WaterMedia;

public class SearchAPI implements WaterMediaAPI {
    public static void search(String term) {

    }

    @Override
    public Priority priority() {
        return null;
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
