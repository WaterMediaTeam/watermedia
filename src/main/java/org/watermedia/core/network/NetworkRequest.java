package org.watermedia.core.network;

import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.watermedia.tools.ThreadTool;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NetworkRequest implements Runnable {
    private static final Marker IT = MarkerManager.getMarker("ImageAPI");
    private static final DateFormat FORMAT = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
    private static final ExecutorService EXECUTOR = Executors.newScheduledThreadPool(ThreadTool.minThreads(), ThreadTool.factory("ImageFetch-Worker", Thread.NORM_PRIORITY + 1));


    public NetworkRequest() {

    }


    public void start() {
        EXECUTOR.execute(this);
    }

    @Override
    public void run() {

    }




}
