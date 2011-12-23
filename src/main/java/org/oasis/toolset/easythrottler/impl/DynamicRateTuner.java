package org.oasis.toolset.easythrottler.impl;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicLong;

import org.oasis.toolset.easythrottler.Startable;
import org.oasis.toolset.easythrottler.ThrottleEventListener;
import org.oasis.toolset.easythrottler.ThrottleRateTuner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author hsun
 * 
 * A refrence implementation on how to dynamically adjust request rate.
 * 
 */
public class DynamicRateTuner implements ThrottleRateTuner, ThrottleEventListener, Startable {

    private static final Logger LOG = LoggerFactory.getLogger(DynamicRateTuner.class);

    private boolean started;
    private CallRateCounter rateCounter;
    private long monitorIntervalMillis;
    private Timer monitorTimer;
    private double threshold;
    private double adjustStep;
    private long lastSuccess;
    private double desiredCallRate;
    private AtomicLong currentCallIntervalNanos;

    /**
     * Creates an instance.
     * 
     * @param desiredRate desired request rate per second.
     * @param monitorIntervalMillis time interval to measue the request rate.
     * @param threshold the minimum request rate difference that will trigger the rate adjust action.
     * @param adjustStep the percentage of rate to adjust per measuring cycle.
     */
    public DynamicRateTuner(double desiredRate, long monitorIntervalMillis, double threshold,
            double adjustStep) {
        
        this.desiredCallRate = desiredRate;
        this.monitorIntervalMillis = monitorIntervalMillis;
        this.threshold = threshold;
        this.adjustStep = adjustStep;

        this.currentCallIntervalNanos = new AtomicLong(convertRateToCallIntervalNanos(desiredRate));
        this.rateCounter = new CallRateCounter();
        this.started = false;
    }

    @Override
    public synchronized void start() {
        if (started) {
            return;
        }
        started = true;

        monitorTimer = new Timer("CallRateMonitor Timer", true);
        monitorTimer.schedule(new TimerTask() {

            @Override
            public void run() {
                adjustCallInterval();
            }

        }, 0, monitorIntervalMillis);
        started = true;
    }

    @Override
    public void stop() {
        monitorTimer.cancel();
        started = false;
    }

    @Override
    public long getCallIntervalNanos() {
        return currentCallIntervalNanos.get();
    }

    @Override
    public void succeded() {
        rateCounter.incSuccess();
    }

    @Override
    public void failed() {
        rateCounter.incFailure();
    }

    long convertRateToCallIntervalNanos(double rate) {
        return (long) (1.0E9 / rate);
    }

    void adjustCallInterval() {
        long successes = rateCounter.getSuccessCount();
        if (successes >= lastSuccess) {
            double realCallRate = (successes - lastSuccess) * 1000.0 / monitorIntervalMillis;
            double delta = desiredCallRate - realCallRate;
            if (Math.abs(delta) > threshold) {
                double changePercentage = Math.signum(delta) * adjustStep;
                long currentInterval = currentCallIntervalNanos.get();
                currentCallIntervalNanos.set((long) (currentInterval * (1 - changePercentage / 100.0)));
                LOG.info("Real call rate is {}. adjust percentage is {}",
                        realCallRate,
                        changePercentage);
            }
        } else {
            LOG.info("Skip a cycle as the counters may need to be reset");
            // in rare cases when we exceed the Long.MAX_VALUE, skip a cycle
        }
        lastSuccess = successes;
    }
}
