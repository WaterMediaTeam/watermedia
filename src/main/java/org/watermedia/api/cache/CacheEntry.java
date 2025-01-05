package org.watermedia.api.cache;

import org.watermedia.tools.DataTool;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.Base64;
import java.util.Date;

public record CacheEntry(URI uri, String tag, String file, String ext, Date requestedDate, Date expirationDate) {

    public CacheEntry(URI uri, String tag, String ext, Date requestedDate, Date expirationDate) {
        this(uri, tag, genFilename(uri), ext, requestedDate, expirationDate);
    }

    public boolean expired() {
        return expirationDate != null && System.currentTimeMillis() > expirationDate.getTime();
    }

    public void write(DataOutputStream in) throws IOException {
        in.writeUTF(this.uri.toString());
        in.writeUTF(this.tag);
        in.writeUTF(this.file);
        in.writeUTF(this.ext);
        in.writeLong(this.requestedDate.getTime());
        in.writeLong(this.expirationDate.getTime());
    }

    public static CacheEntry read(DataInputStream in) throws IOException {
        String url = in.readUTF();
        String tag = in.readUTF();
        String file = in.readUTF();
        String ext = in.readUTF();
        long requestedTime = in.readLong();
        long expirationTime = in.readLong();
        return new CacheEntry(URI.create(url), !tag.isEmpty() ? tag : null, file, ext, new Date(requestedTime), new Date(expirationTime));
    }

    private static String genFilename(URI url) {
        String n = DataTool.hexEncode(url.toString());
        return n != null ? n : Base64.getEncoder().encodeToString(url.toString().getBytes());
    }
}