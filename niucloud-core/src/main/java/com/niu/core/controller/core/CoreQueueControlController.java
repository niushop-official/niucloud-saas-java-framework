package com.niu.core.controller.core;

import com.niu.core.common.domain.Result;
import com.niu.core.service.core.addon.AddonInstallJavaTools;
import com.niu.core.service.core.app.ICoreQueueService;
import com.niu.core.service.core.app.param.CoreQueueExecParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 通过队列控制将并行请求转发为串行异步处理
 * todo 参考代码
 */
@RestController
@RequestMapping("/core/queue")
public class CoreQueueControlController {

    @Autowired
    private ICoreQueueService coreQueueService;

    /**
     * 本地插件列表
     *
     * @return
     */
    @GetMapping("/exec")
    public Result<Object> exec(CoreQueueExecParam coreQueueExecParam) {
        return coreQueueService.execUseQueue(coreQueueExecParam);
    }

}
