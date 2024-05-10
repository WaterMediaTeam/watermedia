package me.lib720.watermod.concurrent;

import me.lib720.watermod.safety.TryCore;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;

/**
 * @deprecated No replacement
 */
@Deprecated(forRemoval = true)
public class ThreadCore {
    private static final Logger LOGGER = LogManager.getLogger("ThreadCore");
    private static final Thread.UncaughtExceptionHandler EXCEPTION_HANDLER = (t, e) -> LOGGER.error("Failed running {}", t.getName(), e);

    private static Thread THREAD_LOGGER = null;
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

    public static <T> T executeLock(Lock lock, RunnableToReturn<T> runnable) {
        lock.lock();
        try {
            return runnable.run();
        } finally {
            lock.unlock();
        }
    }

    public static void executeLock(Lock lock, Runnable runnable) {
        lock.lock();
        try {
            runnable.run();
        } finally {
            lock.unlock();
        }
    }

    private static Thread thread$basic(Runnable runnable) {
        Thread thread = new Thread(runnable);
        thread.setName("WATERCoRE-worker-" + (++TWC));
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

    public static Thread thread(Runnable runnable) {
        Thread thread = thread$basic(runnable);
        thread.start();
        return thread;
    }

    public static ThreadFactory factory(String name, int priority) {
        AtomicInteger count = new AtomicInteger();
        Thread.UncaughtExceptionHandler handler = Thread.currentThread().getUncaughtExceptionHandler();
        return r -> {
            Thread t = new Thread(r);
            t.setName(name + "-" + count.incrementAndGet());
            t.setDaemon(true);
            t .setUncaughtExceptionHandler(((t1, e2) -> {
                EXCEPTION_HANDLER.uncaughtException(t1, e2);
                handler.uncaughtException(t1, e2);
            }));
            t.setPriority(priority);
            return t;
        };
    }

    public static ThreadFactory factory(String name) {
        return factory(name, 3);
    }

    public static void simple(TryCore.ActionSimple runnable, TryCore.CatchSimple catchSimple, TryCore.FinallySimple finallySimple) {
        try {
            runnable.run();
        } catch (Exception e) {
            if (catchSimple != null) catchSimple.run(e);
        } finally {
            if (finallySimple != null) finallySimple.run();
        }
    }

    public interface RunnableToReturn<T> {
        T run();
    }
}