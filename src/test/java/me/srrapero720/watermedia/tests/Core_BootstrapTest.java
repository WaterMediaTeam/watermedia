package me.srrapero720.watermedia.tests;

import me.srrapero720.watermedia.WaterMedia;
import me.srrapero720.watermedia.loaders.ILoader;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public class Core_BootstrapTest extends Test {
    private static final Marker IT = MarkerManager.getMarker("Bootstrap");

    private WaterMedia W;
    @Override
    protected void prepare() throws Exception {
        W = WaterMedia.prepare(ILoader.DEFAULT);
    }

    @Override
    protected void run() throws Exception {
        W.start();
    }

    @Override
    protected void release() throws Exception {
//        W.release();
    }
}