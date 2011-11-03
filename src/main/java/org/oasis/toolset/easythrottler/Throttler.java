package org.oasis.toolset.easythrottler;

public interface Throttler extends Startable {

    String getName();
    void throttle();
    void pause();
    void resume();
    void registerThrottleEventListener(ThrottleEventListener listener);
    void unregisterThrottleEventListener(ThrottleEventListener listener);
    void setThrottleRateTuner(ThrottleRateTuner tuner);
}
