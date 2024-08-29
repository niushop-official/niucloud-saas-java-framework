package com.niu.core.service.core.sys;

import cn.hutool.json.JSONObject;
import com.niu.core.service.admin.sys.param.*;
import com.niu.core.service.admin.sys.vo.*;
import com.niu.core.service.core.sys.vo.CoreSysConfigVo;
import com.niu.core.service.core.sys.vo.SceneDomainVo;

public interface ICoreSysConfigService {

    /**
     * 获取网站设置
     * @return
     */
    SysWebsiteVo getWebSite(Integer siteId);

    /**
     * 设置网站设置
     * @return
     */
    void setWebSite(Integer siteId, SysWebsiteParam configParam);

    /**
     * 获取服务信息
     * @param siteId
     * @return
     */
    SysServiceVo getService(Integer siteId);

    /**
     * 获取版权信息
     * @return
     */
    SysCopyRightVo getCopyRight(Integer siteId);

    /**
     * 设置版权信息
     * @param configParam
     */
    void setCopyRight(Integer siteId, SysCopyRightParam configParam);

    /**
     * 获取地图配置
     * @param siteId
     * @return
     */
    SysMapVo getMap(Integer siteId);

    /**
     * 设置地图配置
     * @param siteId
     * @param configParam
     */
    void setMap(Integer siteId, SysMapParam configParam);

    /**
     * 获取开发者key
     * @return
     */
    SysDeveloperTokenVo getDeveloperToken();

    /**
     * 设置开发者key
     * @param configParam
     */
    void setDeveloperToken(SysDeveloperTokenParam configParam);

    /**
     * 获取布局风格
     * @return
     */
    JSONObject getLayout(Integer siteId);

    /**
     * 设置布局风格
     * @param siteId
     * @param configParam
     */
    void setLayout(Integer siteId, JSONObject configParam);

    /**
     * 获取布局风格
     *
     * @return
     */
    JSONObject getThemeColor(Integer siteId);

    /**
     * 设置布局风格
     *
     * @param siteId
     * @param configParam
     */
    void setThemeColor(Integer siteId, JSONObject configParam);

    /**
     * 获取注册与登录设置
     *
     * @param siteId
     * @return
     */
    SysLoginConfigVo getLogin(Integer siteId);

    /**
     * 设置注册与登录设置
     *
     * @param siteId
     * @param configParam
     */
    void setLogin(Integer siteId, SysLoginConfigParam configParam);

    /**
     * 暂用于单站点业务(不适用于命令行模式)
     *
     * @param siteId
     * @return
     */
    SceneDomainVo getSceneDomain(Integer siteId);
}
