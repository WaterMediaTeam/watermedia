package me.srrapero720.watermedia.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;

import static me.srrapero720.watermedia.WaterMedia.LOGGER;

public class StomachUtil {
    public static boolean integrityFrom(ClassLoader loader, String source, File targetFile) {
        try (InputStream is = loader.getResourceAsStream(source)) {
            return integrityFrom(is, targetFile);
        } catch (Exception e) {
            LOGGER.error(ResourceUtil.IT, "Failed to check file integrity of '{}'", targetFile.toPath(), e);
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
        try (inputStream; DigestInputStream dis = new DigestInputStream(inputStream, md)) {
            byte[] buffer = new byte[8192];
            while (dis.read(buffer) != -1);

            return md.digest();
        } catch (Exception e) {
            throw new IllegalStateException("Failed calculating digest", e);
        }
    }
}
