package org.oasis.toolset.easythrottler;

public interface ThrottleEventListener {

    void start();
    
    void stop();
    
    void succeded();

    void failed();
}
