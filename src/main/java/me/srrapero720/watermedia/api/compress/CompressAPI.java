package me.srrapero720.watermedia.api.compress;

import me.srrapero720.watermedia.api.WaterInternalAPI;
import me.srrapero720.watermedia.api.compress.spi.BaseCompressedFile;
import me.srrapero720.watermedia.loader.ILoader;
import me.srrapero720.watermedia.tools.ThreadTool;
import net.sf.sevenzipjbinding.SevenZip;

import java.io.File;
import java.util.concurrent.ExecutorService;

public class CompressAPI extends WaterInternalAPI {
    private static ExecutorService EX;

    public static BaseCompressedFile decompressFile(File file, boolean sync) {

    }

    @Override
    public Priority priority() {
        return Priority.HIGHEST;
    }

    @Override
    public boolean prepare(ILoader bootCore) throws Exception {
        SevenZip.initSevenZipFromPlatformJAR();
        if (EX == null || EX.isTerminated()) {
            EX = null;
        }
        return true;
    }

    @Override
    public void start(ILoader bootCore) throws Exception {
        EX = ThreadTool.executorReduced("decompressor");
    }

    @Override
    public void release() {
        EX.shutdownNow();
        EX.close();
    }
}
