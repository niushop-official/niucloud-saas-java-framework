package com.niu.core.service.admin.upgrade;

import cn.hutool.json.JSONObject;
import com.niu.core.service.admin.upgrade.vo.UpgradeTaskVo;

public interface IUpgradeService {

    /**
     * 获取升级内容
     * @param addon
     * @return
     */
    JSONObject getUpgradeContent(String addon);

    /**
     * 升级
     * @param addon
     */
    UpgradeTaskVo upgrade(String addon);

    /**
     * 获取正在进行的升级任务
     * @return
     */
    UpgradeTaskVo getUpgradeTask();

    /**
     * 清除升级任务
     */
    void clearUpgradeTask(int delayed);

    /**
     * 执行升级步骤
     *
     */
    void execute();
}
