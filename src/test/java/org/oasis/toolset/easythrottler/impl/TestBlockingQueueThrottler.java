package org.oasis.toolset.easythrottler.impl;

import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class TestBlockingQueueThrottler {

    public static void main(String[] args) throws InterruptedException {

        final int totalRuns = 6000;
        
        final BlockingQueueThrottler throttler = new BlockingQueueThrottler.Builder("Test Throttler", 100.0)
            .withBlockStyle(10000).build();
        CallRateMonitor monitor = new CallRateMonitor();
        throttler.registerThrottleEventListener(monitor);
        CallRateLogger logger = new CallRateLogger(monitor, 5000L);
        CallRateBasedFeedbackProvider p = new CallRateBasedFeedbackProvider(throttler, monitor, 5000L, 100.0, 0.5, 2.0);
        //throttler.setFeedbackProvider(p);
        throttler.on();
        logger.start();
        
        final Random rand = new Random();
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(100);
        CompletionService<String> completionService = new ExecutorCompletionService<String>(executor);
        final long start = System.currentTimeMillis();
        
        for (int i = 0; i < totalRuns; i++) {
            completionService.submit(new Callable<String>() {
                public String call() throws Exception {
                    throttler.throttle();
                    //Thread.sleep(rand.nextInt(100));
                    return "OK";
                }
            });
        }
        int completed = 0;
        while (completed < totalRuns) {
            completionService.take();
            completed++;
        }
        
        logger.stop();
        throttler.off();
        throttler.unregisterThrottleEventListener(monitor);
        executor.shutdown();
        
        System.out.println("Final Average: "
                + (totalRuns * 1000.0 / (System.currentTimeMillis() - start)));
    }
}
