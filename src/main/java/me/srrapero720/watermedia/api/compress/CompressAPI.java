package me.srrapero720.watermedia.api.compress;

import me.srrapero720.watermedia.api.WaterInternalAPI;
import me.srrapero720.watermedia.loader.ILoader;
import net.sf.sevenzipjbinding.SevenZip;

public class CompressAPI extends WaterInternalAPI {
    @Override
    public Priority priority() {
        return Priority.HIGHEST;
    }

    @Override
    public boolean prepare(ILoader bootCore) throws Exception {
        SevenZip.initSevenZipFromPlatformJAR();
        return false;
    }

    @Override
    public void start(ILoader bootCore) throws Exception {

    }

    @Override
    public void release() {

    }
}
