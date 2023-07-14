package me.srrapero720.watermedia.core.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;

import static me.srrapero720.watermedia.WaterMedia.LOGGER;
import static me.srrapero720.watermedia.core.util.Tools.IT;

public class Stomach {
    public static boolean integrityFrom(ClassLoader loader, String source, File targetFile) {
        try (InputStream sourceStream = loader.getResourceAsStream(source)) {
            MessageDigest md = MessageDigest.getInstance("MD5");

            byte[] sourceDigest = digest(sourceStream, md);
            byte[] targetDigest = digest(new FileInputStream(targetFile), md);
            if (!MessageDigest.isEqual(sourceDigest, targetDigest)) throw new RuntimeException("File no match with the stored one");

            return true;
        } catch (Exception e) {
            LOGGER.error(IT, "Integrity check failed, exception occurred on file '{}'", targetFile.toPath());
            LOGGER.debug(IT, "DETECTED ERROR", e);
        }

        return false;
    }

    private static byte[] digest(InputStream inputStream, MessageDigest md) {
        try (DigestInputStream dis = new DigestInputStream(inputStream, md)) {
            byte[] buffer = new byte[8192];
            while (dis.read(buffer) != -1);

            dis.close();
            return md.digest();
        } catch (Exception e) {
            throw new IllegalStateException("Failed calculating digest", e);
        }
    }
}
