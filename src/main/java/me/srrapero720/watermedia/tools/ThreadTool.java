package me.srrapero720.watermedia.tools;

import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import static me.srrapero720.watermedia.WaterMedia.LOGGER;

public class ThreadTool {
    private static final Marker IT = MarkerManager.getMarker("ThreadTool");
    private static final Thread.UncaughtExceptionHandler EXCEPTION_HANDLER = (t, e) -> LOGGER.error(IT, "Failed running {}", t.getName(), e);

    private static int TWC = 0;

    public static int maxThreads() { return Runtime.getRuntime().availableProcessors(); }
    public static int minThreads() {
        int count = maxThreads();
        if (count <= 2) return 1;
        if (count <= 4) return 2;
        if (count <= 8) return 3;
        if (count <= 16) return 4;
        if (count <= 32) return 6;
        if (count <= 64) return 8;
        return 10;
    }

    @Deprecated
    public static Thread thread(int priority, Runnable runnable) {
        Thread thread = thread$basic(runnable);
        thread.setPriority(priority);
        thread.start();
        return thread;
    }

    @Deprecated
    public static Thread thread(Runnable runnable) {
        Thread thread = thread$basic(runnable);
        thread.start();
        return thread;
    }

    private static Thread thread$basic(Runnable runnable) {
        Thread thread = new Thread(runnable);
        thread.setName("WaterMedia-Task-" + (++TWC));
        thread.setUncaughtExceptionHandler(EXCEPTION_HANDLER);
        thread.setDaemon(true);
        return thread;
    }

    public static ScheduledExecutorService executorReduced(String name) {
        return Executors.newScheduledThreadPool(ThreadTool.minThreads(), ThreadTool.factory("wm-worker-" + name, Thread.NORM_PRIORITY));
    }

    public static ThreadFactory factory(String name, int priority) {
        AtomicInteger count = new AtomicInteger();
        Thread.UncaughtExceptionHandler handler = Thread.currentThread().getUncaughtExceptionHandler();
        return r -> {
            Thread t = new Thread(r);
            t.setName(name + "-" + count.incrementAndGet());
            t.setDaemon(true);
            t.setUncaughtExceptionHandler(((t1, e2) -> {
                EXCEPTION_HANDLER.uncaughtException(t1, e2);
                handler.uncaughtException(t1, e2);
            }));
            t.setPriority(priority);
            return t;
        };
    }
}
