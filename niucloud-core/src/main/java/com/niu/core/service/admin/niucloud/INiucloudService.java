package com.niu.core.service.admin.niucloud;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.niu.core.service.admin.niucloud.param.GetAppVersionListParam;
import com.niu.core.service.admin.niucloud.vo.*;
import com.niu.core.service.core.niucloud.param.SetAuthorizeParam;

import java.util.List;
import java.util.Map;

public interface INiucloudService {

    /**
     * 获取框架最新版本
     * @return
     */
    FrameWorkVersion getFrameworkLastVersion();

    /**
     * 获取框架更新记录
     *
     * @return
     */
    List<FrameworkVersionListVo> getFrameworkVersionList();

    /**
     * 获取授权信息
     *
     * @return
     */
    AuthInfoVo getAuthinfo();

    /**
     * 绑定授权信息
     *
     */
    void setAuthorize(SetAuthorizeParam param);

    /**
     * 获取已购买应用
     *
     * @return
     */
    List<ModuleListVo> getModuleList();

    /**
     * 获取操作token
     *
     * @param action
     * @param query
     * @return
     */
    JSONObject getActionToken(String action, Map<String, Object> query);

    /**
     * 校验key是否被占用
     * @param key
     * @return
     */
    Boolean checkKey(String key);

    /**
     * 获取应用/插件的版本更新记录
     * @return
     */
    List<AppVersionListVo> getAppVersionList(GetAppVersionListParam param);
}
