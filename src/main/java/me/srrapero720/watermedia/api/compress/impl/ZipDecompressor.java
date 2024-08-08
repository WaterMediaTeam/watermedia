package me.srrapero720.watermedia.api.compress.impl;

import me.srrapero720.watermedia.api.compress.spi.BaseDecompresor;

public class ZipDecompressor extends BaseDecompresor {

    @Override
    public int files() {
        return 0;
    }

    @Override
    public long size() {
        return 0;
    }

    @Override
    public int remaining() {
        return 0;
    }
}
