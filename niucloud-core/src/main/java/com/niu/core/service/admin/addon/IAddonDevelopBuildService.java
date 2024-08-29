package com.niu.core.service.admin.addon;

import cn.hutool.json.JSONObject;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.entity.addon.Addon;
import com.niu.core.service.admin.addon.param.AddonParam;
import com.niu.core.service.admin.addon.param.AddonSearchParam;
import com.niu.core.service.admin.addon.vo.AddonInfoVo;
import com.niu.core.service.admin.addon.vo.LocalAddonListVo;
import com.niu.core.service.core.addon.vo.InstallAddonListVo;

import java.util.Map;

/**
 * 插件打包
 */
public interface IAddonDevelopBuildService {

    /**
     * 插件打包
     * @param addon
     */
    void build(String addon);

    /**
     * 下载
     *
     * @param key
     * @return
     */
    String download(String key);
}
