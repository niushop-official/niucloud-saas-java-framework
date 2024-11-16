package com.niu.core.common.component.context.queue;

import com.niu.core.common.component.context.SpringContext;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.concurrent.*;

@Slf4j
public final class AsyncTaskQueueManager {

    private static final AsyncTaskQueueManager asyncTaskQueueManager = new AsyncTaskQueueManager();

    private static ArrayBlockingQueue<Runnable> taskRunnableQueue;

    private static ExecutorService taskExecutorService;

    private boolean starting = false;

    private AsyncTaskQueueManager() {

    }

    public static AsyncTaskQueueManager getInstance() {
        return asyncTaskQueueManager;
    }

    public boolean isStarting() {
        return starting;
    }

    /**
     *
     */
    public synchronized void startup() {
        this.startup(5, 100);
    }

    /**
     * @param nThreads
     * @param queueCapacity
     */
    public synchronized void startup(int nThreads, int queueCapacity) {
        if (!starting) {
            starting = true;
            this.taskRunnableQueue = new ArrayBlockingQueue<>(queueCapacity);
            this.taskExecutorService = new ThreadPoolExecutor(nThreads, nThreads, 1, TimeUnit.MILLISECONDS, this.taskRunnableQueue,
                    Executors.defaultThreadFactory(), new ThreadPoolExecutor.CallerRunsPolicy());
        }
    }

    /**
     *
     */
    public synchronized void shutdown() {
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
    public boolean execute(AsyncTask asyncTask) {
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
            return false;
        }
        this.submitTask(target, asyncTask);
        return true;
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
        this.taskExecutorService.submit(() -> {
            try {
                Class<?> targetBeanClass = targetBean.getClass();
                Method method = targetBeanClass.getMethod(taskArgument.getTargetMethod(), taskArgument.getClass());
                method.invoke(targetBean, (Object) taskArgument);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
            }
        });
    }

    /**
     * 异步执行
     *
     * @param asyncTask
     * @return
     */
    public static synchronized boolean delayExecute(AsyncTask asyncTask) {
        if (!AsyncTaskQueueManager.getInstance().isStarting()) {
            AsyncTaskQueueManager.getInstance().startup();
        }
        return AsyncTaskQueueManager.getInstance().execute(asyncTask);
    }

}
