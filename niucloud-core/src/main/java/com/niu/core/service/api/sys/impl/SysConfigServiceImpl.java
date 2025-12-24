package com.niu.core.service.api.sys.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.entity.member.Member;
import com.niu.core.mapper.member.MemberMapper;
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
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class SysConfigServiceImpl implements ISysConfigService {

    @Autowired
    private ICoreSysConfigService coreSysConfigService;

    @Autowired
    private ICoreSiteService coreSiteService;

    @Resource
    private MemberMapper memberMapper;

    @Override
    public SysCopyRightVo copyright() {
        return coreSysConfigService.getCopyRight(RequestUtils.siteId());
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

    @Override
    public Map<String, Integer> getMemberMobileExist(String openid, String channel) {
        Map<String, Integer> result = new HashMap<>();
        result.put("member_mobile_exist", 0);
        result.put("member_exist", 0);

        if (!openid.isEmpty()) {
            String openidField = switch (channel.toLowerCase()) {
                case "wechat" -> "wx_openid";
                case "weapp" -> "weapp_openid";
                default -> "";
            };

            if (!openidField.isEmpty()) {
                Member member = memberMapper.selectOne(new QueryWrapper<Member>()
                    .select("member_id, mobile")
                    .eq("site_id", RequestUtils.siteId())
                    .eq(openidField, openid));
                result.put("member_exist", member == null ? 0 : 1);
                result.put("member_mobile_exist", member != null && !member.getMobile().isBlank() ? 1 : 0);
            }
        }

        return result;
    }

}
