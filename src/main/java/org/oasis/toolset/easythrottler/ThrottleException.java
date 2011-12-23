package org.oasis.toolset.easythrottler;

/**
 * @author hsun
 *
 * Base exception for all internal errors.
 */
public class ThrottleException extends RuntimeException {

    private static final long serialVersionUID = -2789346284856187146L;

    public ThrottleException() {
    }

    /**
     * Creates an exception with additional information.
     * 
     * @param reason explains what is the error.
     */
    public ThrottleException(String reason) {
        super(reason);
    }

}
