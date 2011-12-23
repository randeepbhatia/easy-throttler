package org.oasis.toolset.easythrottler;

/**
 * @author hsun
 * Defines different style of throttling.
 */
public enum ThrottleStyle {
    
    /**
     * Request is blocked if throttling threshold is hit.
     */
    BLOCK, 
    
    /**
     * Request is rejected and exception is thrown if throttling threshold is hit.
     */
    FAIL
}
