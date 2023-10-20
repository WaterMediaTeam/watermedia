package me.srrapero720.watermedia.api.loader;

public interface IModuleLoader {
    void init(IMediaLoader loader) throws Exception;
    void release();
}