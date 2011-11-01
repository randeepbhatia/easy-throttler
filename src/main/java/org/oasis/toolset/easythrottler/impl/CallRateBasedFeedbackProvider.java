package org.oasis.toolset.easythrottler.impl;

import java.util.Timer;
import java.util.TimerTask;

import org.oasis.toolset.easythrottler.DynamicThrottler;
import org.oasis.toolset.easythrottler.FeedbakProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CallRateBasedFeedbackProvider implements FeedbakProvider {

    private static final Logger LOG = LoggerFactory.getLogger(CallRateBasedFeedbackProvider.class);

    private DynamicThrottler throttler;
    private CallRateMonitor rateMonitor;
    private long monitorIntervalMillis;
    private Timer monitorTimer;
    private double desiredCallRate;
    private double threshold;
    private double adjustStep;
    
    private long lastSuccess;

    public CallRateBasedFeedbackProvider(DynamicThrottler throttler, CallRateMonitor monitor, long monitorIntervalMillis, 
            double desiredCallRate, double threshold, double adjustStep) {
        this.throttler = throttler;
        this.rateMonitor = monitor;
        this.monitorIntervalMillis = monitorIntervalMillis;
        this.desiredCallRate = desiredCallRate;
        this.threshold = threshold;
        this.adjustStep = adjustStep;
    }

    @Override
    public void start() {
        monitorTimer = new Timer("CallRateMonitor Timer", true);
        monitorTimer.schedule(new TimerTask() {

            @Override
            public void run() {
                long successes = rateMonitor.getSuccessCount();
                if (successes >= lastSuccess) {
                    double realCallRate = (successes - lastSuccess) * 1000.0 / monitorIntervalMillis;
                    double delta = desiredCallRate - realCallRate;
                    if (Math.abs(delta) > threshold) {
                        double changePercentage = Math.signum(delta) * adjustStep;
                        throttler.adjustThrottleRate(changePercentage);
                        LOG.info("Real call rate is {}. adjust percentage is {}", realCallRate, changePercentage);
                    }
                } else {
                    LOG.info("Skip a cycle as the counters may need to be reset");
                    // in rare cases when we exceed the Long.MAX_VALUE, skip a cycle
                }
                lastSuccess = successes;
            }
        }, 0, monitorIntervalMillis);
    }

    @Override
    public void stop() {
        monitorTimer.cancel();
    }
}
