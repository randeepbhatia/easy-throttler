package org.oasis.toolset.easythrottler.impl;

import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CallRateLogger {

    private static final Logger LOG = LoggerFactory.getLogger(CallRateLogger.class);

    private CallRateMonitor rateMonitor;
    private long monitorIntervalMillis;
    private Timer monitorTimer;
    
    private long lastSuccess;
    private long lastFailure;

    public CallRateLogger(CallRateMonitor monitor, long monitorIntervalMillis) {
        this.rateMonitor = monitor;
        this.monitorIntervalMillis = monitorIntervalMillis;
    }

    public void start() {
        monitorTimer = new Timer("CallRateMonitor Timer", true);
        monitorTimer.schedule(new TimerTask() {

            @Override
            public void run() {
                long successes = rateMonitor.getSuccessCount();
                long failures = rateMonitor.getFailureCount();
                if (successes >= lastSuccess && failures >= lastFailure) {
                LOG.info("Call rates. Success: {} Failure: {}",
                        ((successes - lastSuccess) * 1000.0 / monitorIntervalMillis),
                        ((failures - lastFailure) * 1000.0 / monitorIntervalMillis));
                } else {
                    LOG.info("Skip a cycle as the counters may need to be reset");
                    // in rare cases when we exceed the Long.MAX_VALUE, skip a cycle
                }
                lastSuccess = successes;
                lastFailure = failures;
            }

        }, 0, monitorIntervalMillis);
    }

    public void stop() {
        monitorTimer.cancel();
    }
}
