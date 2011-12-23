package org.oasis.toolset.easythrottler;

/**
 * @author hsun
 *
 * Listener that receives events on throttling result.
 */
public interface ThrottleEventListener {

    /**
     * A request is served.
     */
    void succeded();

    /**
     * A request is throttled.
     */
    void failed();
}
