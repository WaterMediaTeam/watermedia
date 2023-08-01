package me.srrapero720.watermedia.api.images;

import me.srrapero720.watermedia.api.WaterMediaAPI;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static me.srrapero720.watermedia.WaterMedia.LOGGER;

public class ImageCache {
    private static final Marker IT = MarkerFactory.getMarker("ImageCache");
    private static final Map<String, ImageCache> CACHE = new HashMap<>();

    public static ImageCache findOrCreate(URL url, AsyncRunnable runnable) {
        ImageCache image = CACHE.get(url.toString());
        image = (image == null) ? new ImageCache(url, runnable) : image.use();
        CACHE.put(url.toString(), image);
        return image;
    }

    // INFO
    public final URL url;
    private final ImageFetch fetch;
    private final AsyncRunnable asyncRunnable;

    // STATUS
    private Status status = Status.WAITING;
    private final AtomicBoolean video = new AtomicBoolean(false);
    private final AtomicInteger uses = new AtomicInteger(1);

    private ImageRenderer renderer;
    private Exception exception;

    public ImageCache(String url, AsyncRunnable runnable) {
        this(WaterMediaAPI.url_toURL(url), runnable);
    }

    public ImageCache(URL url, AsyncRunnable runnable) {
        this.url = url;
        this.asyncRunnable = runnable;
        this.fetch = new ImageFetch(url);
        CACHE.put(url.toString(), this);
    }

    public ImageCache(ImageRenderer renderer) {
        this.url = null;
        this.fetch = null;
        this.asyncRunnable = null;
        this.renderer = renderer;
    }

    public boolean isVideo() { return video.get(); }
    public boolean isUsed() { return uses.get() > 0; }
    public ImageCache use() { uses.getAndIncrement(); return this; }
    public ImageCache deuse() { uses.decrementAndGet(); return this; }
    public URL getUrl() { return url; }
    public Status getStatus() { return status; }
    public Exception getException() { return exception; }
    public ImageRenderer getRenderer() { return renderer; }



    public synchronized void load() {
        if (fetch == null) return;
        synchronized (fetch) {
            if (!status.equals(Status.WAITING)) return;
            this.status = Status.LOADING;
            fetch.setOnSuccessCallback(imageRenderer -> {
                synchronized (fetch) {
                    if (!this.status.equals(Status.LOADING)) {
                        imageRenderer.release(); // IS SAFE DO THAT WHEN ANY TEXT PICTURE ISN'T GENERATED
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
                } else {
                    this.exception = exception;
                }
                this.status = Status.FAILED;
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
            this.asyncRunnable.askForExecution(() -> {
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
            this.asyncRunnable.askForExecution(() -> {
                this.renderer.release();
                synchronized (this) { if ( renderer.textures[0] == -1) this.renderer = null; }
            });
            CACHE.remove(url.toString());
        }
    }

    public enum Status { WAITING, LOADING, READY, FORGOTTEN, FAILED; }
    public interface AsyncRunnable { void askForExecution(Runnable runnable); }
}
