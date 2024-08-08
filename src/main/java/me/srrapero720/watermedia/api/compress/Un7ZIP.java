package me.srrapero720.watermedia.api.compress;

import me.srrapero720.watermedia.api.compress.spi.IDecompressor;
import net.sf.sevenzipjbinding.*;
import net.sf.sevenzipjbinding.impl.RandomAccessFileInStream;

import java.io.Closeable;
import java.io.IOException;
import java.io.RandomAccessFile;

public class Un7ZIP implements IDecompressor, Runnable, Closeable {
    RandomAccessFile file;
    IInArchive archive;
    final long size;
    final int files;
    int extracted = 0;

    public Un7ZIP(String filename) throws IOException {
        file = new RandomAccessFile(filename, "r");
        archive = SevenZip.openInArchive(ArchiveFormat.SEVEN_ZIP, new RandomAccessFileInStream(file));
        size = Long.parseLong(archive.getStringArchiveProperty(PropID.SIZE));
        files = archive.getNumberOfItems();
    }

    @Override
    public int files() {
        try {
            return archive.getNumberOfItems();
        } catch (SevenZipException e) {
            return -1;
        }
    }

    @Override
    public long size() {
        return size;
    }

    @Override
    public int remaining() {
        return files - extracted;
    }

    @Override
    public void close() throws IOException {
        archive.close();
        file.close();
    }

    @Override
    public void run() {

    }

    private static final class ExtractionCallback implements IArchiveExtractCallback {
        private int size = 0;
        private int index;
        private IInArchive archive;

        public ExtractionCallback(IInArchive inArchive) {
            this.archive = inArchive;
        }

        @Override
        public ISequentialOutStream getStream(int i, ExtractAskMode extractAskMode) throws SevenZipException {
            this.index = i;
            if (extractAskMode != ExtractAskMode.EXTRACT) {
                return null;
            }
            return data -> {
                size += data.length;
                return data.length; // Return amount of proceed data
            };
        }

        @Override
        public void prepareOperation(ExtractAskMode extractAskMode) throws SevenZipException {

        }

        @Override
        public void setOperationResult(ExtractOperationResult extractOperationResult) throws SevenZipException {

        }

        @Override
        public void setTotal(long l) throws SevenZipException {

        }

        @Override
        public void setCompleted(long l) throws SevenZipException {

        }
    }
}
