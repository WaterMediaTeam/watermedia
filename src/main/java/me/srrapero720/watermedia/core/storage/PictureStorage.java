package me.srrapero720.watermedia.core.storage;

import me.srrapero720.watermedia.WaterMedia;
import me.srrapero720.watermedia.core.util.IModLoader;
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
public class PictureStorage {
    private static final Marker IT = MarkerFactory.getMarker("PictureStorage");
    private static final Map<String, Entry> ENTRIES = new HashMap<>();

    private static File dir;
    private static File index;

    public static boolean init(IModLoader modLoader) {
        Path workingDir = modLoader.getTempDir();
        if (dir != null || index != null) {
            LOGGER.error(IT, "Rejected attempt to reload LocalStorage" + (workingDir.toAbsolutePath().resolve("cache/pictures").equals(dir.toPath()) ? "with a different path" : ""));
            return true;
        }
        LOGGER.info(IT, "Storage path used for logs and binaries '{}'", workingDir);

        dir = workingDir.toAbsolutePath().resolve("cache/pictures").toFile();
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
                    ENTRIES.put(entry.getUrl(), entry);
                }

                return true;
            } catch (Exception e) {
                LOGGER.error(IT, "Failed to load indexes", e);
                return false;
            }
        }
        return true;
    }

    private static File getFile(String url) {
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
        if (saved && refreshAllIndexOnFile()) ENTRIES.put(url, entry);
        else if (file.exists()) file.delete();
    }

    private static boolean refreshAllIndexOnFile() {
        var out = (DataOutputStream) null;
        try {
            out = new DataOutputStream(new GZIPOutputStream(new FileOutputStream(index)));
            out.writeInt(ENTRIES.size());

            for (var mapEntry : ENTRIES.entrySet()) {
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

    public static Entry getEntry(String url) { return ENTRIES.get(url); }
    public static void updateEntry(Entry fresh) {
        ENTRIES.put(fresh.url, fresh);
    }
    public static void deleteEntry(String url) {
        ENTRIES.remove(url);
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
        public File getFile() { return PictureStorage.getFile(url); }
    }
}
