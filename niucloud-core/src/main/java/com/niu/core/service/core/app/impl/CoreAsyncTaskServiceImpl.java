package com.niu.core.service.core.app.impl;

import com.niu.core.common.domain.Result;
import com.niu.core.service.core.app.ICoreAsyncTaskService;
import com.niu.core.service.core.app.param.CoreAsyncTaskParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CoreAsyncTaskServiceImpl implements ICoreAsyncTaskService {

    @Async
    @Override
    public Result executeAsyncTask(CoreAsyncTaskParam coreAsyncTaskParam) {
        log.warn("我是异步执行的...............");
        try {
            Thread.sleep(3 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.warn("异步执行完成了...............");
        return Result.success();
    }

    @Override
    public Result execute(CoreAsyncTaskParam coreAsyncTaskParam) {
        log.warn("我是同步执行的...............");
        try {
            Thread.sleep(3 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.warn("同步执行完成了...............");
        return Result.success();
    }

}
