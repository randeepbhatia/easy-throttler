package org.oasis.toolset.easythrottler;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicReference;

import org.oasis.toolset.easythrottler.impl.CallRateCounter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThrottleMonitor implements ThrottleMonitorMBean, Startable, ThrottleEventListener {

    private static final Logger LOG = LoggerFactory.getLogger(ThrottleMonitor.class);

    private CallRateCounter counter;
    private Throttler throttler;
    private long monitorIntervalMillis;
    private Timer monitorTimer;
    private long lastSuccess;
    private long lastFailure;
    private AtomicReference<Double> succeededCallRate;
    private AtomicReference<Double> failedCallRate;

    public ThrottleMonitor(Throttler throttler, long monitorIntervalMillis) {
        this.counter = new CallRateCounter();
        this.succeededCallRate = new AtomicReference<Double>(0.0);
        this.failedCallRate = new AtomicReference<Double>(0.0);
        this.monitorIntervalMillis = monitorIntervalMillis;
        this.throttler = throttler;
        this.throttler.registerThrottleEventListener(this);
    }

        @Override
    public void succeded() {
        counter.incSuccess();
    }

    @Override
    public void failed() {
        counter.incFailure();
    }

    @Override
    public void start() {
        monitorTimer = new Timer("ThrottleMonitor Timer", true);
        monitorTimer.schedule(new TimerTask() {

            @Override
            public void run() {
                long successes = counter.getSuccessCount();
                long failures = counter.getFailureCount();
                if (successes >= lastSuccess && failures >= lastFailure) {
                    succeededCallRate.set((successes - lastSuccess) * 1000.0 / monitorIntervalMillis);
                    failedCallRate.set((failures - lastFailure) * 1000.0 / monitorIntervalMillis);
                    LOG.info("Call rates. Success: {} Failure: {}",
                            succeededCallRate.get(),
                            failedCallRate.get());
                } else {
                    LOG.info("Skip a cycle as the counters may need to be reset");
                    // in rare cases when we exceed the Long.MAX_VALUE, skip a cycle
                }
                lastSuccess = successes;
                lastFailure = failures;
            }
        }, 0, monitorIntervalMillis);
    }

    @Override
    public void stop() {
        monitorTimer.cancel();
    }

    @Override
    public double getCallRateMovingAverage() {
        return succeededCallRate.get();
    }

    @Override
    public double getFailureRateMovingAverage() {
        return failedCallRate.get();
    }

    @Override
    public void startThrottler() {
        throttler.start();
    }

    @Override
    public void stopThrottler() {
        throttler.stop();
    }

    @Override
    public void pauseThrottler() {
        throttler.pause();
    }

    @Override
    public void resumeThrottler() {
        throttler.resume();
    }
}
