package me.srrapero720.watermedia.api.images;

import me.srrapero720.watermedia.api.WaterMediaAPI;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static me.srrapero720.watermedia.WaterMedia.LOGGER;

public class PictureManager {
    private static final Map<String, RenderablePicture> CACHE = new HashMap<>();
    private static final Marker IT = MarkerFactory.getMarker("PictureManager");

    /**
     * Find any registered {@link RenderablePicture}.
     * <br>
     * <strong>IMPORTANT:</strong> after any CACHE update if any {@link RenderablePicture} instance
     * have 0 uses then it got released and deleted.
     * <br><br>
     * After you end using it use {@link RenderablePicture#deuse()} to clear your usage
     * @return cached renderable picture, null if not exists
     */
    public static RenderablePicture findAndUse(String url) {
        synchronized (CACHE) {
            // SEARCH ON BASE CACHE
            RenderablePicture result = CACHE.get(url);
            if (result != null) {
                result.use();
                return result;
            }

            return null;
        }
    }

    /**
     * Find any registered {@link RenderablePicture}.
     * <br>
     * <strong>IMPORTANT:</strong> after any CACHE update if any {@link RenderablePicture} instance
     * have 0 uses then it got released and deleted.
     * <br><br>
     * After you end using it use {@link RenderablePicture#deuse()} to clear your usage
     * <br><br>
     * The big difference between {@link PictureManager#findAndUse(String)} is this method if can't found cached renderables
     * returns instead {@link WaterMediaAPI#LOADING_GIF}
     * @return cached renderable picture, null if not exists
     */
    public static RenderablePicture findAndUseSafe(String url) {
        synchronized (CACHE) {
            RenderablePicture result = findAndUse(url);
            if (result != null) return result;
            return WaterMediaAPI.LOADING_GIF;
        }
    }

    synchronized static void addEntry(String url, RenderablePicture entry) {
        synchronized (CACHE) {
            List<String> toDelete = new ArrayList<>();
            CACHE.forEach((s, renderablePicture) -> {
                if (!renderablePicture.isUsed()) {
                    toDelete.add(s);
                    renderablePicture.release();
                }
            });

            for (String key: toDelete) CACHE.remove(key);
            CACHE.put(url, entry);
        }
    }


    /**
     * Reloads EVERY SINGLE PICTURE stored on this manager, ignoring if is used or not
     * CAREFUL: Unexpected reloading can cause a broken state in other mods. ensure you can handle possible crashes
     */
    public synchronized static void reload() {

    }

    /**
     * Releases EVERY SINGLE PICTURE stored on this manager ignoring if is used or not
     * CAREFUL: in case you are using WATERMeDIA with more dependants PLEASE make you sure all are calling when doesn't require pictures
     * MINECRAFT SUGGESTION: Handle world unloading event.
     */
    public synchronized static void release() {
        synchronized (CACHE) {
            if (CACHE.isEmpty()) return;
            LOGGER.warn(IT, "Releasing cached renderables");
            CACHE.forEach((s, renderablePicture) -> renderablePicture.release());
            CACHE.clear();
        }
    }
}