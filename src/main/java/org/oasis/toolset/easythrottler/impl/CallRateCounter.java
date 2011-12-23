package org.oasis.toolset.easythrottler.impl;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author hsun
 *
 * Used to count request rates.
 * 
 */
public class CallRateCounter {

    private AtomicLong successCount;
    private AtomicLong failureCount;

    public CallRateCounter() {
        this.successCount = new AtomicLong(0L);
        this.failureCount = new AtomicLong(0L);
    }

    /**
     * Increases success count by 1.
     */
    public void incSuccess() {
        if (successCount.incrementAndGet() == Long.MAX_VALUE) {
            successCount.set(0L);
        }
    }

    /**
     * Increases failure count by 1.
     */
    public void incFailure() {
        if (failureCount.incrementAndGet() == Long.MAX_VALUE) {
            failureCount.set(0L);
        }
    }

    /**
     * Resets all counts.
     */
    public void reset() {
        successCount.set(0);
        failureCount.set(0);
    }

    /**
     * Gets success count.
     * 
     * @return the success count.
     */
    public long getSuccessCount() {
        return successCount.get();
    }

    /**
     * Gets failure count.
     * 
     * @return the failure count.
     */
    public long getFailureCount() {
        return failureCount.get();
    }
}
