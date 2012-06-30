Easy Throttler
==============

Easy Throttler is a simple Java library that helps to throttle method calls

How to use it ?
-------------

You can either explicitly emebed the throttler in the method or annotate it on the method.

Direct embdedding
+++++++++++++++++

Here are the piece of code shows how to throttle a method by directly embed the throttler inside the method::

    // throttler can be created programmatically or wired in with spring framework 
    private Throttler throttler;
    
    public void init() {
        throttler.start();
        // other init 
    }        

    public void talk() {
        throttler.throttle();
        // do real things
    }
    
Method Annotation
+++++++++++++++++

This is intended to be used with spring framework. In the spring framework context, define a throttler and a throttle aspect::

    <bean id="myThrottler" class="org.oasis.toolset.easythrottler.impl.BlockingQueueThrottler">
        ....
    </bean>

    <bean id="throttleAspect" class="org.oasis.toolset.easythrottler.annotation.ThrottleAspect">
        ....
    </bean>
    
Then we can add throttle to any public method inside a spring bean

    @Throttled(name="myThrottler")
    public void talk() {
        // do real things
    }

Current Implementations
-----------------------

There is only one implementation of Throttle which is BlockingQueueThrottler. 

There are two implementations of ThrottleRateTuner which are FixedRateTuner and DynamicRateTuner.

Other Features
--------------

- JMX MBean
- ThrottleEventListener






 