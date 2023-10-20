package me.srrapero720.watermedia.api.loader;

public abstract class IModuleBootstrap {
    public IModuleBootstrap(IMediaLoader loader) {

    }

    public abstract boolean boot();
    public abstract void init() throws Exception;
    public abstract void release();
}