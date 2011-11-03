package org.oasis.toolset.easythrottler.impl;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.oasis.toolset.easythrottler.RequestThrottledException;
import org.oasis.toolset.easythrottler.Startable;
import org.oasis.toolset.easythrottler.ThrottleEventListener;
import org.oasis.toolset.easythrottler.ThrottleException;
import org.oasis.toolset.easythrottler.ThrottleRateTuner;
import org.oasis.toolset.easythrottler.ThrottleStyle;
import org.oasis.toolset.easythrottler.Throttler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main entry point to use easy throttler.
 * 
 * @author hsun
 * 
 */
public class BlockingQueueThrottler implements Throttler {

    private static final Logger LOG = LoggerFactory.getLogger(BlockingQueueThrottler.class);
    private static final Boolean TOKEN = Boolean.TRUE;

    private Semaphore semaphore;
    private BlockingQueue<Boolean> tokens;
    private List<ThrottleEventListener> eventListeners;
    private ThrottleRateTuner tuner;

    private String name;
    private boolean isThrottleOn;
    private boolean paused;
    private ThrottleStyle style;
    private long timeoutMillis;

    public static class Builder {

        private BlockingQueueThrottler throttler;

        public Builder(String name) {
            this.throttler = new BlockingQueueThrottler(name);
        }

        public Builder withFailStyle() {
            this.throttler.style = ThrottleStyle.FAIL;
            return this;
        }

        public Builder withBlockStyle(long timeout) {
            this.throttler.style = ThrottleStyle.BLOCK;
            this.throttler.timeoutMillis = timeout;
            return this;
        }

        public BlockingQueueThrottler build() {
            return this.throttler;
        }
    }

    // use builder to create instance
    BlockingQueueThrottler(String name) {
        this.name = name;
        this.style = ThrottleStyle.FAIL;
        this.semaphore = new Semaphore(1);
        this.tokens = new LinkedBlockingQueue<Boolean>(1);
        this.eventListeners = new LinkedList<ThrottleEventListener>();
        this.isThrottleOn = false;
        this.paused = false;
    }

    @Override
    public void start() {
        Thread t = new Thread() {
            public void run() {
                try {
                    if (tuner != null && tuner instanceof Startable) {
                        ((Startable)tuner).start();
                    }
                    for (ThrottleEventListener listener : eventListeners) {
                        if (listener instanceof Startable) {
                            ((Startable)listener).start();
                        }
                    }

                    semaphore.acquire();
                    isThrottleOn = true;
                    LOG.info("Scheduler is on.");
                    while (isThrottleOn) {
                        semaphore.tryAcquire(1, tuner.getCallIntervalNanos(), TimeUnit.NANOSECONDS);
                        tokens.put(TOKEN);
                    }
                } catch (InterruptedException e) {
                    LOG.error("Scheduler is interrupted.");
                    throw new ThrottleException("Scheduler is interrupted.");
                } finally {
                    isThrottleOn = false;
                    tokens.drainTo(new HashSet<Boolean>());
                    semaphore.release();
                    for (ThrottleEventListener listener : eventListeners) {
                        if (listener instanceof Startable) {
                            ((Startable)listener).stop();
                        }
                    }
                    if (tuner != null && tuner instanceof Startable) {
                        ((Startable)tuner).stop();
                    }
                    LOG.info("Scheduler is off.");
                }
            };
        };
        t.setDaemon(true);
        t.start();
    }

    @Override
    public void stop() {
        isThrottleOn = false;
    }

    @Override
    public void pause() {
        paused = true;
    }

    @Override
    public void resume() {
        paused = false;
    }

    public void throttle() {
        if (!isThrottleOn || paused) {
            notifySuccess();
            return;
        }

        try {
            switch (this.style) {
            case FAIL:
                callOnce();
                break;
            case BLOCK:
                callWithBlocking(this.timeoutMillis);
            default:
                // no throttling
            }
            notifySuccess();
        } catch (RuntimeException re) {
            notifyFailure();
            throw re;
        }
    }

    private void notifySuccess() {
        for (ThrottleEventListener listener : eventListeners) {
            listener.succeded();
        }
    }

    private void notifyFailure() {
        for (ThrottleEventListener listener : eventListeners) {
            listener.failed();
        }
    }

    private void callOnce() {
        if (tokens.poll() == null) {
            throw new RequestThrottledException();
        }
    }

    private void callWithBlocking(long timeoutMillis) {
        try {
            if (tokens.poll(timeoutMillis, TimeUnit.MILLISECONDS) == null) {
                throw new RequestThrottledException();
            }
        } catch (InterruptedException e) {
            throw new ThrottleException("Caller is interrupted.");
        }
    }

    @Override
    public void registerThrottleEventListener(ThrottleEventListener listener) {
        this.eventListeners.add(listener);
    }

    @Override
    public void unregisterThrottleEventListener(ThrottleEventListener listener) {
        this.eventListeners.remove(listener);
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setThrottleRateTuner(ThrottleRateTuner tuner) {
        this.tuner = tuner;
    }
}
