package me.srrapero720.watermedia.core.compress;

import me.srrapero720.watermedia.core.WaterInternalAPI;
import org.watermedia.WaterMedia;
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
    public boolean prepare(WaterMedia.ILoader bootCore) throws Exception {
        SevenZip.initSevenZipFromPlatformJAR();
        if (EX == null || EX.isTerminated()) {
            EX = null;
        }
        return true;
    }

    @Override
    public void start(WaterMedia.ILoader bootCore) throws Exception { EX = ThreadTool.executorReduced("decompressor"); }

    @Override
    public void release() { EX.shutdown(); }
}
