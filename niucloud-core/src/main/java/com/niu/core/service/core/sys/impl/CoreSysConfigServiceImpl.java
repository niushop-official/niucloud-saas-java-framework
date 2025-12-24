package com.niu.core.service.core.sys.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.component.context.WebAppEnvs;
import com.niu.core.common.config.GlobalConfig;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.entity.site.Site;
import com.niu.core.mapper.site.SiteMapper;
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

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
     *
     * @param siteId
     * @return
     */
    @Override
    public SysWebsiteVo getWebSite(Integer siteId) {
        Site model = siteMapper.selectOne(new QueryWrapper<Site>().eq("site_id", siteId).last("limit 1"));
        Assert.notNull(model, "站点不存在");

        SysWebsiteVo vo = new SysWebsiteVo();
        BeanUtils.copyProperties(model, vo);

        SysServiceVo sysService = getService();
        if (sysService != null) BeanUtils.copyProperties(sysService, vo);

        return vo;
    }

    /**
     * 设置网站信息
     *
     * @param siteId
     * @param configParam
     */
    public void setWebSite(Integer siteId, SysWebsiteParam configParam) {
        Site model = new Site();
        model.setSiteId(siteId);
        BeanUtils.copyProperties(configParam, model);
        siteMapper.updateById(model);
        coreSiteService.clearSiteCache(siteId);

        JSONObject service = new JSONObject();
        service.set("wechat_code", configParam.getWechatCode());
        service.set("enterprise_wechat", configParam.getEnterpriseWechat());
        service.set("tel", configParam.getTel());
        service.set("site_login_logo", configParam.getSiteLoginLogo());
        service.set("site_login_bg_img", configParam.getSiteLoginBgImg());
        coreConfigService.setConfig(RequestUtils.defaultSiteId(), "SERVICE_INFO", service);
    }

    /**
     * 获取服务信息
     *
     * @return
     */
    @Override
    public SysServiceVo getService() {
        JSONObject config = coreConfigService.getConfigValue(RequestUtils.defaultSiteId(), "SERVICE_INFO");
        return JSONUtil.toBean(config, SysServiceVo.class);
    }

    /**
     * 获取版权信息
     *
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
     *
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
     *
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
     *
     * @param siteId
     * @param configParam
     */
    @Override
    public void setMap(Integer siteId, SysMapParam configParam) {
        JSONObject json = JSONUtil.parseObj(configParam);
        coreConfigService.setConfig(RequestUtils.siteId(), "MAPKEY", json);

        if (RequestUtils.defaultSiteId().equals(siteId)) {
            this.mapKeyChange(configParam.getKey());
        }
    }

    private void mapKeyChange(String key) {
        File file = new File(WebAppEnvs.get().webRootDownRuntime + "uni-app/src/manifest.json");
        if (file.exists()) {
            JSONObject content = JSONUtil.parseObj(removeComments(FileUtil.readUtf8String(file)));
            content.putByPath("h5.sdkConfigs.maps.qqmap.key", key);
            FileUtil.writeUtf8String(content.toStringPretty(), file);
        }
    }

    public String removeComments(String json) {
        // 匹配多行注释的正则表达式
        Pattern pattern = Pattern.compile("/\\*.*?\\*/", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(json);
        // 将匹配到的注释替换为空字符串
        String result = matcher.replaceAll("");
        return result;
    }

    /**
     * 获取开发者key
     *
     * @param
     * @return
     */
    @Override
    public SysDeveloperTokenVo getDeveloperToken() {
        JSONObject config = coreConfigService.getConfigValue(RequestUtils.defaultSiteId(), "DEVELOPER_TOKEN");
        return JSONUtil.toBean(config, SysDeveloperTokenVo.class);
    }

    /**
     * 设置开发者key
     *
     * @param configParam
     */
    @Override
    public void setDeveloperToken(SysDeveloperTokenParam configParam) {
        JSONObject json = JSONUtil.parseObj(configParam);
        coreConfigService.setConfig(RequestUtils.defaultSiteId(), "DEVELOPER_TOKEN", json);
    }

    /**
     * 获取布局风格
     *
     * @return
     */
    @Override
    public JSONObject getLayout(Integer siteId) {
        JSONObject config = coreConfigService.getConfigValue(siteId, "LAYOUT_SETTING");
        return config == null ? new JSONObject() : config;
    }

    /**
     * 设置布局风格
     *
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
     *
     * @return
     */
    @Override
    public JSONObject getThemeColor(Integer siteId) {
        JSONObject config = coreConfigService.getConfigValue(siteId, "THEMECOLOR_SETTING");
        return config == null ? new JSONObject() : config;
    }

    /**
     * 设置色调
     *
     * @param configParam
     */
    @Override
    public void setThemeColor(Integer siteId, JSONObject configParam) {
        JSONObject config = this.getThemeColor(siteId);
        config.set(configParam.getStr("key"), configParam.getStr("value"));
        coreConfigService.setConfig(siteId, "THEMECOLOR_SETTING", config);
    }

    /**
     * 获取注册与登录设置
     *
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
     *
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
        String wapDomain = ObjectUtil.isNotEmpty(GlobalConfig.wapDomain) ? GlobalConfig.wapDomain.replace("#/$#", "") : RequestUtils.getRequestSecure() + "://" + RequestUtils.getRequestDomain();

        SiteInfoVo siteInfoVo = coreSiteService.getSiteCache(siteId);
        String siteDomain = siteInfoVo.getSiteDomain();
        if (!siteDomain.isEmpty()) siteDomain = RequestUtils.getRequestSecure() + "://" + siteDomain;

        SceneDomainVo sceneDomainVo = new SceneDomainVo();
        sceneDomainVo.setWapDomain(wapDomain);
        sceneDomainVo.setWapUrl(ObjectUtil.isNotEmpty(siteDomain) ? siteDomain + "/wap" : wapDomain + "/wap/" + siteId);
        sceneDomainVo.setWebUrl(ObjectUtil.isNotEmpty(siteDomain) ? siteDomain + "/web" : wapDomain + "/web/" + siteId);
        return sceneDomainVo;
    }
}
