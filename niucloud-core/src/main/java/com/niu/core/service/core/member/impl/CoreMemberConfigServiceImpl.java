package com.niu.core.service.core.member.impl;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.mapper.site.SiteMapper;
import com.niu.core.service.admin.member.param.CashOutConfigParam;
import com.niu.core.service.admin.member.param.LoginConfigParam;
import com.niu.core.service.admin.member.param.MemberConfigParam;
import com.niu.core.service.admin.member.vo.CashOutConfigVo;
import com.niu.core.service.admin.member.vo.LoginConfigVo;
import com.niu.core.service.admin.member.vo.MemberConfigVo;
import com.niu.core.service.core.member.ICoreMemberConfigService;
import com.niu.core.service.core.member.ICoreMemberService;
import com.niu.core.service.core.sys.ICoreConfigService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class CoreMemberConfigServiceImpl implements ICoreMemberConfigService {
    @Resource
    ICoreConfigService coreConfigService;

    ICoreMemberService coreMemberService;

    @Resource
    public void getCoreMemberService(@Lazy ICoreMemberService coreMemberService) {
        this.coreMemberService = coreMemberService;
    }

    /**
     * 获取注册与登录设置
     */
    @Override
    public LoginConfigVo getLoginConfig(Integer siteId) {
        JSONObject config = coreConfigService.getConfigValue(siteId, "LOGIN");
        return JSONUtil.toBean(config, LoginConfigVo.class);
    }

    /**
     * 设置注册与登录设置
     * @param configParam
     */
    @Override
    public void setLoginConfig(Integer siteId, LoginConfigParam configParam) {
        coreConfigService.setConfig(siteId, "LOGIN", JSONUtil.parseObj(configParam));
    }

    /**
     * 获取会员提现设置
     */
    @Override
    public CashOutConfigVo getCashOutConfig(Integer siteId) {
        JSONObject config = coreConfigService.getConfigValue(siteId, "member_cash_out");
        return JSONUtil.toBean(config, CashOutConfigVo.class);
    }

    /**
     * 设置注册与登录设置
     * @param configParam
     */
    @Override
    public void setCashOutConfig(Integer siteId, CashOutConfigParam configParam) {
        coreConfigService.setConfig(siteId, "member_cash_out", JSONUtil.parseObj(configParam));
    }

    /**
     * 获取会员设置
     */
    @Override
    public MemberConfigVo getMemberConfig(Integer siteId) {
        JSONObject config = coreConfigService.getConfigValue(siteId, "MEMBER");
        return JSONUtil.toBean(config, MemberConfigVo.class);
    }

    /**
     * 会员设置
     * @param configParam
     */
    @Override
    public void setMemberConfig(Integer siteId, MemberConfigParam configParam) {
        coreConfigService.setConfig(siteId, "MEMBER", JSONUtil.parseObj(configParam));
    }

    /**
     * 获取成长值规则
     */
    @Override
    public JSONObject getGrowthRuleConfig(Integer siteId) {
        JSONObject config = coreConfigService.getConfigValue(siteId, "GROWTH_RULE");
        if (config != null) config = coreMemberService.getGrowthRuleContent(siteId, config, "admin");
        return config;
    }

    /**
     * 设置成长值规则
     * @param configParam
     */
    @Override
    public void setGrowthRuleConfig(Integer siteId, JSONObject configParam) {
        coreConfigService.setConfig(siteId, "GROWTH_RULE", configParam);
    }

    /**
     * 获取积分规则
     */
    @Override
    public JSONObject getPointRuleConfig(Integer siteId) {
        JSONObject config = coreConfigService.getConfigValue(siteId, "POINT_RULE");
        if (config != null) {
            JSONObject grant = config.get("consume") instanceof JSONObject ? config.getJSONObject("grant") : new JSONObject();
            if (grant != null && grant.size() > 0) config.set("grant", coreMemberService.getPointGrantRuleContent(siteId, grant, "admin"));
            JSONObject consume = config.get("consume") instanceof JSONObject ? config.getJSONObject("consume") : new JSONObject();
            if (consume != null && consume.size() > 0) config.set("consume", coreMemberService.getPointConsumeRuleContent(siteId, consume, "admin"));
        }
        return config;
    }

    /**
     * 设置积分规则
     * @param configParam
     */
    @Override
    public void setPointRuleConfig(Integer siteId, JSONObject configParam) {
        coreConfigService.setConfig(siteId, "POINT_RULE", configParam);
    }
}
