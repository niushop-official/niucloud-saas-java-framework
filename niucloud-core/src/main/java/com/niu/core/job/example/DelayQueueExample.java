package com.niu.core.job.example;

import java.time.LocalDateTime;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * 延时队列的案例
 */
public class DelayQueueExample {

    public static class DelayTask implements Delayed {

        private Integer taskId;

        private long exeTime;

        DelayTask(Integer taskId, long exeTime) {
            this.taskId = taskId;
            this.exeTime = exeTime;
        }

        @Override
        public long getDelay(TimeUnit unit) {
            return exeTime - System.currentTimeMillis();
        }

        @Override
        public int compareTo(Delayed o) {
            DelayTask t = (DelayTask) o;
            if (this.exeTime - t.exeTime <= 0) {
                return -1;
            } else {
                return 1;
            }
        }

        @Override
        public String toString() {
            return "DelayTask{" +
                    "taskId=" + taskId +
                    ", exeTime=" + exeTime +
                    '}';
        }
    }

    public static void main(String[] args) throws InterruptedException {
        // DelayQueue是JDK中java.util.concurrent包下的一种无界阻塞队列，底层是优先队列 PriorityQueue。
        // 对于放到队列中的任务，可以按照到期时间进行排序，只需要取已经到期的元素处理即可。

        // 新建3个任务，并依次设置超时时间为 20s 10s 30s
        DelayTask d1 = new DelayTask(1, System.currentTimeMillis() + 20000L);
        DelayTask d2 = new DelayTask(2, System.currentTimeMillis() + 10000L);
        DelayTask d3 = new DelayTask(3, System.currentTimeMillis() + 30000L);

        DelayQueue<DelayTask> queue = new DelayQueue<>();
        queue.add(d1);
        queue.add(d2);
        queue.add(d3);
        int size = queue.size();
        System.out.println("当前时间是：" + LocalDateTime.now());

        // 从延时队列中获取元素， 将输出 d2 、d1 、d3
        for (int i = 0; i < size; i++) {
            System.out.println(queue.take() + " ------ " + LocalDateTime.now());
        }
    }

}
