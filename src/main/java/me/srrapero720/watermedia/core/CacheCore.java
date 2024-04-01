package me.srrapero720.watermedia.core;

import me.srrapero720.watermedia.api.loader.IMediaLoader;
import me.srrapero720.watermedia.core.tools.exceptions.ReInitException;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.io.*;
import java.nio.file.Files;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import static me.srrapero720.watermedia.WaterMedia.LOGGER;

@SuppressWarnings({"ResultOfMethodCallIgnored", "PathCanBeConvertedToMethod"})
public class CacheCore {
    private static final Marker IT = MarkerManager.getMarker(CacheCore.class.getSimpleName());
    private static final Map<String, Entry> ENTRIES = new HashMap<>();

    private static File dir;
    private static File index;
    private static boolean init = false;

    public static void init(IMediaLoader modLoader) throws Exception {
        if (init) throw new ReInitException(CacheCore.class.getSimpleName());

        // SETUP
        dir = modLoader.tmpPath().toAbsolutePath().resolve("cache/pictures").toFile();
        index = new File(dir, "index");

        // LOGGER
        LOGGER.info(IT, "Mounted on path '{}'", dir);

        if (!dir.exists()) dir.mkdirs();
        if (index.exists()) {
            try (DataInputStream stream = new DataInputStream(new GZIPInputStream(new FileInputStream(index)))) {
                int length = stream.readInt();

                for (int i = 0; i < length; i++) {
                    String url = stream.readUTF();
                    String tag = stream.readUTF();
                    long time = stream.readLong();
                    long expireTime = stream.readLong();
                    Entry entry = new Entry(url, !tag.isEmpty() ? tag : null, time, expireTime);
                    ENTRIES.put(entry.getUrl(), entry);
                }
            } catch (Exception e) {
                LOGGER.error(IT, "Failed to load indexes", e);
            }
        }

        init = true;
    }

    private static boolean refreshAll() {
        try(DataOutputStream out = new DataOutputStream(new GZIPOutputStream(new FileOutputStream(index)))) {
            out.writeInt(ENTRIES.size());

            for (Map.Entry<String, Entry> mapEntry : ENTRIES.entrySet()) {
                Entry entry = mapEntry.getValue();
                out.writeUTF(entry.getUrl());
                out.writeUTF(entry.getTag() == null ? "" : entry.getTag());
                out.writeLong(entry.getTime());
                out.writeLong(entry.getExpireTime());
            }

            return true;
        } catch (IOException e) { LOGGER.error(IT, "Failed to refresh cache index", e); }
        return false;
    }

    private static File entry$getFile(String url) {
        return new File(dir, Base64.getEncoder().encodeToString(url.getBytes()));
    }

    public static void saveFile(String url, String tag, long time, long expireTime, byte[] data) {
        synchronized (ENTRIES) {
            Entry entry = new Entry(url, tag, time, expireTime);
            boolean saved = false;
            File file = entry$getFile(entry.url);

            try (OutputStream out = Files.newOutputStream(file.toPath())) {
                out.write(data);
                saved = true;
            } catch (Exception e) { LOGGER.error(IT, "Failed to save cache file {}", url, e); }

            // SAVE INDEX FIST
            if (saved && refreshAll()) ENTRIES.put(url, entry);
            else if (file.exists()) file.delete();
        }
    }

    public static Entry getEntry(String url) {
        synchronized (ENTRIES) {
            return ENTRIES.get(url);
        }
    }

    public static void updateEntry(Entry fresh) {
        synchronized (ENTRIES) {
            ENTRIES.put(fresh.url, fresh);
        }
    }

    public static void deleteEntry(String url) {
        synchronized (ENTRIES) {
            ENTRIES.remove(url);
            File file = entry$getFile(url);
            if (file.exists()) file.delete();
        }
    }

    public static final class Entry {
        private final String url;
        private String tag;
        private long time;
        private long expireTime;

        public Entry(String url, String tag, long time, long expireTime) {
            this.url = url;
            this.tag = tag;
            this.time = time;
            this.expireTime = expireTime;
        }

        public void setTag(String tag) { this.tag = tag; }
        public void setTime(long time) { this.time = time; }
        public void setExpireTime(long expireTime) { this.expireTime = expireTime; }
        public String getUrl() { return url; }
        public String getTag() { return tag; }
        public long getTime() { return time; }
        public long getExpireTime() { return expireTime; }
        public File getFile() { return entry$getFile(url); }
    }
}