package me.srrapero720.watermedia.api.player;

import java.nio.ByteBuffer;

public interface IVideoPlayer extends IAudioPlayer, IMediaPlayer {

    int width();

    int height();

    ByteBuffer textureBuffer();

    int prepare();

    int texture();
}
