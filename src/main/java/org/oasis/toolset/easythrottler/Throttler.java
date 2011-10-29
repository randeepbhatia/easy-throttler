package org.oasis.toolset.easythrottler;

import java.util.HashSet;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
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

    private static final Long TOKEN = 0L; 
    private BlockingQueue<Long> tokens = new LinkedBlockingQueue<Long>(1);
    private Timer dispatchTimer;
    private Timer reportTimer;
    private long interval;
    private AtomicInteger count;
    private long lastTimestamp;

    public void start() {
        
        count = new AtomicInteger(0);
        lastTimestamp = System.currentTimeMillis();

        dispatchTimer = new Timer("Scheduler", true);
        dispatchTimer.schedule(new TimerTask() {
            
            @Override
            public void run() {
                try {
                    tokens.put(TOKEN);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }, 0, interval);
        
        reportTimer = new Timer("Reporter", true);
        reportTimer.schedule(new TimerTask() {
            
            @Override
            public void run() {
                    double rate = count.getAndSet(0) * 1000.0 / 5000.0;
                    System.out.println("RPS now is " + rate);
            }
        }, 0, 5000);
    }

    public void stop() throws InterruptedException {
        tokens.drainTo(new HashSet<Long>());
        dispatchTimer.cancel();
        reportTimer.cancel();
    }

    public void setRate(long interval) {
        this.interval = interval;
    }

    public void throttle() throws InterruptedException {
        tokens.poll(10, TimeUnit.SECONDS);
        count.incrementAndGet();
    }

    public static void main(String[] args) throws InterruptedException {

        final int totalRuns = 10000;
        final Throttler throttler = new Throttler();
        final Random rand = new Random();
        CompletionService<String> completionService = new ExecutorCompletionService<String>(Executors.newFixedThreadPool(100));

        final long start = System.currentTimeMillis();
        throttler.setRate(20);
        throttler.start();
        
        for (int i = 0; i < totalRuns; i++) {
            completionService.submit(new Callable<String>() {
                public String call() throws Exception {
                    throttler.throttle();
                    
                    //Thread.sleep(rand.nextInt(1000));
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
