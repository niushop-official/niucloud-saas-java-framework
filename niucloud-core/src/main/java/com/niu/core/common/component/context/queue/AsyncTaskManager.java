package com.niu.core.common.component.context.queue;

import com.niu.core.common.component.context.SpringContext;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public final class AsyncTaskManager {

    private static final AsyncTaskManager asyncTaskManager = new AsyncTaskManager();

    private static ArrayBlockingQueue<AsyncTask> taskQueue;

    private static ExecutorService taskExecutorService;

    private final AtomicInteger taskCounter = new AtomicInteger();

    private boolean starting = false;

    private AsyncTaskManager() {

    }

    public static AsyncTaskManager getInstance() {
        return asyncTaskManager;
    }

    /**
     *
     */
    public synchronized void startup() {
        this.startup(5, 100, 1);
    }

    /**
     * @param nThreads
     * @param queueCapacity
     * @param waitForSeconds
     */
    public synchronized void startup(int nThreads, int queueCapacity, int waitForSeconds) {
        if (!starting) {
            starting = true;
            ExecutorService systemExecutorService = (ExecutorService) SpringContext.getBean("executorService");
            this.taskQueue = new ArrayBlockingQueue<>(queueCapacity);
            this.taskExecutorService = Executors.newFixedThreadPool(nThreads);
            systemExecutorService.submit(() -> {
                while (true) {
                    try {
                        AsyncTask asyncTask = this.taskQueue.poll();
                        if (taskCounter.get() >= nThreads) {
                            // log.info("async task sleep......");
                            TimeUnit.SECONDS.sleep(waitForSeconds);
                        } else {
                            if (asyncTask == null) {
                                // log.info("async task sleep......");
                                TimeUnit.SECONDS.sleep(waitForSeconds);
                            } else {
                                callAsyncTask(asyncTask);
                            }
                        }
                    } catch (Exception e) {
                        // ignore
                    }
                }
            });
        }
    }

    /**
     * @param asyncTask
     */
    private void callAsyncTask(AsyncTask asyncTask) {
        Object target = null;
        if (asyncTask.getBeanType() == 1) {
            target = SpringContext.getBean(asyncTask.getBeanName());
        } else if (asyncTask.getBeanType() == 2) {
            target = SpringContext.getBean(asyncTask.getBeanClass());
        }
        if (target == null) {
            if (asyncTask.getBeanType() == 1) {
                log.error("SpringContext Bean " + asyncTask.getBeanName() + " does not exist.");
            } else if (asyncTask.getBeanType() == 2) {
                log.error("SpringContext Bean " + asyncTask.getBeanClass() + " does not exist.");
            }
            return;
        }
        this.submitTask(target, asyncTask);
    }


    /**
     * @param targetBean
     * @param asyncTask
     */
    private void submitTask(final Object targetBean, AsyncTask asyncTask) {
        TaskArgument taskArgument = new TaskArgument();
        taskArgument.setTargetBean(targetBean);
        taskArgument.setTargetMethod(asyncTask.getMethodName());
        taskArgument.putAll(asyncTask.getMethodParamsMap());
        log.info("execute [" + targetBean + "] method [" + asyncTask.getMethodName() + "]");
        taskCounter.incrementAndGet();
        this.taskExecutorService.submit(() -> {
            try {
                Class<?> targetBeanClass = targetBean.getClass();
                Method method = targetBeanClass.getMethod(taskArgument.getTargetMethod(), taskArgument.getClass());
                method.invoke(targetBean, (Object) taskArgument);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                taskCounter.decrementAndGet();
            }
        });
    }

    public boolean isStarting() {
        return starting;
    }

    /**
     *
     */
    public void shutdown() {
        if (taskExecutorService != null && !taskExecutorService.isShutdown()) {
            taskExecutorService.shutdown();
        }
    }

    /**
     * 异步执行
     *
     * @param asyncTask
     * @return
     */
    public static synchronized boolean delayExecute(AsyncTask asyncTask) {
        if (!AsyncTaskManager.getInstance().isStarting()) {
            AsyncTaskManager.getInstance().startup();
        }
        return taskQueue.offer(asyncTask);
    }

    public static AsyncTask build(String beanName) {
        return new AsyncTask(beanName);
    }

    public static AsyncTask build(Class<?> beanClass) {
        return new AsyncTask(beanClass);
    }

}
