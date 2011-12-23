package org.oasis.toolset.easythrottler.impl;

import java.util.concurrent.atomic.AtomicLong;

import org.oasis.toolset.easythrottler.ThrottleRateTuner;

/**
 * @author hsun
 *
 * A implementation of ThrottleRateTuner that throttles based on fixed request rate.
 * 
 */
public class FixedRateTuner implements ThrottleRateTuner {

    private AtomicLong currentCallIntervalNanos;

    /**
     * Creates a tuner with desired request rate per second.
     * 
     * @param desiredRate desired calls per second.
     */
    public FixedRateTuner(double desiredRate) {
        this.currentCallIntervalNanos = new AtomicLong(convertRateToCallIntervalNanos(desiredRate));
    }

    @Override
    public long getCallIntervalNanos() {
        return currentCallIntervalNanos.get();
    }

    private long convertRateToCallIntervalNanos(double rate) {
        return (long) (1.0E9 / rate);
    }
}
