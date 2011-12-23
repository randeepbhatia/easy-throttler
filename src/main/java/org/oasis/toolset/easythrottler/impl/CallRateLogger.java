package org.oasis.toolset.easythrottler.impl;

import java.util.Timer;
import java.util.TimerTask;

import org.oasis.toolset.easythrottler.Startable;
import org.oasis.toolset.easythrottler.ThrottleEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author hsun
 *
 * Used to log request rates.
 * 
 */
public class CallRateLogger implements ThrottleEventListener, Startable {

    private static final Logger LOG = LoggerFactory.getLogger(CallRateLogger.class);

    private CallRateCounter rateCounter;
    private Timer monitorTimer;
    private long monitorIntervalMillis;
    
    private long lastSuccess;
    private long lastFailure;

    public CallRateLogger(long monitorIntervalMillis) {
        this.rateCounter = new CallRateCounter();
        this.monitorIntervalMillis = monitorIntervalMillis;
    }

    @Override
    public void start() {
        monitorTimer = new Timer("CallRateMonitor Timer", true /* run as daemon */);
        monitorTimer.schedule(new TimerTask() {

            @Override
            public void run() {
                long successes = rateCounter.getSuccessCount();
                long failures = rateCounter.getFailureCount();
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

    @Override
    public void stop() {
        monitorTimer.cancel();
    }

    @Override
    public void succeded() {
        rateCounter.incSuccess();
    }

    @Override
    public void failed() {
        rateCounter.incFailure();
    }
}
