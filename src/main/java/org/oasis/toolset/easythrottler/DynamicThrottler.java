package org.oasis.toolset.easythrottler;

public interface DynamicThrottler extends Throttler {

    void setFeedbackProvider(FeedbakProvider fbp); 
}
