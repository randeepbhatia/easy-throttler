package org.oasis.toolset.easythrottler;

/**
 * @author hsun
 *
 * Indicates a request is rejected due to throttling. 
 */
public class RequestThrottledException extends RuntimeException {

    private static final long serialVersionUID = -2789346284856187122L;

    public RequestThrottledException() {
    }

    /**
     * Creates the exception with additional information.
     * 
     * @param reason additional information on why the request is throttled.
     */
    public RequestThrottledException(String reason) {
        super(reason);
    }

}
