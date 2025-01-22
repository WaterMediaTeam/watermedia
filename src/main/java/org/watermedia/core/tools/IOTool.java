package org.watermedia.core.tools;

import org.watermedia.api.image.decoders.GifDecoder;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.watermedia.WaterMedia.LOGGER;

public class IOTool {
    private static final Marker IT = MarkerManager.getMarker("Tools");

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

    public static String readString(Path from) {
        try (BufferedInputStream in = new BufferedInputStream(Files.newInputStream(from)))  {
            byte[] bytes = DataTool.readAllBytes(in);
            return new String(bytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
//            LOGGER.error(IT, "Failed to read text file", e);
            return null;
        }
    }

    public static String readString(File from) {
        return readString(from.toPath());
    }

    public static GifDecoder readGif(Path path) {
        try (BufferedInputStream in = new BufferedInputStream(Files.newInputStream(path))) {
            GifDecoder gif = new GifDecoder();
            int status = gif.read(in);
            if (status == GifDecoder.STATUS_OK) return gif;

            throw new IOException("Failed to process GIF - Decoder status: " + status);
        } catch (Exception e) {
            LOGGER.error(IT, "Failed reading GIF from disk", e);
        }
        return null;
    }

    public static boolean writeData(File to, byte[] data) {
        return writeData(to.toPath(), data);
    }

    public static boolean writeData(Path to, byte[] data) {
        to.getParent().toFile().mkdirs();
        try (BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(to.toFile()))) {
            os.write(data);
            return true;
        } catch (Exception e) {
            LOGGER.error(IT, "Failed to write text file from disk", e);
            return false;
        }
    }

    public static void un7zip(Path zipPath) throws IOException { un7zip(zipPath, zipPath.getParent()); }
    public static void un7zip(Path zipPath, Path destPath) throws IOException {
        if (true)
            throw new UnsupportedOperationException("Cannot extract 7z file, support was dropped");
        LOGGER.debug(IT, "Unzipping ZIP file '{}' to directory '{}'", zipPath, destPath);

//        try (RandomAccessFile file = new RandomAccessFile(zipPath.toFile(), "r");
//             IInArchive archive = SevenZip.openInArchive(null, new RandomAccessFileInStream(file))
//        ) {
//            final int count = archive.getNumberOfItems();
//            final ArrayList<Integer> extractIds = new ArrayList<>();
//            for (int i = 0; i < count; i++) {
//                if ((boolean) archive.getProperty(i, PropID.IS_FOLDER)) {
//                    File f = destPath.resolve(archive.getProperty(i, PropID.PATH).toString()).toFile();
//                    if (!f.exists() && !f.mkdirs()) throw new IOException("Failed to create directories");
//                } else {
//                    extractIds.add(i);
//                }
//            }
//
//            archive.extract(DataTool.unbox(extractIds), false, new Seven7ExtractCallback(destPath, archive));
//        }
    }

    public static void unzip(Path zipFilePath) throws IOException { unzip(zipFilePath, zipFilePath.getParent()); }
    public static void unzip(Path zipFilePath, Path destDirectory) throws IOException {
        LOGGER.debug(IT, "Unzipping file from '{}' to directory '{}'", zipFilePath, destDirectory);
        if (zipFilePath.toString().endsWith(".7z")) throw new IOException("Attempted to extract a 7z as a .zip");
        File destDir = destDirectory.toFile();
        if (!destDir.exists()) destDir.mkdirs();

        try (ZipInputStream zipIn = new ZipInputStream(Files.newInputStream(zipFilePath))) {
            ZipEntry entry = zipIn.getNextEntry();
            // iterates over entries in the zip file
            while (entry != null) {
                String filePath = destDirectory + File.separator + entry.getName();
                if (!entry.isDirectory()) {
                    // if the entry is a file, extracts it
                    unzip$extract(zipIn, filePath);
                } else {
                    // if the entry is a directory, make the directory
                    File dir = new File(filePath);
                    dir.mkdirs();
                }
                zipIn.closeEntry();
                entry = zipIn.getNextEntry();
            }
        }
    }

    private static void unzip$extract(ZipInputStream zipIn, String filePath) throws IOException {
        try (BufferedOutputStream output = new BufferedOutputStream(Files.newOutputStream(Paths.get(filePath)))) {
            byte[] bytesIn = new byte[4096];
            int read;
            while ((read = zipIn.read(bytesIn)) != -1) output.write(bytesIn, 0, read);
        }
    }

//    private static final class Seven7ExtractCallback implements IArchiveExtractCallback {
//        private final IInArchive archive;
//        private final Path destPath;
//        private OutputStream out;
//        private int index = 0;
//
//        public Seven7ExtractCallback(Path destPath, IInArchive archive) {
//            this.archive = archive;
//            this.destPath = destPath;
//        }
//
//        @Override
//        public ISequentialOutStream getStream(int i, ExtractAskMode extractAskMode) throws SevenZipException {
//            this.index = i;
//            try {
//                this.out = Files.newOutputStream(destPath.resolve(archive.getProperty(i, PropID.PATH).toString()));
//                return bytes -> {
//                    try {
//                        out.write(bytes);
//                    } catch (IOException e) {
//                        throw new SevenZipException(e);
//                    }
//                    return bytes.length;
//                };
//            } catch (IOException e) {
//                throw new SevenZipException(e);
//            }
//        }
//
//        @Override
//        public void prepareOperation(ExtractAskMode extractAskMode) {}
//
//        @Override
//        public void setOperationResult(ExtractOperationResult extractOperationResult) throws SevenZipException {
//            if (extractOperationResult != ExtractOperationResult.OK) {
//                LOGGER.error(IT, "Failed to extract file {}", archive.getProperty(index, PropID.PATH));
//            }
//
//            try {
//                out.close();
//            } catch (IOException e) {
//                throw new SevenZipException(e);
//            }
//        }
//
//        @Override
//        public void setTotal(long l) {}
//
//        @Override
//        public void setCompleted(long l) {}
//    }
}