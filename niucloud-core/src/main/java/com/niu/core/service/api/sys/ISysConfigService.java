package com.niu.core.service.api.sys;

import com.niu.core.entity.sys.SysConfig;
import com.niu.core.service.admin.site.vo.SiteInfoVo;
import com.niu.core.service.admin.sys.vo.SysCopyRightVo;
import com.niu.core.service.admin.sys.vo.SysMapVo;
import com.niu.core.service.admin.sys.vo.SysWebsiteVo;
import com.niu.core.service.api.sys.param.SysConfigCopyrightParam;
import com.niu.core.service.api.sys.param.SysConfigMapParam;
import com.niu.core.service.api.sys.param.SysConfigSceneDomainParam;
import com.niu.core.service.api.sys.param.SysConfigSiteParam;
import com.niu.core.service.core.sys.vo.SceneDomainVo;

import java.util.Map;

public interface ISysConfigService {

    /**
     * 版权信息
     *
     * @param param
     * @return
     */
    SysCopyRightVo copyright(SysConfigCopyrightParam param);

    /**
     * 站点信息
     *
     * @param param
     * @return
     */
    SiteInfoVo site(SysConfigSiteParam param);

    /**
     * 场景域名
     *
     * @param param
     * @return
     */
    SceneDomainVo sceneDomain(SysConfigSceneDomainParam param);

    /**
     * 获取地图设置
     *
     * @param param
     * @return
     */
    SysMapVo map(SysConfigMapParam param);

    /**
     * 获取公众号用户是否绑定手机
     * @param openid
     * @param channel
     * @return
     */
    Map<String, Integer> getMemberMobileExist(String openid, String channel);
}
