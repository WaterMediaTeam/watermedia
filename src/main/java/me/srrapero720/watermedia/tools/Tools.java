package me.srrapero720.watermedia.tools;

import java.util.concurrent.locks.Lock;

public class Tools {
    public static void execLock(Lock lock, Runnable runnable) {
        lock.lock();
        try {
            runnable.run();
        } finally {
            lock.unlock();
        }
    }

    public static <T> T withReturn(ActionWithReturn<T> runnable, T defaultVar) {
        try {
            return runnable.run(defaultVar);
        } catch (Exception exception) {
            return defaultVar;
        }
    }

    public interface ActionWithReturn<T> {
        T run(T defaultVar) throws Exception;
    }
}
