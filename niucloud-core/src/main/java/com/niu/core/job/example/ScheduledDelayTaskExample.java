package com.niu.core.job.example;

import java.util.concurrent.*;

/**
 * 延时任务的实现
 */
public class ScheduledDelayTaskExample {

    public static void main(String[] args) {
        // 创建线程池
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(4, new ThreadFactory() {

            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                thread.setName("delay-task");
                return thread;
            }
        });

        // 延时任务一
        scheduledExecutorService.schedule(() -> {
            System.out.println(Thread.currentThread() + ", run(). 延时任务....");
        }, 2, TimeUnit.SECONDS);
        System.out.println("run()...");

        // 延时任务二
        Callable callable = new Callable<Long>() {

            @Override
            public Long call() throws Exception {
                System.out.println(Thread.currentThread() + ", call(). 延时任务....");
                return System.currentTimeMillis();
            }
        };
        ScheduledFuture<Long> scheduledFuture1 = scheduledExecutorService.schedule(callable, 3, TimeUnit.SECONDS);
        System.out.println("call()...1...");
        ScheduledFuture<Long> scheduledFuture2 = scheduledExecutorService.schedule(callable, 3, TimeUnit.SECONDS);
        System.out.println("call()...2...");
        Long result1 = null;
        Long result2 = null;
        try {
            // 直到任务全部执行完毕才返回结果
            result1 = scheduledFuture1.get();
            result2 = scheduledFuture2.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        System.out.println(result1);
        System.out.println(result2);

        // 关闭线程池
        if (!scheduledExecutorService.isShutdown()) {
            scheduledExecutorService.shutdown();
        }
    }

}
