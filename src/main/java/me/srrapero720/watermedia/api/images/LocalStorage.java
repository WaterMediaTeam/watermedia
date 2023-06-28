package me.srrapero720.watermedia.api.images;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.io.*;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import static me.srrapero720.watermedia.WaterMedia.LOGGER;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class LocalStorage {
    private static final Marker IT = MarkerFactory.getMarker("CacheStorage");
    private static final Map<String, Entry> entries = new HashMap<>();

    private static File dir;
    private static File index;

    public static boolean init(Path rootDir) {
        dir = rootDir.toAbsolutePath().resolve("cache/pictures").toFile();
        index = new File(dir, "indexer");

        if (!dir.exists()) dir.mkdirs();
        if (index.exists()) {
            try (var stream = new DataInputStream(new GZIPInputStream(new FileInputStream(index)))) {
                int length = stream.readInt();

                for (int i = 0; i < length; i++) {
                    var url = stream.readUTF();
                    var tag = stream.readUTF();
                    var time = stream.readLong();
                    var expireTime = stream.readLong();
                    var entry = new Entry(url, tag.length() > 0 ? tag : null, time, expireTime);
                    entries.put(entry.getUrl(), entry);
                }

                return true;
            } catch (Exception e) {
                LOGGER.error(IT, "Failed to load indexes", e);
                return false;
            }
        }
        return true;
    }

    public static File getFile(String url) {
        return new File(dir, Base64.encodeBase64String(url.getBytes()));
    }

    public static void saveFile(String url, String tag, long time, long expireTime, byte[] data) {
        var entry = new Entry(url, tag, time, expireTime);
        var saved = false;
        var out = (OutputStream) null;
        var file = getFile(entry.url);

        try {
            out = new FileOutputStream(file);
            out.write(data);
            saved = true;
        } catch (Exception e) { LOGGER.error(IT, "Failed to save cache file {}", url, e);
        } finally { IOUtils.closeQuietly(out); }

        // SAVE INDEX FIST
        if (saved && refreshAllIndexOnFile()) entries.put(url, entry);
        else if (file.exists()) file.delete();
    }

    private static boolean refreshAllIndexOnFile() {
        var out = (DataOutputStream) null;
        try {
            out = new DataOutputStream(new GZIPOutputStream(new FileOutputStream(index)));
            out.writeInt(entries.size());

            for (var mapEntry : entries.entrySet()) {
                var entry = mapEntry.getValue();
                out.writeUTF(entry.getUrl());
                out.writeUTF(entry.getTag() == null ? "" : entry.getTag());
                out.writeLong(entry.getTime());
                out.writeLong(entry.getExpireTime());
            }

            return true;
        } catch (IOException e) {
            LOGGER.error(IT, "Failed to save cache index", e);
            return false;
        } finally { IOUtils.closeQuietly(out); }
    }

    public static Entry getEntry(String url) { return entries.get(url); }
    public static void updateEntry(Entry fresh) {
        entries.put(fresh.url, fresh);
    }
    public static void deleteEntry(String url) {
        entries.remove(url);
        var file = getFile(url);
        if (file.exists()) file.delete();
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
    }
}
