package org.watermedia.tools;

import net.sf.sevenzipjbinding.*;
import net.sf.sevenzipjbinding.impl.RandomAccessFileInStream;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.watermedia.WaterMedia.LOGGER;

public class IOTool {
    private static final Marker IT = MarkerManager.getMarker("Tools");

    public static String readString(Path from) {
        try {
            byte[] bytes = Files.readAllBytes(from);
            return new String(bytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean rmdirs(Path path) {
        return rmdirs(path.toFile());
    }

    public static boolean rmdirs(File root) {
        File[] files = root.listFiles();

        if (files == null || files.length == 0) return root.delete();
        for (File f: files) {
            File[] childs = f.listFiles();
            if (childs != null && childs.length != 0 && !rmdirs(f)) return false;
            if (!f.delete()) return false;
        }
        return true;
    }

    public static void un7zip(Path zipPath) throws IOException { un7zip(zipPath, zipPath.getParent()); }
    public static void un7zip(Path zipPath, Path destPath) throws IOException {
        LOGGER.debug(IT, "Unzipping ZIP file '{}' to directory '{}'", zipPath, destPath);

        try (var file = new RandomAccessFile(zipPath.toFile(), "r");
             var archive = SevenZip.openInArchive(null, new RandomAccessFileInStream(file))
        ) {
            final int count = archive.getNumberOfItems();
            final var extractIds = new ArrayList<Integer>();
            for (int i = 0; i < count; i++) {
                if ((boolean) archive.getProperty(i, PropID.IS_FOLDER)) {
                    File f = destPath.resolve(archive.getProperty(i, PropID.PATH).toString()).toFile();
                    if (!f.exists() && !f.mkdirs()) throw new IOException("Failed to create directories");
                } else {
                    extractIds.add(i);
                }
            }

            archive.extract(DataTool.unbox(extractIds), false, new Seven7ExtractCallback(destPath, archive));
        }
    }

    public static void unzip(Path zip) throws IOException { unzip(zip, zip.getParent()); }
    public static void unzip(Path zipPath, Path destPath) throws IOException {
        LOGGER.debug(IT, "Unzipping 7z file '{}' to directory '{}'", zipPath, destPath);

        if (!zipPath.toString().endsWith(".zip"))
            throw new IOException("Attempted to extract a non .zip file");
        if (!destPath.toFile().exists() && !destPath.toFile().mkdirs())
            throw new IOException("Failed to make required directories");

        try (var in = new ZipInputStream(Files.newInputStream(zipPath))) {
            ZipEntry en = in.getNextEntry();
            while (en != null) { // iterates over entries in the zip file
                String desPath = destPath + File.separator + en.getName();
                if (!en.isDirectory()) {
                    unzip$extract(in, desPath); // if the entry is a file, extracts it
                } else {
                    File dir = new File(desPath); // if the entry is a directory, make the directory
                    dir.mkdirs();
                }
                in.closeEntry();
                en = in.getNextEntry();
            }
        }
    }

    private static void unzip$extract(ZipInputStream zipIn, String filePath) throws IOException {
        try (BufferedOutputStream output = new BufferedOutputStream(Files.newOutputStream(Paths.get(filePath)))) {
            byte[] bytesIn = new byte[1024 * 8];
            int read;
            while ((read = zipIn.read(bytesIn)) != -1) output.write(bytesIn, 0, read);
        }
    }

    private static final class Seven7ExtractCallback implements IArchiveExtractCallback {
        private final IInArchive archive;
        private final Path destPath;
        private OutputStream out;
        private int index = 0;

        public Seven7ExtractCallback(Path destPath, IInArchive archive) {
            this.archive = archive;
            this.destPath = destPath;
        }

        @Override
        public ISequentialOutStream getStream(int i, ExtractAskMode extractAskMode) throws SevenZipException {
            this.index = i;
            try {
                this.out = Files.newOutputStream(destPath.resolve(archive.getProperty(i, PropID.PATH).toString()));
                return bytes -> {
                    try {
                        out.write(bytes);
                    } catch (IOException e) {
                        throw new SevenZipException(e);
                    }
                    return bytes.length;
                };
            } catch (IOException e) {
                throw new SevenZipException(e);
            }
        }

        @Override
        public void prepareOperation(ExtractAskMode extractAskMode) {}

        @Override
        public void setOperationResult(ExtractOperationResult extractOperationResult) throws SevenZipException {
            if (extractOperationResult != ExtractOperationResult.OK) {
                LOGGER.error(IT, "Failed to extract file {}", archive.getProperty(index, PropID.PATH));
            }

            try {
                out.close();
            } catch (IOException e) {
                throw new SevenZipException(e);
            }
        }

        @Override
        public void setTotal(long l) {}

        @Override
        public void setCompleted(long l) {}
    }
}