
package org.watermedia.api.media;

public abstract class MediaRenderer {
    public final int width;
    public final int height;

    public MediaRenderer(int width, int height) {
        this.width = width;
        this.height = height;
    }


}
