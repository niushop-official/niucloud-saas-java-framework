package com.niu.core.controller.core;

import com.niu.core.common.domain.Result;
import com.niu.core.service.core.app.ICoreAsyncTaskService;
import com.niu.core.service.core.app.param.CoreAsyncTaskParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 异步任务处理案例
 * <p>
 *  todo 参考代码
 */
@RestController
@RequestMapping("/core/task")
public class CoreAsyncTaskController {

    @Autowired
    ICoreAsyncTaskService coreAsyncTaskService;

    /**
     * 同步执行
     *
     * @param coreAsyncTaskParam
     * @return
     */
    @GetMapping("/sync")
    public Result startSyncTask(CoreAsyncTaskParam coreAsyncTaskParam) {
        Result result = coreAsyncTaskService.execute(coreAsyncTaskParam);
        return result;
    }

    /**
     * 异步执行
     *
     * @param coreAsyncTaskParam
     * @return
     */
    @GetMapping("/async")
    public Result startAsyncTask(CoreAsyncTaskParam coreAsyncTaskParam) {
        coreAsyncTaskService.executeAsyncTask(coreAsyncTaskParam);
        return Result.success("异步任务开始");
    }

}
