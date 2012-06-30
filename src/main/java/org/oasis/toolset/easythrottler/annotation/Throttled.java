package org.oasis.toolset.easythrottler.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


/**
 * @author hsun
 *
 * Used to annotate a method that requires throttling. This is intended to be used 
 * with @ThrottleAspect in a Spring framework context.
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Throttled {

    /**
     * The name of the throttler bean.
     * 
     * @return throttler bean name.
     */
    String name();
}
