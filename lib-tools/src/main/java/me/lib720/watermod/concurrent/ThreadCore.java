package me.lib720.watermod.concurrent;

import me.lib720.watermod.safety.TryCore;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;

// This class comes from WATERCoRE and isn't sync with WATERCoRE main project
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

    public static void sleep(long timeMillis) {
        try {
            Thread.sleep(timeMillis);
        } catch (Exception e) {
            LOGGER.warn("Cannot sleep thread {}", Thread.currentThread().getName());
        }
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

    public static Thread thread(Runnable runnable) {
        Thread thread = thread$basic(runnable);
        thread.start();
        return thread;
    }

    public static Thread threadNoDaemon(Runnable runnable) {
        Thread thread = thread$basic(runnable);
        thread.setDaemon(false);
        thread.start();
        return thread;
    }

    public static Thread threadUncaught(Runnable runnable) {
        Thread thread = thread$basic(runnable);
        thread.setUncaughtExceptionHandler(null);
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

    public static ThreadFactory factory(String name) {
        return factory(name, 3);
    }

    public static void hasClassLoaderOrSet(ClassLoader classLoader) {
        if (Thread.currentThread().getContextClassLoader() == null) Thread.currentThread().setContextClassLoader(classLoader);
    }

    @SuppressWarnings("InfiniteLoopStatement")
    public static void enableLoggerThreads() {
        stopLoggerThreads();
        THREAD_LOGGER = thread(() -> {
            while (true) {
                TryCore.simple(ThreadCore::logThreads);
                ThreadCore.sleep(2000);
                if (THREAD_LOGGER == null) throw new IllegalStateException("Thread logger was lost");
            }
        });
    }

    public static void stopLoggerThreads() {
        TryCore.simple(() -> {
            if (isLoggingThreads()) THREAD_LOGGER.interrupt();
            THREAD_LOGGER = null;
            System.gc();
        });
    }

    public static boolean isLoggingThreads() { return THREAD_LOGGER != null && !THREAD_LOGGER.isInterrupted(); }

    public static void logThreads() {
        LOGGER.info("{}\t{}\t{}\t{}\n", "Name", "State", "Priority", "isDaemon");
        for (Thread t: Thread.getAllStackTraces().keySet())
            LOGGER.info("{}\t{}\t{}\t{}\n", t.getName(), t.getState(), t.getPriority(), t.isDaemon());
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