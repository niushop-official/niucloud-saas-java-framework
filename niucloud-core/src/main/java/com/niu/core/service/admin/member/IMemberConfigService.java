package com.niu.core.service.admin.member;

import cn.hutool.json.JSONObject;
import com.niu.core.service.admin.member.param.CashOutConfigParam;
import com.niu.core.service.admin.member.param.LoginConfigParam;
import com.niu.core.service.admin.member.param.MemberConfigParam;
import com.niu.core.service.admin.member.vo.CashOutConfigVo;
import com.niu.core.service.admin.member.vo.LoginConfigVo;
import com.niu.core.service.admin.member.vo.MemberConfigVo;


/**
 * 会员相关设置接口
 */
public interface IMemberConfigService {

    /**
     * 获取注册与登录设置
     * @return
     */
    LoginConfigVo getLoginConfig();

    /**
     * 设置注册与登录设置
     * @param configParam
     */
    void setLoginConfig(LoginConfigParam configParam);

    /**
     * 获取会员提现设置
     * @return
     */
    CashOutConfigVo getCashOutConfig();

    /**
     * 设置会员提现设置
     * @param configParam
     */
    void setCashOutConfig(CashOutConfigParam configParam);

    /**
     * 获取会员设置
     * @return
     */
    MemberConfigVo getMemberConfig();

    /**
     * 会员设置
     * @param configParam
     */
    void setMemberConfig(MemberConfigParam configParam);

    /**
     * 获取成长值规则
     * @return
     */
    JSONObject getGrowthRuleConfig();

    /**
     * 设置成长值规则
     * @param configParam
     */
    void setGrowthRuleConfig(JSONObject configParam);

    /**
     * 获取成长值规则
     * @return
     */
    JSONObject getPointRuleConfig();

    /**
     * 设置积分规则
     * @param configParam
     */
    void setPointRuleConfig(JSONObject configParam);
}
