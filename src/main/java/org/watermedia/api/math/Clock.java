package org.watermedia.api.math;

public class Clock implements Runnable {
    private long systemTime = System.currentTimeMillis();
    private long time;
    private boolean clock = false;

    public void enable() {
        this.clock = true;
    }

    public void disable() {
        this.clock = false;
    }

    @Override
    public void run() {
        long delta = System.currentTimeMillis() - this.systemTime;
        if (clock) {
            time += delta; // start counting
        }
        this.systemTime = System.currentTimeMillis();
    }
}
