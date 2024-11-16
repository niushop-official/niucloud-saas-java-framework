package com.niu.core.common.component.context.task;

import com.niu.core.common.component.context.SpringContext;

import java.util.concurrent.ExecutorService;

public class ThreadPoolManager {

    /**
     * 执行一个任务
     *
     * @param task
     */
    public static void runTask(Runnable task) {
        ExecutorService executorService = SpringContext.bean("executorService");
        executorService.submit(task);
    }

}
