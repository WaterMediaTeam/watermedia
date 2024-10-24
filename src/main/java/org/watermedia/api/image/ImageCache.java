package org.watermedia.api.image;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import static org.watermedia.WaterMedia.LOGGER;

public class ImageCache {
    static final Map<URI, ImageCache> CACHE = new HashMap<>();

    /**
     * @nullable
     */
    public final URI uri;
    private final ImageFetch fetch;
    private final Executor renderThreadEx;
    private final AtomicInteger uses = new AtomicInteger(1);

    // STATUS
    private volatile Status status = Status.WAITING;
    private volatile boolean video = false;
    private volatile boolean cache = false;

    private volatile ImageRenderer renderer;
    private volatile Exception exception;

    private final List<Consumer<ImageRenderer>> releaseListeners = new ArrayList<>();

    ImageCache(URI uri, Executor runnable) {
        this.uri = uri;
        this.renderThreadEx = runnable;
        this.fetch = new ImageFetch(uri);
        CACHE.put(uri, this);
    }

    ImageCache(ImageRenderer renderer) {
        this.uri = URI.create("water://media/");
        this.fetch = null;
        this.renderThreadEx = null;
        this.renderer = renderer;
    }

    public boolean isCache() { return cache; }
    public boolean isVideo() { return video; }
    public boolean isUsed() { return uses.get() > 0; }
    public ImageCache use() { uses.incrementAndGet(); return this; }
    public ImageCache deuse() {
        if (uses.decrementAndGet() <= 0) release();
        return this;
    }

    /**
     * Calls to {@link ImageRenderer#flush()} when only have one usage in a safety way
     * @return self
     */
    public ImageCache flush() {
        if (uses.get() == 1 && this.renderer != null && !this.renderer.isFlushed()) {
            renderer.flush();
        }
        return this;
    }

    /**
     * Calls to {@link ImageRenderer#flush()} when only have one usage in a safety way
     * @return self
     */
    public ImageCache reset() {
        if (uses.get() == 1 && this.renderer != null && this.renderer.isFlushed()) {
            renderer.reset();
        }
        return this;
    }

    public int getUsages() { return uses.get(); }
    public Status getStatus() { return status; }
    public Exception getException() { return exception; }
    public ImageRenderer getRenderer() {
        if (this.isVideo()) {
            return ImageAPI.failedVLC();
        }
        return renderer;
    }

    public ImageCache addReleaseCallback(Consumer<ImageRenderer> consumer) {
        this.releaseListeners.add(consumer);
        return this;
    }

    public void load() {
        if (fetch == null) return;
        synchronized (fetch) {
            if (!status.equals(Status.WAITING)) return;
            this.status = Status.LOADING;
            fetch.setSuccessCallback((imageRenderer, isCache) -> {
                synchronized (fetch) {
                    if (!this.status.equals(Status.LOADING)) {
                        renderThreadEx.execute(imageRenderer::release);
                        return;
                    }
                    this.renderer = imageRenderer;
                    this.cache = isCache;
                    this.video = false;
                    this.exception = null;
                    this.status = Status.READY;
                }
            }).setErrorCallback((exception, isVideo) -> {
                synchronized (fetch) {
                    this.renderer = null;
                    if (!this.status.equals(Status.LOADING)) return;
                    if (isVideo) {
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
            this.exception = null;

            ImageRenderer imageRenderer = this.renderer;
            this.renderer = null;
            if (imageRenderer != null) this.renderThreadEx.execute(imageRenderer::release);
        }
    }

    public void release() {
        if (fetch == null) return;
        synchronized (fetch) {
            if (uses.get() > 0) {
                LOGGER.warn(ImageAPI.IT, "Cache of '{}' is released with {} usages remaining", this.uri, this.uses.get());
            }

            ImageRenderer imageRenderer = this.renderer;
            this.renderer = null;
            if (imageRenderer != null) {
                this.releaseListeners.forEach(consumer -> consumer.accept(imageRenderer));
                this.renderThreadEx.execute(imageRenderer::release);
            }
            this.status = Status.FORGOTTEN;
            CACHE.remove(uri);
        }
    }

    public enum Status { WAITING, LOADING, READY, FORGOTTEN, FAILED; }
}