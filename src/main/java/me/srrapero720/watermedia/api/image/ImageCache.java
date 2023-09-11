package me.srrapero720.watermedia.api.image;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;

public class ImageCache {
    static final Map<String, ImageCache> CACHE = new HashMap<>();
    static final ImageCache EMPTY_INSTANCE = new ImageCache(null);

    /**
     * Gets a cache for a URL
     * if no exists then creates an unready one
     * @param originalURL url of the picture
     * @param renderThreadEx concurrent executor
     * @deprecated use instead {@link ImageAPI#getCache(String, Executor)}
     * @return cache instance
     */
    @Deprecated
    public static ImageCache get(String originalURL, Executor renderThreadEx) {
        return ImageAPI.getCache(originalURL, renderThreadEx);
    }

    /**
     * Reloads all ImageCache instanced
     * This might cause lag
     * @deprecated use instead {@link ImageAPI#reloadCache()}
     */
    @Deprecated
    public static void reloadAll() {
        for (ImageCache imageCache : CACHE.values()) { imageCache.reload(); }
    }

    // INFO;
    public final String url;
    private final ImageFetch fetch;
    private final Executor renderThreadEx;

    // STATUS
    private volatile boolean video = false;
    private final AtomicInteger uses = new AtomicInteger(1);
    private volatile Status status = Status.WAITING;

    private volatile ImageRenderer renderer;
    private volatile Exception exception;

    @Deprecated
    public ImageCache(String url, Executor runnable) {
        this.url = url;
        this.renderThreadEx = runnable;
        this.fetch = new ImageFetch(url);
        CACHE.put(url, this);
    }

    @Deprecated
    public ImageCache(ImageRenderer renderer) {
        this.url = "";
        this.fetch = null;
        this.renderThreadEx = null;
        this.renderer = renderer;
    }

    public boolean isVideo() { return video; }
    public boolean isUsed() { return uses.get() > 0; }
    public ImageCache use() { uses.getAndIncrement(); return this; }
    public ImageCache deuse() {
        uses.decrementAndGet();
        if (uses.get() <= 0) release();
        return this;
    }

    public Status getStatus() { return status; }
    public Exception getException() { return exception; }
    public ImageRenderer getRenderer() { return renderer; }

    public void load() {
        if (fetch == null) return;
        synchronized (fetch) {
            if (!status.equals(Status.WAITING)) return;
            this.status = Status.LOADING;
            fetch.setOnSuccessCallback(imageRenderer -> {
                synchronized (fetch) {
                    if (!this.status.equals(Status.LOADING)) {
                        imageRenderer.release(); // IS SAFE DO THAT WHEN ANY TEX PICTURE ISN'T GENERATED
                        return;
                    }
                    this.renderer = imageRenderer;
                    this.video = false;
                    this.exception = null;
                    this.status = Status.READY;
                }
            }).setOnFailedCallback(exception -> {
                synchronized (fetch) {
                    this.renderer = null;
                    if (!this.status.equals(Status.LOADING)) return;
                    if (exception instanceof ImageFetch.NoPictureException) {
                        this.video = true;
                        this.exception = null;
                        this.status = Status.READY;
                    } else {
                        this.exception = exception;
                        this.status = Status.FAILED;
                    }
                }
            }).start();
        }
    }

    public void reload() {
        if (fetch == null) return;
        synchronized (fetch) {
            if (!this.status.equals(Status.READY) && !this.status.equals(Status.FAILED)) return; // ONLY READY OR FAILED CACHE CAN BE RELOADED
            this.status = Status.WAITING;
            this.video = false;
            this.exception = null;

            ImageRenderer imageRenderer = this.renderer;
            this.renderer = null;
            if (imageRenderer != null) this.renderThreadEx.execute(imageRenderer::release);
        }
    }

    public void release() {
        if (fetch == null) return;
        synchronized (fetch) {
            this.status = Status.FORGOTTEN;
            this.video = false;
            this.exception = null;

            ImageRenderer imageRenderer = this.renderer;
            this.renderer = null;
            if (renderer != null) this.renderThreadEx.execute(imageRenderer::release);
            CACHE.remove(url);
        }
    }

    public enum Status { WAITING, LOADING, READY, FORGOTTEN, FAILED; }
}