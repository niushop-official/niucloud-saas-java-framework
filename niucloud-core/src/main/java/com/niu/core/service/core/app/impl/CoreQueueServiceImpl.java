package com.niu.core.service.core.app.impl;

import com.niu.core.component.context.queue.AsyncTaskQueueManager;
import com.niu.core.component.context.queue.TaskArgument;
import com.niu.core.component.context.queue.AsyncTask;
import com.niu.core.component.context.queue.AsyncTaskManager;
import com.niu.core.common.domain.Result;
import com.niu.core.service.core.app.ICoreQueueService;
import com.niu.core.service.core.app.param.CoreQueueExecParam;
import org.springframework.stereotype.Service;

@Service
public class CoreQueueServiceImpl implements ICoreQueueService {

    @Override
    public Result exec(TaskArgument taskArgument) {
        System.out.println("target bean ->" + taskArgument.getTargetBean());
        System.out.println("target method ->" + taskArgument.getTargetMethod());
        System.out.println("method argument ->" + taskArgument);
        System.out.println("execute time -> " + System.currentTimeMillis());
        try {
            Thread.sleep(10 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Result.success();
    }

    @Override
    public Result execUseQueue(CoreQueueExecParam coreQueueExecParam) {
        AsyncTask asyncTask = AsyncTaskManager.build(ICoreQueueService.class)
                .setMethodName("exec").addMethodParameter("param", coreQueueExecParam);
//        boolean result = AsyncTaskManager.delayExecute(asyncTask);
        boolean result = AsyncTaskQueueManager.delayExecute(asyncTask);
        if (result) {
            return Result.success();
        }
        return Result.fail("队列已满.");
    }

}
