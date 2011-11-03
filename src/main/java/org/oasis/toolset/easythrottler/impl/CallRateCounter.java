package org.oasis.toolset.easythrottler.impl;

import java.util.concurrent.atomic.AtomicLong;

public class CallRateCounter {

    private AtomicLong successCount;
    private AtomicLong failureCount;

    public CallRateCounter() {
        this.successCount = new AtomicLong(0L);
        this.failureCount = new AtomicLong(0L);
    }

    public void incSuccess() {
        if (successCount.incrementAndGet() == Long.MAX_VALUE) {
            successCount.set(0L);
        }
    }

    public void incFailure() {
        if (failureCount.incrementAndGet() == Long.MAX_VALUE) {
            failureCount.set(0L);
        }
    }

    public void reset() {
        successCount.set(0);
        failureCount.set(0);
    }

    public long getSuccessCount() {
        return successCount.get();
    }

    public long getFailureCount() {
        return failureCount.get();
    }
}
