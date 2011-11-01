package org.oasis.toolset.easythrottler;

public class ThrottleException extends RuntimeException {

    private static final long serialVersionUID = -2789346284856187146L;

    public ThrottleException() {
    }

    public ThrottleException(String reason) {
        super(reason);
    }

}
