package com.niu.core.service.core.app;

import com.niu.core.common.component.context.queue.TaskArgument;
import com.niu.core.common.domain.Result;
import com.niu.core.service.core.app.param.CoreQueueExecParam;

public interface ICoreQueueService {

    /**
     * @param taskArgument
     * @return
     */
    public Result exec(TaskArgument taskArgument);

    /**
     * @param coreQueueExecParam
     * @return
     */
    public Result execUseQueue(CoreQueueExecParam coreQueueExecParam);

}
