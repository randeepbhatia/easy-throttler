package org.oasis.toolset.easythrottler;

public class RequestThrottledException extends RuntimeException {

    private static final long serialVersionUID = -2789346284856187122L;

    public RequestThrottledException() {
    }

    public RequestThrottledException(String reason) {
        super(reason);
    }

}
