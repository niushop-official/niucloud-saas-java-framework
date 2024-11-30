package com.niu.core.listener.sys;

import cn.hutool.core.util.ObjectUtil;
import com.niu.core.common.component.context.SpringContext;
import com.niu.core.common.component.context.WebAppEnvs;
import com.niu.core.common.component.initialize.InitializeProvider;
import com.niu.core.common.component.initialize.annotation.Initializer;
import com.niu.core.service.admin.upgrade.IUpgradeService;
import com.niu.core.service.admin.upgrade.vo.UpgradeTaskVo;
import org.springframework.context.ApplicationContext;

@Initializer("system")
public class SystemRestartListener implements InitializeProvider {

    public void init(ApplicationContext applicationContext) throws Exception {
        if (WebAppEnvs.get().envType.equals("dev")) {
            IUpgradeService upgradeService = SpringContext.bean(IUpgradeService.class);
            UpgradeTaskVo task = upgradeService.getUpgradeTask();
            if (ObjectUtil.isNotEmpty(task) && ObjectUtil.isNotEmpty(task.getStatus()) && task.getStatus().equals("restarting")) {
                task.setStatus("restarted");
                upgradeService.setUpgradeTaskCache(task);
            }
        }
    }
}
