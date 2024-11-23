package me.srrapero720.watermedia.api.player.impl;

import me.srrapero720.watermedia.api.player.IMediaPlayer;
import me.srrapero720.watermedia.api.player.IVideoPlayer;

import java.nio.ByteBuffer;

public class FFmpegVideoPlayer extends FFmpegPlayer implements IVideoPlayer {
    @Override
    public int width() {
        return 0;
    }

    @Override
    public int height() {
        return 0;
    }

    @Override
    public ByteBuffer textureBuffer() {
        return null;
    }

    @Override
    public int prepare() {
        return 0;
    }

    @Override
    public int texture() {
        return 0;
    }
}
