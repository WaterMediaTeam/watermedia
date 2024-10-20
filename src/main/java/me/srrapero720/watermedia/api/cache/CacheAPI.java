package me.srrapero720.watermedia.api.cache;

import me.srrapero720.watermedia.api.WaterInternalAPI;
import me.srrapero720.watermedia.core.tools.DataTool;
import me.srrapero720.watermedia.loaders.ILoader;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import static me.srrapero720.watermedia.WaterMedia.LOGGER;

@SuppressWarnings({"unused"})
public class CacheAPI extends WaterInternalAPI {
    private static final Marker IT = MarkerManager.getMarker(CacheAPI.class.getSimpleName());
    private static final Map<String, Entry> ENTRIES = new HashMap<>();

    private static File dir;
    private static File index;
    private static boolean init = false;

    private static boolean refreshAll() {
        try(DataOutputStream out = new DataOutputStream(new GZIPOutputStream(Files.newOutputStream(index.toPath())))) {
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
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			return new File(dir, DataTool.encodeHexString(digest.digest(url.getBytes(StandardCharsets.UTF_8))));
		} catch (NoSuchAlgorithmException e) { LOGGER.error(IT, "Failed to initalize digest", e); }

		// Fallback to old naming
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
            if (saved && refreshAll()) {
                ENTRIES.put(url, entry);
            } else if (file.exists()) {
                if (file.delete()) LOGGER.warn(IT, "Cannot delete unsaved entry file of '{}' located in '{}'", url, file.toString());
            }
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
            if (file.exists()) {
                if (file.delete()) LOGGER.warn(IT, "Cannot delete entry file of '{}' located in '{}'", url, file.toString());
            }
        }
    }

    @Override
    public Priority priority() {
        return Priority.HIGHEST;
    }

    @Override
    public boolean prepare(ILoader bootCore) throws Exception {
        // SETUP
        dir = bootCore.tempDir().toAbsolutePath().resolve("cache/pictures").toFile();
        index = new File(dir, "index");
        LOGGER.info(IT, "Mounted on path '{}'", dir);

        return !init;
    }

    @Override
    public void start(ILoader bootCore) throws Exception {
        if (!dir.exists() && !dir.mkdirs()) throw new IOException("Cannot make necessary dirs for proper storing");
        if (index.exists()) {
            try (DataInputStream stream = new DataInputStream(new GZIPInputStream(Files.newInputStream(index.toPath())))) {
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

    @Override
    public void release() {
        refreshAll();
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