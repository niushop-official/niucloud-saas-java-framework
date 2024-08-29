package com.niu.core.service.core.app;

import com.niu.core.common.domain.Result;
import com.niu.core.service.core.app.param.CoreAsyncTaskParam;

public interface ICoreAsyncTaskService {

    /**
     * @param coreAsyncTaskParam
     * @return
     */
    public Result executeAsyncTask(CoreAsyncTaskParam coreAsyncTaskParam);

    /**
     * @param coreAsyncTaskParam
     * @return
     */
    public Result execute(CoreAsyncTaskParam coreAsyncTaskParam);

}
