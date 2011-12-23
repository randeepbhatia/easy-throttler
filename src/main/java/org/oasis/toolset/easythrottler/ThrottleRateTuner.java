package org.oasis.toolset.easythrottler;

/**
 * @author hsun
 *
 * Interface defines basic capabilities of a tuner.
 */
public interface ThrottleRateTuner {

    /**
     * Gets desired wait interval between two requests.
     * 
     * @return desired interval in nano-second.
     */
    long getCallIntervalNanos();
}
