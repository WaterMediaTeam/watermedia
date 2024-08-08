package me.srrapero720.watermedia.api.compress.spi;

public interface IDecompressor {
    int files();
    long size();
    int remaining();
}
