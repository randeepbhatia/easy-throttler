package org.oasis.toolset.easythrottler;

public interface Throttler {

    String getName();
    void on();
    void off();
    void throttle();
    void setThrottleRate(double desiredRate);
    void registerThrottleEventListener(ThrottleEventListener listener);
    void unregisterThrottleEventListener(ThrottleEventListener listener);
    void adjustThrottleRate(double percentage);
}
