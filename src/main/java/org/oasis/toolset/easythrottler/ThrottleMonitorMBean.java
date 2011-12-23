package org.oasis.toolset.easythrottler;

/**
 * @author hsun
 *
 * Defines the capabilities of a JMX monitor bean for throttler.
 */
public interface ThrottleMonitorMBean {

    /**
     * Gets the current request success rate.
     * 
     * @return request success rate per second
     */
    double getCallRateMovingAverage();

    /**
     * Gets the current request failure rate due to throttling.
     * 
     * @return request failure rate per second
     */
    double getFailureRateMovingAverage();
    
    /**
     *  Starts the throttler.
     */
    void startThrottler();

    /**
     *  Stops the throttler.
     */
    void stopThrottler();
    
    /**
     *  Pauses the throttler.
     */
    void pauseThrottler();

    /**
     *  Resumes the throttler.
     */
    void resumeThrottler();
}
