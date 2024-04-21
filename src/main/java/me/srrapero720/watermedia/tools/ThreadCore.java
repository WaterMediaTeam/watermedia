package me.srrapero720.watermedia.tools;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

// This class comes from WATERCoRE and isn't sync with WATERCoRE main project
public class ThreadCore {
    private static final Logger LOGGER = LogManager.getLogger("ThreadCore");
    private static final Thread.UncaughtExceptionHandler EXCEPTION_HANDLER = (t, e) -> LOGGER.error("Failed running {}", t.getName(), e);

    private static int TWC = 0;

    public static int maxThreads() { return Runtime.getRuntime().availableProcessors(); }
    public static int minThreads() {
        int count = maxThreads();
        if (count <= 2) return 1;
        if (count <= 8) return 2;
        if (count <= 16) return 3;
        if (count <= 32) return 4;
        if (count <= 64) return 6;
        return 8;
    }

    private static Thread thread$basic(Runnable runnable) {
        Thread thread = new Thread(runnable);
        thread.setName("WATERCoRE-worker-" + (++TWC));
        thread.setContextClassLoader(Thread.currentThread().getContextClassLoader());
        thread.setUncaughtExceptionHandler(EXCEPTION_HANDLER);
        thread.setDaemon(true);
        return thread;
    }

    public static Thread thread(int priority, Runnable runnable) {
        Thread thread = thread$basic(runnable);
        thread.setPriority(priority);
        thread.start();
        return thread;
    }

    public static ThreadFactory factory(String name, int priority) {
        AtomicInteger count = new AtomicInteger();
        return r -> {
            Thread t = new Thread(r);
            t.setName(name + "-" + count.incrementAndGet());
            t.setContextClassLoader(Thread.currentThread().getContextClassLoader());
            t.setDaemon(true);
            t.setPriority(priority);
            return t;
        };
    }
}