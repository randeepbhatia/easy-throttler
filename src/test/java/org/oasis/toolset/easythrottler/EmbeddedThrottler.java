package org.oasis.toolset.easythrottler;

import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class EmbeddedThrottler implements InitializingBean {

    Random random;
    Throttler throttler;

    public void setThrottler(Throttler throttler) {
        this.throttler = throttler;
    }

    @Override
    public void afterPropertiesSet() throws Exception {

        random = new Random();
        throttler.start();
    }
    
    public void talk() throws InterruptedException {
        throttler.throttle();
        Thread.sleep(random.nextInt(100));
    }

    public static void main(String[] args) throws Exception {

        int totalRuns = 600;

        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(100);
        CompletionService<String> completionService = new ExecutorCompletionService<String>(executor);
        final long start = System.currentTimeMillis();

        ApplicationContext context = new ClassPathXmlApplicationContext("test-throttle.xml");
        final EmbeddedThrottler tester = context.getBean("embedded", EmbeddedThrottler.class);

        for (int i = 0; i < totalRuns; i++) {
            completionService.submit(new Callable<String>() {
                public String call() throws Exception {
                    tester.talk();
                    return "OK";
                }
            });
        }
        int completed = 0;
        while (completed < totalRuns) {
            completionService.take();
            completed++;
        }

        executor.shutdown();
        System.out.println("Final Average: "
                + (totalRuns * 1000.0 / (System.currentTimeMillis() - start)));
    }
}
