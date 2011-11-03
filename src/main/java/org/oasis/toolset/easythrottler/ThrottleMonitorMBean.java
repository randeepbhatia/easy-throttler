package org.oasis.toolset.easythrottler;

public interface ThrottleMonitorMBean {

    public double getCallRateMovingAverage();

    double getFailureRateMovingAverage();
    
    void startThrottler();

    void stopThrottler();
    
    void pauseThrottler();

    void resumeThrottler();
}
