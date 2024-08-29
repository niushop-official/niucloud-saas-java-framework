package com.niu.core.service.core.sys.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.config.GlobalConfig;
import com.niu.core.entity.member.MemberLevel;
import com.niu.core.entity.site.Site;
import com.niu.core.mapper.site.SiteMapper;
import com.niu.core.service.admin.member.vo.MemberLevelInfoVo;
import com.niu.core.service.admin.site.vo.SiteInfoVo;
import com.niu.core.service.admin.sys.param.*;
import com.niu.core.service.admin.sys.vo.*;
import com.niu.core.service.core.site.ICoreSiteService;
import com.niu.core.service.core.sys.ICoreConfigService;
import com.niu.core.service.core.sys.ICoreSysConfigService;
import com.niu.core.service.core.sys.vo.SceneDomainVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Service
public class CoreSysConfigServiceImpl implements ICoreSysConfigService {

    @Resource
    ICoreConfigService coreConfigService;

    @Resource
    SiteMapper siteMapper;

    @Resource
    ICoreSiteService coreSiteService;

    private Object SysWebsiteParam;

    /**
     * 获取网站信息
     * @param siteId
     * @return
     */
    @Override
    public SysWebsiteVo getWebSite(Integer siteId) {
        Site model = siteMapper.selectOne(new QueryWrapper<Site>().eq("site_id", siteId).last("limit 1"));
        Assert.notNull(model, "站点不存在");

        SysWebsiteVo vo = new SysWebsiteVo();
        BeanUtils.copyProperties(model, vo);
        return vo;
    }

    /**
     * 设置网站信息
     * @param siteId
     * @param configParam
     */
    public void setWebSite(Integer siteId, SysWebsiteParam configParam) {
        Site model = new Site();
        model.setSiteId(siteId);
        BeanUtils.copyProperties(configParam, model);
        siteMapper.updateById(model);

        JSONObject service = new JSONObject();
        service.set("wechat_code", configParam.getWechatCode());
        service.set("enterprise_wechat", configParam.getEnterpriseWechat());
        service.set("tel", configParam.getTel());
        coreConfigService.setConfig(siteId, "SERVICE_INFO", service);
    }

    /**
     * 获取服务信息
     * @param siteId
     * @return
     */
    @Override
    public SysServiceVo getService(Integer siteId) {
        JSONObject config = coreConfigService.getConfigValue(siteId, "SERVICE_INFO");
        return JSONUtil.toBean(config, SysServiceVo.class);
    }

    /**
     * 获取版权信息
     * @param siteId
     * @return
     */
    @Override
    public SysCopyRightVo getCopyRight(Integer siteId) {
        JSONObject config = coreConfigService.getConfigValue(siteId, "COPYRIGHT");
        return JSONUtil.toBean(config, SysCopyRightVo.class);
    }

    /**
     * 设置版权
     * @param siteId
     * @param configParam
     */
    @Override
    public void setCopyRight(Integer siteId, SysCopyRightParam configParam) {
        JSONObject json = JSONUtil.parseObj(configParam);
        coreConfigService.setConfig(RequestUtils.siteId(), "COPYRIGHT", json);
    }

    /**
     * 获取地图配置
     * @param siteId
     * @return
     */
    @Override
    public SysMapVo getMap(Integer siteId) {
        JSONObject config = coreConfigService.getConfigValue(siteId, "MAPKEY");
        return JSONUtil.toBean(config, SysMapVo.class);
    }

    /**
     * 设置地图配置
     * @param siteId
     * @param configParam
     */
    @Override
    public void setMap(Integer siteId, SysMapParam configParam) {
        JSONObject json = JSONUtil.parseObj(configParam);
        coreConfigService.setConfig(RequestUtils.siteId(), "MAPKEY", json);
    }

    /**
     * 获取开发者key
     * @param siteId
     * @return
     */
    @Override
    public SysDeveloperTokenVo getDeveloperToken() {
        JSONObject config = coreConfigService.getConfigValue(RequestUtils.defaultSiteId(), "DEVELOPER_TOKEN");
        return JSONUtil.toBean(config, SysDeveloperTokenVo.class);
    }

    /**
     * 设置开发者key
     * @param configParam
     */
    @Override
    public void setDeveloperToken(SysDeveloperTokenParam configParam) {
        JSONObject json = JSONUtil.parseObj(configParam);
        coreConfigService.setConfig(RequestUtils.defaultSiteId(), "DEVELOPER_TOKEN", json);
    }

    /**
     * 获取布局风格
     * @return
     */
    @Override
    public JSONObject getLayout(Integer siteId) {
        JSONObject config = coreConfigService.getConfigValue(siteId, "LAYOUT_SETTING");
        return config == null ? new JSONObject() : config;
    }

    /**
     * 设置布局风格
     * @param configParam
     */
    @Override
    public void setLayout(Integer siteId, JSONObject configParam) {
        JSONObject config = this.getLayout(siteId);
        config.set(configParam.getStr("key"), configParam.getStr("value"));
        coreConfigService.setConfig(siteId, "LAYOUT_SETTING", config);
    }

    /**
     * 获取色调设置
     * @return
     */
    @Override
    public JSONObject getThemeColor(Integer siteId) {
        JSONObject config = coreConfigService.getConfigValue(siteId, "THEMECOLOR_SETTING");
        return config == null ? new JSONObject() : config;
    }

    /**
     * 设置色调
     * @param configParam
     */
    @Override
    public void setThemeColor(Integer siteId, JSONObject configParam) {
        JSONObject config = this.getLayout(siteId);
        config.set(configParam.getStr("key"), configParam.getStr("value"));
        coreConfigService.setConfig(siteId, "THEMECOLOR_SETTING", config);
    }

    /**
     * 获取注册与登录设置
     * @param siteId
     * @return
     */
    @Override
    public SysLoginConfigVo getLogin(Integer siteId) {
        JSONObject config = coreConfigService.getConfigValue(siteId, "admin_login");
        return JSONUtil.toBean(config, SysLoginConfigVo.class);
    }

    /**
     * 设置注册与登录设置
     * @param siteId
     * @param configParam
     */
    @Override
    public void setLogin(Integer siteId, SysLoginConfigParam configParam) {
        JSONObject json = JSONUtil.parseObj(configParam);
        coreConfigService.setConfig(RequestUtils.siteId(), "admin_login", json);
    }

    /**
     * 暂用于单站点业务(不适用于命令行模式)
     *
     * @param siteId
     * @return
     */
    @Override
    public SceneDomainVo getSceneDomain(Integer siteId) {
        HttpServletRequest request = RequestUtils.handler();

        String wapDomain= ObjectUtil.isNotEmpty(GlobalConfig.wapDomain) ? GlobalConfig.wapDomain.replace("#/$#", "") : request.getScheme() + "://" + RequestUtils.getRequestDomain();
        String webDomain= ObjectUtil.isNotEmpty(GlobalConfig.webDomain) ? GlobalConfig.webDomain.replace("#/$#", "") : request.getScheme() + "://" + RequestUtils.getRequestDomain();

        SiteInfoVo siteInfoVo=coreSiteService.getSiteCache(siteId);
        String siteDomain = siteInfoVo.getSiteDomain();
        if (!siteDomain.isEmpty()) siteDomain = request.getScheme() + "://" + siteDomain;

        SceneDomainVo sceneDomainVo=new SceneDomainVo();
        sceneDomainVo.setWapDomain(wapDomain);
        sceneDomainVo.setWapUrl(ObjectUtil.isNotEmpty(siteDomain) ? siteDomain + "/wap" : wapDomain + "/wap/" + siteId);
        sceneDomainVo.setWebUrl(ObjectUtil.isNotEmpty(siteDomain) ? siteDomain + "/web" : wapDomain + "/web/" + siteId);
        return sceneDomainVo;
    }
}
