package me.srrapero720.watermedia.tests;

import me.srrapero720.watermedia.WaterMedia;
import me.srrapero720.watermedia.loaders.IBootCore;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public class Core_BootstrapTest extends Test {
    private static final Marker IT = MarkerManager.getMarker("Bootstrap");

    private WaterMedia W;
    @Override
    protected void prepare() throws Exception {
        W = WaterMedia.create(IBootCore.DEFAULT);
    }

    @Override
    protected void run() throws Exception {
        W.init();
    }

    @Override
    protected void release() throws Exception {
//        W.release();
    }
}