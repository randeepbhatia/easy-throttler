package org.oasis.toolset.easythrottler.annotation;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.oasis.toolset.easythrottler.Throttler;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

/**
 * @author hsun
 * 
 * Adds throttling to method annotated by @Throttled.
 *
 */
@Aspect
public class ThrottleAspect implements BeanFactoryAware {

    private BeanFactory beanFactory;

    @Pointcut("execution(@org.oasis.toolset.easythrottler.annotation.Throttled * *(..))")
    public void ThrottledMethod() {
    }

    @Around("ThrottledMethod() && @annotation(anno)")
    public Object throttle(ProceedingJoinPoint pjp, Throttled anno) throws Throwable {

        Throttler throttler = null;
        String throttlerName = anno.name();
        if (throttlerName == null) {
            // name is required to get the real throttler
            throw new IllegalArgumentException();
        } else {
            Object bean = beanFactory.getBean(throttlerName);
            if (bean == null || !(bean instanceof Throttler)) {
                throw new IllegalArgumentException();
            } else {
                throttler = (Throttler) bean;
            }
        }

        throttler.throttle();
        return pjp.proceed();
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }
}
