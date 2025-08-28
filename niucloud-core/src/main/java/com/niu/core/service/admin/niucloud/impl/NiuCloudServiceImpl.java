package com.niu.core.service.admin.niucloud.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.NiucloudUtils;
import com.niu.core.service.admin.niucloud.INiucloudService;
import com.niu.core.service.admin.niucloud.param.GetAppVersionListParam;
import com.niu.core.service.admin.niucloud.vo.*;
import com.niu.core.service.core.niucloud.param.SetAuthorizeParam;
import com.niu.core.service.core.niucloud.ICoreNiucloudConfigService;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.*;

/**
 * 获取官方框架服务层
 */
@Service
public class NiuCloudServiceImpl implements INiucloudService {

    @Resource
    ICoreNiucloudConfigService coreNiucloudConfigService;

    /**
     * 获取框架最新版本
     * @return
     */
    @Override
    public FrameWorkVersion getFrameworkLastVersion() {
        NiucloudUtils instance = NiucloudUtils.getInstance();

        Map<String, Object> query = new HashMap<>();
        query.put("product_key", instance.getProductKey());

        JSONObject data = NiucloudUtils.Niucloud.get("store/framework/lastversion", query).getJSONObject("data");

        FrameWorkVersion frameWorkVersion = new FrameWorkVersion();
        if (data != null) {
            frameWorkVersion.setLastVersion(data.getStr("last_version", ""));
            frameWorkVersion.setLastUpdateTime(data.getStr("last_update_time", ""));
        }

        return frameWorkVersion;
    }

    /**
     * 获取框架版本记录
     *
     */
    public List<FrameworkVersionListVo> getFrameworkVersionList() {
        NiucloudUtils instance = NiucloudUtils.getInstance();

        Map<String, Object> query = new HashMap<>();
        query.put("product_key", instance.getProductKey());

        JSONArray data = NiucloudUtils.Niucloud.get("store/framework/version", query).getJSONArray("data");
        if (data == null) return null;

        List<FrameworkVersionListVo> list =  new LinkedList<>();
        for (int i = 0; i < data.size(); i++) {
            list.add(JSONUtil.toBean(data.getJSONObject(i), FrameworkVersionListVo.class));
        }
        return list;
    }

    @Override
    public AuthInfoVo getAuthinfo() {
        NiucloudUtils instance = NiucloudUtils.getInstance();

        Map<String, Object> query = new HashMap<>();
        query.put("code", instance.getCode());
        query.put("secret", instance.getSecret());
        query.put("product_key", instance.getProductKey());

        JSONObject authInfo = NiucloudUtils.Niucloud.get("authinfo", query).getJSONObject("data");
        if (authInfo == null) return null;

        AuthInfoVo vo = new AuthInfoVo();
        AuthInfoVo.AuthInfo data = JSONUtil.toBean(authInfo, AuthInfoVo.AuthInfo.class);
        vo.setData(data);
        return vo;
    }

    public void setAuthorize(SetAuthorizeParam param) {
        NiucloudUtils instance = NiucloudUtils.getInstance();

        Map<String, Object> query = new HashMap<>();
        query.put("code", param.getAuthCode());
        query.put("secret", param.getAuthSecret());
        query.put("product_key", instance.getProductKey());

        JSONObject authInfo = NiucloudUtils.Niucloud.get("authinfo", query).getJSONObject("data");
        if (authInfo == null) throw new CommonException("未获取到授权信息");

        coreNiucloudConfigService.setNiucloudConfig(param);
        NiucloudUtils.Niucloud.clearAccessToken();
    }

    /**
     *
     * @return
     */
    public List<ModuleListVo> getModuleList() {
        NiucloudUtils instance = NiucloudUtils.getInstance();

        Map<String, Object> query = new HashMap<>();
        query.put("code", instance.getCode());
        query.put("secret", instance.getSecret());
        query.put("product_key", instance.getProductKey());

        JSONArray addonList = NiucloudUtils.Niucloud.get("member_app_all", query).getJSONArray("data");
        if (addonList == null && addonList.size() == 0) return null;

        List<ModuleListVo> list = new LinkedList<>();
        for (int i = 0; i < addonList.size(); i++) {
            JSONObject item = addonList.getJSONObject(i);
            ModuleListVo vo = JSONUtil.toBean(item, ModuleListVo.class);
            list.add(vo);
        }
        return list;
    }

    /**
     * 获取操作token
     *
     * @param action
     * @param query
     * @return
     */
    public JSONObject getActionToken(String action, Map<String, Object> query) {
        return NiucloudUtils.Niucloud.get("member_app_action/" + action, query).getJSONObject("data");
    }

    /**
     * 校验key是否被占用
     *
     * @param key
     * @return
     */
    public Boolean checkKey(String key) {
        NiucloudUtils instance = NiucloudUtils.getInstance();

        Map<String, Object> query = new HashMap<>();
        query.put("product_key", instance.getProductKey());

        return NiucloudUtils.Niucloud.get("store/app_check/" + key, query).get("data", Boolean.class);
    }

    /**
     * 获取应用/插件的版本更新记录
     *
     * @param param
     * @return
     */
    @Override
    public List<AppVersionListVo> getAppVersionList(GetAppVersionListParam param) {
        NiucloudUtils instance = NiucloudUtils.getInstance();

        Map<String, Object> query = new HashMap<>();
        query.put("product_key", instance.getProductKey());
        query.put("app_key", param.getAppKey());

        JSONArray data = ObjectUtil.defaultIfNull(NiucloudUtils.Niucloud.get("store/app_version/list", query).get("data", JSONArray.class), new JSONArray());

        List<AppVersionListVo> list = new LinkedList<>();

        for (int i = 0; i < data.size(); i++) {
            list.add(JSONUtil.toBean(data.getJSONObject(i), AppVersionListVo.class));
        }
        return list;
    }
}
