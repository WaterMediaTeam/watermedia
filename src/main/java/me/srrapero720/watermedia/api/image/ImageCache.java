package me.srrapero720.watermedia.api.image;

import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class ImageCache {
    private static final Marker IT = MarkerManager.getMarker("ImageCache");
    private static final Map<String, ImageCache> CACHE = new HashMap<>();

    public static ImageCache findOrCreate(String originalURL, RenderThread runnable) {
        ImageCache image = CACHE.get(originalURL);
        image = (image == null) ? new ImageCache(originalURL, runnable) : image.use();
        CACHE.put(originalURL, image);
        return image;
    }

    public static void reloadAll() {
        ImageCache[] loaded = CACHE.values().toArray(new ImageCache[0]);
        for (ImageCache imageCache : loaded) { imageCache.reload(); }
    }

    // INFO;
    public final String url;
    private final ImageFetch fetch;
    private final RenderThread renderThread;

    // STATUS
    private final AtomicBoolean video = new AtomicBoolean(false);
    private final AtomicInteger uses = new AtomicInteger(1);
    private volatile Status status = Status.WAITING;

    private volatile ImageRenderer renderer;
    private volatile Exception exception;

    public ImageCache(String url, RenderThread runnable) {
        this.url = url;
        this.renderThread = runnable;
        this.fetch = new ImageFetch(url);
        CACHE.put(url, this);
    }

    public ImageCache(ImageRenderer renderer) {
        this.url = null;
        this.fetch = null;
        this.renderThread = null;
        this.renderer = renderer;
    }

    public boolean isVideo() { return video.get(); }
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
                    synchronized (this) { this.renderer = imageRenderer; }
                    this.video.set(false);
                    this.exception = null;
                    this.status = Status.READY;
                }
            }).setOnFailedCallback(exception -> {
                synchronized (this) { this.renderer = null; }
                if (exception instanceof ImageFetch.VideoContentException) {
                    this.video.set(true);
                    this.exception = null;
                    this.status = Status.READY;
                } else {
                    this.exception = exception;
                    this.status = Status.FAILED;
                }
            }).start();
        }
    }

    public void reload() {
        if (fetch == null) return;
        synchronized (fetch) {
            if (!this.status.equals(Status.READY) && !this.status.equals(Status.FAILED)) return; // ONLY READY CACHE CAN BE RELOADED
            this.status = Status.WAITING;
            this.video.set(false);
            this.exception = null;
            this.renderThread.askForExecution(() -> {
                this.renderer.release();
                // ENSURE IS THE SAME FUCKING RENDERER AND IF WAS RELEASED
                synchronized (fetch) {
                    if (status.equals(Status.FORGOTTEN))
                        synchronized (this) { if ( renderer.textures[0] == -1) this.renderer = null; }
                }
            });
        }
    }

    public void release() {
        if (fetch == null) return;
        synchronized (fetch) {
            if (!this.status.equals(Status.READY)) return; // ONLY READY CACHE CAN BE RELEASED
            this.status = Status.FORGOTTEN;
            this.video.set(false);
            this.exception = null;
            this.uses.set(0);
            this.renderThread.askForExecution(() -> {
                this.renderer.release();
                synchronized (this) { if ( renderer.textures[0] == -1) this.renderer = null; }
            });
            CACHE.remove(url);
        }
    }

    public enum Status { WAITING, LOADING, READY, FORGOTTEN, FAILED; }
    public interface RenderThread { void askForExecution(Runnable runnable); }
}
