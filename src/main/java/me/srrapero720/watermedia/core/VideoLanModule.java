package me.srrapero720.watermedia.core;

import me.srrapero720.watermedia.api.loader.IMediaLoader;
import me.srrapero720.watermedia.api.loader.IModuleBootstrap;

public class VideoLanModule extends IModuleBootstrap {
    public VideoLanModule(IMediaLoader loader) {
        super(loader);
    }

    @Override
    public boolean boot() {
        return false;
    }

    @Override
    public void init() throws Exception {

    }

    @Override
    public void release() {

    }
}