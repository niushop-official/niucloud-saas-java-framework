package com.niu.core.service.core.member;

import cn.hutool.json.JSONObject;
import com.niu.core.service.admin.member.param.CashOutConfigParam;
import com.niu.core.service.admin.member.param.LoginConfigParam;
import com.niu.core.service.admin.member.param.MemberConfigParam;
import com.niu.core.service.admin.member.vo.CashOutConfigVo;
import com.niu.core.service.admin.member.vo.LoginConfigVo;
import com.niu.core.service.admin.member.vo.MemberConfigVo;

/**
 * 会员相关配置服务层
 */
public interface ICoreMemberConfigService {

    /**
     * 获取注册与登录设置
     * @return
     */
    LoginConfigVo getLoginConfig(Integer siteId);

    /**
     * 设置注册与登录设置
     * @param configParam
     */
    void setLoginConfig(Integer siteId, LoginConfigParam configParam);

    /**
     * 获取会员提现设置
     * @return
     */
    CashOutConfigVo getCashOutConfig(Integer siteId);

    /**
     * 设置会员提现设置
     * @param configParam
     */
    void setCashOutConfig(Integer siteId, CashOutConfigParam configParam);

    /**
     * 获取会员设置
     * @return
     */
    MemberConfigVo getMemberConfig(Integer siteId);

    /**
     * 会员设置
     * @param configParam
     */
    void setMemberConfig(Integer siteId, MemberConfigParam configParam);

    /**
     * 获取成长值规则
     * @return
     */
    JSONObject getGrowthRuleConfig(Integer siteId);

    /**
     * 设置成长值规则
     * @param configParam
     */
    void setGrowthRuleConfig(Integer siteId, JSONObject configParam);

    /**
     * 获取成长值规则
     * @return
     */
    JSONObject getPointRuleConfig(Integer siteId);

    /**
     * 设置积分规则
     * @param configParam
     */
    void setPointRuleConfig(Integer siteId, JSONObject configParam);
}
