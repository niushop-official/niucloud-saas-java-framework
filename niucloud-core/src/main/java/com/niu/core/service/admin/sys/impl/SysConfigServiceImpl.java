package com.niu.core.service.admin.sys.impl;

import cn.hutool.json.JSONObject;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.service.admin.site.ISiteService;
import com.niu.core.service.admin.site.vo.SiteInfoVo;
import com.niu.core.service.admin.sys.ISysConfigService;
import com.niu.core.service.admin.sys.param.*;
import com.niu.core.service.admin.sys.vo.*;
import com.niu.core.service.core.sys.ICoreSysConfigService;
import com.niu.core.service.core.sys.vo.SceneDomainVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Service
public class SysConfigServiceImpl implements ISysConfigService {

    @Resource
    ICoreSysConfigService coreSysConfigService;

    @Resource
    ISiteService siteService;

    /**
     * 获取网站信息
     * @return
     */
    @Override
    public SysWebsiteVo getWebSite() {
        return coreSysConfigService.getWebSite(RequestUtils.siteId());
    }

    /**
     * 获取网站信息
     * @return
     */
    @Override
    public void setWebSite(SysWebsiteParam configParam) {
        coreSysConfigService.setWebSite(RequestUtils.siteId(), configParam);
    }

    /**
     * 获取服务信息
     * @return
     */
    @Override
    public SysServiceVo getService() {
        return coreSysConfigService.getService(RequestUtils.siteId());
    }

    @Override
    public SysCopyRightVo getCopyRight() {
        return coreSysConfigService.getCopyRight(RequestUtils.siteId());
    }

    @Override
    public void setCopyRight(SysCopyRightParam configParam) {
        coreSysConfigService.setCopyRight(RequestUtils.siteId(), configParam);
    }

    /**
     * 获取地图配置
     * @return
     */
    public SysMapVo getMap() {
        return coreSysConfigService.getMap(RequestUtils.siteId());
    }

    /**
     * 设置地图配置
     * @param configParam
     */
    public void setMap(SysMapParam configParam) {
        coreSysConfigService.setMap(RequestUtils.siteId(), configParam);
    }

    /**
     * 获取开发者key
     * @return
     */
    public SysDeveloperTokenVo getDeveloperToken() {
        return coreSysConfigService.getDeveloperToken();
    }

    /**
     * 设置开发者key
     * @param configParam
     */
    public void setDeveloperToken(SysDeveloperTokenParam configParam) {
        coreSysConfigService.setDeveloperToken(configParam);
    }

    /**
     * 获取布局风格
     * @return
     */
    public JSONObject getLayout() {
        return coreSysConfigService.getLayout(RequestUtils.siteId());
    }

    /**
     * 设置布局风格
     * @param configParam
     */
    public void setLayout(JSONObject configParam) {
        coreSysConfigService.setLayout(RequestUtils.siteId(), configParam);
    }

    /**
     * 获取布局风格
     * @return
     */
    public JSONObject getThemeColor() {
        return coreSysConfigService.getThemeColor(RequestUtils.siteId());
    }

    /**
     * 设置布局风格
     * @param configParam
     */
    public void setThemeColor(JSONObject configParam) {
        coreSysConfigService.setThemeColor(RequestUtils.siteId(), configParam);
    }

    /**
     * 获取注册与登录设置
     * @return
     */
    public SysLoginConfigVo getLogin() {
        return coreSysConfigService.getLogin(RequestUtils.siteId());
    }

    /**
     * 设置注册与登录设置
     * @param configParam
     */
    public void setLogin(SysLoginConfigParam configParam) {
        coreSysConfigService.setLogin(RequestUtils.siteId(), configParam);
    }

    /**
     * 获取
     * @return
     */
    public SceneDomainVo getUrl() {
        return coreSysConfigService.getSceneDomain(RequestUtils.siteId());
    }
}
