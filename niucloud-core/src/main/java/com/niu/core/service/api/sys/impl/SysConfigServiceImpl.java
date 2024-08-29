package com.niu.core.service.api.sys.impl;

import com.niu.core.service.admin.site.vo.SiteInfoVo;
import com.niu.core.service.admin.sys.vo.SysCopyRightVo;
import com.niu.core.service.admin.sys.vo.SysMapVo;
import com.niu.core.service.api.sys.ISysConfigService;
import com.niu.core.service.api.sys.param.SysConfigCopyrightParam;
import com.niu.core.service.api.sys.param.SysConfigMapParam;
import com.niu.core.service.api.sys.param.SysConfigSceneDomainParam;
import com.niu.core.service.api.sys.param.SysConfigSiteParam;
import com.niu.core.service.core.site.ICoreSiteService;
import com.niu.core.service.core.sys.ICoreSysConfigService;
import com.niu.core.service.core.sys.vo.SceneDomainVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SysConfigServiceImpl implements ISysConfigService {

    @Autowired
    private ICoreSysConfigService coreSysConfigService;

    @Autowired
    private ICoreSiteService coreSiteService;

    @Override
    public SysCopyRightVo copyright(SysConfigCopyrightParam param) {
        return coreSysConfigService.getCopyRight(param.siteId());
    }

    @Override
    public SiteInfoVo site(SysConfigSiteParam param) {
        return coreSiteService.getSiteCache(param.siteId());
    }

    @Override
    public SceneDomainVo sceneDomain(SysConfigSceneDomainParam param) {
        return coreSysConfigService.getSceneDomain(param.siteId());
    }

    @Override
    public SysMapVo map(SysConfigMapParam param) {
        return coreSysConfigService.getMap(param.siteId());
    }

}
