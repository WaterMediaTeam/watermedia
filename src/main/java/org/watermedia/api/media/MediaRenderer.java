
package org.watermedia.api.media;

import org.watermedia.tools.DataTool;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

public abstract class MediaRenderer {
    public static final long NO_DURATION = -1;

    public final int width;
    public final int height;
    public long duration = NO_DURATION; // in MS

    public MediaRenderer(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public abstract boolean start();

    public abstract boolean startPaused();

    public abstract boolean resume();

    public abstract boolean pause();

    public abstract boolean pause(boolean paused);

    public abstract boolean stop();

    public abstract boolean togglePlay();

    public abstract boolean seek(long time);

    public abstract boolean seekQuick(long time);

    public abstract boolean foward();

    public abstract boolean rewind();

    public abstract boolean speed(float speed);

    public abstract boolean repeaat();

    public abstract boolean repeaat(boolean repeat);

    // status
    public abstract boolean usable();

    public abstract boolean loading();

    public abstract boolean buffering();

    public abstract boolean ready();

    public abstract boolean paused();

    public abstract boolean playing();

    public abstract boolean stopped();

    public abstract boolean ended();

    public abstract boolean validSource();

    public abstract boolean liveSource();

    public abstract boolean canSeek();

    public abstract long duration();

    public abstract long time();

    public abstract void release();
    /**
     * Provides the active texture ID of the media
     * Images can calculate the time by their own
     * @return OpenGL texture id
     */
    public abstract int texture();



    public abstract class Video extends MediaRenderer {



        @Override
        public int texture() {
            return 0;
        }
    }

    public abstract class Image extends MediaRenderer {
        public final int[] textures;
        private final ByteBuffer[] images;

        // Clock calculation
        private long currentMS;
        private long marginTime = -1;
        private long[] delays;

        public Image(BufferedImage image) {
            super(image.getWidth(), image.getHeight());
            this.duration = 0;
        }

        public Image(ByteBuffer buffer, int width, int height, long duration) {
            super(width, height);
        }

        public Image(ByteBuffer[] buffers, int width, int height, long[] delays) {
            super(width, height);
            this.images = buffers;
            this.delays = delays;
            this.duration = DataTool.sumArray(delays);
        }

        @Override
        public int texture() {
            if (marginTime == -1) {
                currentMS = marginTime = System.currentTimeMillis();
            }
            long start = (currentMS - marginTime);
            long end = System.currentTimeMillis() - marginTime;
            long time = end - start;

            this.currentMS = time;

            return textureInTime(time);
        }

        private int textureInTime(long time) {
            // if (textures == null) return 0;
            // if (textures.length == 0) return texture(0);
            for (int i = 0; i < delays.length; i++) {
                time -= delays[i];
                if (time <= 0)
                    return texture(i);
            }
            return texture(/*images.length - 1*/);
        }

        private int texture(int index) {
            return ;
        }
    }
}
