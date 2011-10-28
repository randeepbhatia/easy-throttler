package org.oasis.toolset.easythrottler;

import java.util.HashSet;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Main entry point to use easy throttler.
 * 
 * @author hsun
 * 
 */
public class Throttler {

    private BlockingQueue<Long> tokens = new LinkedBlockingQueue<Long>(2);
    private Thread dispatcher;
    private long interval;
    private boolean running;

    public void start() {
        dispatcher = new Thread() {
            public void run() {
                running = true;
                long now = 0;
                long tick = System.currentTimeMillis();
                while (running) {
                    try {
                        tokens.put(tick);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    tick += interval;
                    now = System.currentTimeMillis();
                    tick = tick < now ? now : tick;
                }
            }
        };
        dispatcher.start();
    }

    public void stop() throws InterruptedException {
        tokens.drainTo(new HashSet<Long>());
        running = false;
        dispatcher.join();
        dispatcher = null;
    }

    public void setRate(long interval) {
        this.interval = interval;
    }

    public void throttle() throws InterruptedException {
        Long token = tokens.poll(10, TimeUnit.SECONDS);
        long waitPeriod = token - System.currentTimeMillis();
        if (waitPeriod > 0) {
            //System.out.println("Sleep " + waitPeriod);
            Thread.sleep(waitPeriod);
        }
    }

    public static void main(String[] args) throws InterruptedException {

        final int totalRuns = 10000;
        final Throttler throttler = new Throttler();
        final Random rand = new Random();
        CompletionService<String> completionService = new ExecutorCompletionService<String>(Executors.newFixedThreadPool(100));

        final long start = System.currentTimeMillis();
        throttler.setRate(1);
        throttler.start();
        
        final AtomicInteger count = new AtomicInteger(0);
        for (int i = 0; i < totalRuns; i++) {
            completionService.submit(new Callable<String>() {
                public String call() throws Exception {
                    throttler.throttle();
                    //Thread.sleep(rand.nextInt(1000));
                    int c = count.incrementAndGet();
                    if (c % 1000 == 0) {
                        System.out.println("RPS now is "
                                + (c * 1000.0 / (System.currentTimeMillis() - start)));
                    }
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
        
        System.out.println("Final Average: "
                + (totalRuns * 1000.0 / (System.currentTimeMillis() - start)));
    }
}
