package org.oasis.toolset.easythrottler.annotation;


/**
 * @author hsun
 *
 * Used to annotate a method that requires throttling. This is intended to be used 
 * with @ThrottleAspect in a Spring framework context.
 * 
 */
public @interface Throttled {

    /**
     * The name of the throttler bean.
     * 
     * @return throttler bean name.
     */
    String name();
}
