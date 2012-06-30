package org.oasis.toolset.easythrottler.impl;

import java.lang.management.ManagementFactory;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.oasis.toolset.easythrottler.ThrottleEventListener;
import org.oasis.toolset.easythrottler.ThrottleMonitor;
import org.oasis.toolset.easythrottler.ThrottleMonitorMBean;
import org.oasis.toolset.easythrottler.ThrottleRateTuner;

public class TestBlockingQueueThrottler {

    public static void main(String[] args) throws Exception {

        final int totalRuns = 6000;

        final BlockingQueueThrottler throttler = new BlockingQueueThrottler.Builder("Test Throttler").withBlockStyle(10000)
                .build();
        CallRateLogger logger = new CallRateLogger(5000L);
        // ThrottleRateTuner tuner = new DynamicRateTuner(100.0, 5000L, 0.5, 2.0);
        ThrottleRateTuner tuner2 = new FixedRateTuner(500.0);
        ThrottleMonitorMBean mbean = new ThrottleMonitor(throttler, 5000L);

        MBeanServer mserver = ManagementFactory.getPlatformMBeanServer();
        mserver.registerMBean(mbean,
                new ObjectName("org.oasis.toolset.easythrottler:type=ThrottleMonitor"));

        throttler.registerThrottleEventListener((ThrottleEventListener) logger);
        throttler.setThrottleRateTuner(tuner2);
        throttler.start();

        final Random rand = new Random();
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(100);
        CompletionService<String> completionService = new ExecutorCompletionService<String>(executor);
        final long start = System.currentTimeMillis();

        for (int i = 0; i < totalRuns; i++) {
            completionService.submit(new Callable<String>() {
                public String call() throws Exception {
                    throttler.throttle();
                    Thread.sleep(rand.nextInt(100));
                    return "OK";
                }
            });
        }
        int completed = 0;
        while (completed < totalRuns) {
            completionService.take();
            completed++;
        }

        throttler.stop();
        executor.shutdown();

        System.out.println("Final Average: "
                + (totalRuns * 1000.0 / (System.currentTimeMillis() - start)));
    }
}
