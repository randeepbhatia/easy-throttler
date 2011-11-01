package org.oasis.toolset.easythrottler.impl;

import java.util.concurrent.atomic.AtomicLong;

import org.oasis.toolset.easythrottler.ThrottleEventListener;

public class CallRateMonitor implements ThrottleEventListener {

    private AtomicLong successCount;
    private AtomicLong failureCount;

    public CallRateMonitor() {
        this.successCount = new AtomicLong(0L);
        this.failureCount = new AtomicLong(0L);
    }

    @Override
    public void succeded() {
        if (successCount.incrementAndGet() == Long.MAX_VALUE) {
            successCount.set(0L);
        }
    }

    @Override
    public void failed() {
        if (failureCount.incrementAndGet() == Long.MAX_VALUE) {
            failureCount.set(0L);
        }
    }

    @Override
    public void start() {
     }

    @Override
    public void stop() {
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

    public static void main(String[] args) {
        AtomicLong c = new AtomicLong(Long.MAX_VALUE);
        System.out.println(c.incrementAndGet());
    }
    
}
