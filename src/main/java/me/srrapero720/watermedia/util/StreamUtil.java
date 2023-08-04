package me.srrapero720.watermedia.util;

import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.file.Path;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static me.srrapero720.watermedia.WaterMedia.LOGGER;

public class StreamUtil {
    private static final int MAX_SKIP_BUFFER_SIZE = 2048;
    private static final int DEFAULT_BUFFER_SIZE = 8192;
    private static final int MAX_BUFFER_SIZE = Integer.MAX_VALUE - 8;
    /**
     * Size of the buffer to read/write data
     */
    private static final int BUFFER_SIZE = 4096;


    public static boolean integrityFrom(ClassLoader loader, String source, File targetFile) {
        try (InputStream is = loader.getResourceAsStream(source)) {
            return integrityFrom(is, targetFile);
        } catch (Exception e) {
            LOGGER.error(AssetsUtil.IT, "Failed to check file integrity of '{}'", targetFile.toPath(), e);
        }
        return false;
    }

    public static boolean integrityFrom(InputStream source, File targetFile) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");

        byte[] sourceDigest = digest(source, md);
        byte[] targetDigest = digest(new FileInputStream(targetFile), md);
        if (!MessageDigest.isEqual(sourceDigest, targetDigest)) throw new Exception("File no match with the stored one");

        return true;
    }

    private static byte[] digest(InputStream inputStream, MessageDigest md) {
        try (DigestInputStream dis = new DigestInputStream(inputStream, md)) {
            byte[] buffer = new byte[8192];
            while (dis.read(buffer) != -1);

            return md.digest();
        } catch (Exception e) {
            throw new IllegalStateException("Failed calculating digest", e);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    public static byte[] readAllBytes(InputStream inputStream) throws IOException {
        int len = Integer.MAX_VALUE;
        if (len < 0) {
            throw new IllegalArgumentException("len < 0");
        }

        List<byte[]> bufs = null;
        byte[] result = null;
        int total = 0;
        int remaining = len;
        int n;
        do {
            byte[] buf = new byte[Math.min(remaining, DEFAULT_BUFFER_SIZE)];
            int nread = 0;

            // read to EOF which may read more or less than buffer size
            while ((n = inputStream.read(buf, nread,
                    Math.min(buf.length - nread, remaining))) > 0) {
                nread += n;
                remaining -= n;
            }

            if (nread > 0) {
                if (MAX_BUFFER_SIZE - total < nread) {
                    throw new OutOfMemoryError("Required array size too large");
                }
                if (nread < buf.length) {
                    buf = Arrays.copyOfRange(buf, 0, nread);
                }
                total += nread;
                if (result == null) {
                    result = buf;
                } else {
                    if (bufs == null) {
                        bufs = new ArrayList<>();
                        bufs.add(result);
                    }
                    bufs.add(buf);
                }
            }
            // if the last call to read returned -1 or the number of bytes
            // requested have been read then break
        } while (n >= 0 && remaining > 0);

        if (bufs == null) {
            if (result == null) {
                return new byte[0];
            }
            return result.length == total ?
                    result : Arrays.copyOf(result, total);
        }

        result = new byte[total];
        int offset = 0;
        remaining = total;
        for (byte[] b : bufs) {
            int count = Math.min(b.length, remaining);
            System.arraycopy(b, 0, result, offset, count);
            offset += count;
            remaining -= count;
        }

        return result;
    }

    /**
     * Extracts a zip file specified by the zipFilePath to a directory specified by
     * destDirectory (will be created if does not exists)
     * @param zipFilePath
     * @param destDirectory
     * @throws IOException
     */
    public static void unzip(Path zipFilePath, Path destDirectory) throws IOException {
        File destDir = destDirectory.toFile();
        if (!destDir.exists()) destDir.mkdir();

        ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFilePath.toFile()));
        ZipEntry entry = zipIn.getNextEntry();
        // iterates over entries in the zip file
        while (entry != null) {
            String filePath = destDirectory + File.separator + entry.getName();
            if (!entry.isDirectory()) {
                // if the entry is a file, extracts it
                extractFile(zipIn, filePath);
            } else {
                // if the entry is a directory, make the directory
                File dir = new File(filePath);
                dir.mkdirs();
            }
            zipIn.closeEntry();
            entry = zipIn.getNextEntry();
        }
        zipIn.close();
    }

    /**
     * Extracts a zip entry (file entry)
     * @param zipIn
     * @param filePath
     * @throws IOException
     */
    private static void extractFile(ZipInputStream zipIn, String filePath) throws IOException {
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
        byte[] bytesIn = new byte[BUFFER_SIZE];
        int read = 0;
        while ((read = zipIn.read(bytesIn)) != -1) {
            bos.write(bytesIn, 0, read);
        }
        bos.close();
    }
}
