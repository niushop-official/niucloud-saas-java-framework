package com.niu.core.service.admin.auth.impl;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson2.JSON;
import com.google.gson.Gson;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.entity.sys.SysConfig;
import com.niu.core.enums.sys.ConfigKeyEnum;
import com.niu.core.service.admin.auth.IConfigService;
import com.niu.core.service.admin.auth.param.LoginConfigParam;
import com.niu.core.service.admin.auth.vo.LoginConfigVo;
import com.niu.core.service.admin.sys.vo.SysCopyRightVo;
import com.niu.core.service.core.sys.ICoreConfigService;
import com.niu.core.service.core.sys.vo.CoreSysConfigVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class ConfigServiceImpl implements IConfigService {

    @Resource
    ICoreConfigService coreConfigService;
    /**
     * 获取站点登录配置
     *
     * @return
     */
    @Override
    public LoginConfigVo getLoginConfig() {
        Integer defaultSiteId= RequestUtils.defaultSiteId();
        CoreSysConfigVo sysConfig=coreConfigService.getConfig(defaultSiteId, ConfigKeyEnum.ADMIN_LOGIN.getName());
        LoginConfigVo vo = new LoginConfigVo();
        vo=JSONUtil.toBean(sysConfig.getValueJson(), LoginConfigVo.class);
        return vo;
    }

    /**
     * 更新登录配置信息
     *
     * @param loginConfigParam
     */
    @Override
    public void setLoginConfig(LoginConfigParam loginConfigParam) {
        JSONObject jsonObject=new JSONObject();
        jsonObject.set("is_captcha", loginConfigParam.getIsCaptcha());
        jsonObject.set("is_site_captcha", loginConfigParam.getIsSiteCaptcha());
        jsonObject.set("bg", loginConfigParam.getBg());
        jsonObject.set("site_bg", loginConfigParam.getSiteBg());
        coreConfigService.setConfig(RequestUtils.siteId(), ConfigKeyEnum.ADMIN_LOGIN.getName(), jsonObject);
    }
}
