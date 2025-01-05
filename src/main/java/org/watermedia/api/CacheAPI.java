package org.watermedia.api;

import me.srrapero720.watermedia.core.cache.CacheCore;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.watermedia.WaterMedia;
import org.watermedia.api.cache.CacheEntry;

import java.io.*;
import java.net.URI;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import static org.watermedia.WaterMedia.LOGGER;

public class CacheAPI implements WaterMediaAPI {
    private static final Marker IT = MarkerManager.getMarker("CacheAPI");
    public static final Map<URI, CacheEntry> CACHE = new HashMap<>();

    private static File dir;
    private static File index;
    private static boolean started = false;

    // TODO: accept the file writter
    public static void register(URI uri, CacheEntry entry) {
        assertIsStarted();
        synchronized (CACHE) {
            CacheEntry old = CACHE.put(uri, entry);
            if (old != null) {
                LOGGER.warn(IT, "Cache index for '{}' was replaced, cache is expired?", old);
            }
        }
    }

    public static boolean isCached(URI uri) {
        assertIsStarted();
        synchronized (CACHE) {
            CacheEntry entry = CACHE.get(uri);
            if (entry == null) return false;
            // IF IS EXPIRED, CONSIDER IT AS NOT CACHED
            if (System.currentTimeMillis() < entry.expirationDate().getTime()) {
                CACHE.remove(uri);
                return false;
            }
            return true;
        }
    }

    public static File getCacheFile(URI uri) {
        assertIsStarted();
        synchronized (CACHE) {
            CacheEntry entry = CACHE.get(uri);
            if (entry == null)
                throw new NullPointerException("There's no cache entry for '" + uri + "'");

            return dir.toPath().resolve(entry.file() + "." + entry.ext()).toFile();
        }
    }

    public static InputStream getCacheStream(URI uri) throws IOException {
        return new FileInputStream(getCacheFile(uri));
    }

    private static void assertIsStarted() {
        if (!started)
            throw new IllegalStateException("CacheAPI is not started yet!");
    }

    @Override
    public Priority priority() {
        return Priority.HIGHEST;
    }

    @Override
    public boolean prepare(WaterMedia.ILoader loader) throws Exception {
        dir = loader.tmp().resolve("cache").toFile();
        index = new File(dir, "index");
        return true; // TODO: make cache disable-able
    }

    @Override
    public void start(WaterMedia.ILoader loader) throws Exception {
        if (!dir.exists() && !dir.mkdirs())
            throw new IOException("Failed to create cache directories");

        LOGGER.info(IT, "Loading entries...");
        if (index.exists()) {
            try (DataInputStream in = new DataInputStream(new GZIPInputStream(Files.newInputStream(index.toPath())))) {
                while (in.available() != 0) {
                    CacheEntry entry = CacheEntry.read(in);
                    CACHE.put(entry.uri(), entry);
                }
            }
        }
        started = true;
        LOGGER.info(IT, "Successfully loaded {} entries", CACHE.size());
    }

    @Override
    public void release() {
        LOGGER.info(IT, "Saving entries...");
        try (DataOutputStream out = new DataOutputStream(new GZIPOutputStream(Files.newOutputStream(index.toPath())))) {
            for (CacheEntry entry: CACHE.values()) {
                entry.write(out);
            }
            LOGGER.info(IT, "Successfully saved {} entries", CACHE.size());
            CACHE.clear();
            started = false;
        } catch (Exception e) {
            LOGGER.error(IT, "Failed to save cache indexes", e);
        }
    }
}
