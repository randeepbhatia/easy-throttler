package org.oasis.toolset.easythrottler;

/**
 * @author hsun
 *
 * Interafce defines the basic capabilities of any throttlerimplementation.
 */
public interface Throttler extends Startable {

    /**
     * Gets the throttle name.
     * 
     * @return the name of this throttler instance.
     */
    String getName();
    
    /**
     * Throttles a request.
     */
    void throttle();
    
    /**
     * Pauses throttling.
     */
    void pause();
    
    /**
     * Resumes throttling. 
     */
    void resume();
    
    /**
     * Registers an event listener.
     * 
     * @param listener a ThrottleEventListener
     */
    void registerThrottleEventListener(ThrottleEventListener listener);
    
    /**
     * Unregisters an event listener.
     * 
     * @param listener a ThrottleEventListener instance.
     */
    void unregisterThrottleEventListener(ThrottleEventListener listener);
    
    /**
     * Sets a "tuner" which gives feedback to the throttling algorithm.
     * 
     * @param tuner a ThrottleRateTuner instance.
     */
    void setThrottleRateTuner(ThrottleRateTuner tuner);
}
