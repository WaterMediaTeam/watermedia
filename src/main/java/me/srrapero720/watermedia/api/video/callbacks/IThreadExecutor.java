package me.srrapero720.watermedia.api.video.callbacks;

public interface IThreadExecutor {

    /**
     * Inside you should add a code to store this runnable and execute it on playerthread
     * this fix all stupid concurrency issues by VideoLan that can't be addressed by only 1 man.
     * @param runnable Callback runnable for any threaded event
     */
    void execute(Runnable runnable);
}
