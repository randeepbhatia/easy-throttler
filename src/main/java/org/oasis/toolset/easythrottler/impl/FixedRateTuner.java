package org.oasis.toolset.easythrottler.impl;

import java.util.concurrent.atomic.AtomicLong;

import org.oasis.toolset.easythrottler.ThrottleRateTuner;

public class FixedRateTuner implements ThrottleRateTuner {

    private AtomicLong currentCallIntervalNanos;

    public FixedRateTuner(double desiredRate) {
        this.currentCallIntervalNanos = new AtomicLong(convertRateToCallIntervalNanos(desiredRate));
    }

    @Override
    public long getCallIntervalNanos() {
        return currentCallIntervalNanos.get();
    }

    long convertRateToCallIntervalNanos(double rate) {
        return (long) (1.0E9 / rate);
    }
}
