package me.srrapero720.watermedia.core.compress;

import me.srrapero720.watermedia.core.WaterInternalAPI;
import me.srrapero720.watermedia.loader.ILoader;
import org.watermedia.tools.ThreadTool;
import net.sf.sevenzipjbinding.SevenZip;

import java.util.concurrent.ExecutorService;

public class CompressCore extends WaterInternalAPI {
    private static ExecutorService EX;

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
    public void start(ILoader bootCore) throws Exception { EX = ThreadTool.executorReduced("decompressor"); }

    @Override
    public void release() { EX.shutdown(); }
}
