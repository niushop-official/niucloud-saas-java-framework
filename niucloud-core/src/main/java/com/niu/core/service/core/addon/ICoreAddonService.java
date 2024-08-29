package com.niu.core.service.core.addon;

import cn.hutool.json.JSONObject;
import com.niu.core.entity.addon.Addon;
import com.niu.core.service.admin.addon.param.AddonParam;
import com.niu.core.service.core.addon.param.CoreAddonSearchParam;
import com.niu.core.service.core.addon.vo.InstallAddonListVo;
import java.util.Map;


public interface ICoreAddonService {

    /**
     * 获取插件信息
     *
     * @param key
     * @return
     */
    Addon getInfoByKey(String key);
    /**
     * 获取本地已下载的插件数
     * @return
     */
    Integer getLocalAddonCount();

    /**
     * 通过条件 查询插件数量
     * @param coreAddonSearchParam
     * @return
     */
    Integer getAddonCountByCondition(CoreAddonSearchParam coreAddonSearchParam);

    Map<String, InstallAddonListVo> getInstallAddonList();

    /**
     * 设置插件(安装或更新)
     */
    void set(Addon addon);

    JSONObject getAddonConfig(String addon);
}
