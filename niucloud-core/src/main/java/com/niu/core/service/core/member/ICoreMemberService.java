package com.niu.core.service.core.member;

import cn.hutool.json.JSONObject;
import com.niu.core.entity.member.Member;
import com.niu.core.service.admin.member.param.MemberAccountLogParam;
import com.niu.core.service.core.member.dto.MemberInfoDto;
import com.niu.core.service.core.member.param.MemberStatSearchParam;

import java.util.Map;

/**
 * 会员core服务层
 */
public interface ICoreMemberService {

    /**
     * @param siteId
     * @param memberId
     * @return
     */
    public Member getMember(Integer siteId, Integer memberId);

    /**
     * 创建会员码
     *
     * @param siteId
     * @return
     */
    String createMemberNo(Integer siteId);

    /**
     * 通过条件统计会员
     *
     * @param memberStatSearchParam
     * @return
     */
    Integer getMemberCount(MemberStatSearchParam memberStatSearchParam);

    /**
     * 获取礼包内容
     *
     * @param siteId
     * @param gifts
     * @param scene
     * @return
     */
    JSONObject getGiftContent(Integer siteId, JSONObject gifts, String scene);

    /**
     * 获取权益内容
     *
     * @param siteId
     * @param benefits
     * @param scene
     * @return
     */
    JSONObject getBenefitsContent(Integer siteId, JSONObject benefits, String scene);

    /**
     * 获取成长值规则内容
     *
     * @param siteId
     * @param rules
     * @param scene
     * @return
     */
    JSONObject getGrowthRuleContent(Integer siteId, JSONObject rules, String scene);

    /**
     * 获取积分发放规则内容
     *
     * @param siteId
     * @param rules
     * @param scene
     * @return
     */
    JSONObject getPointGrantRuleContent(Integer siteId, JSONObject rules, String scene);

    /**
     * 获取积分消费规则内容
     *
     * @param siteId
     * @param rules
     * @param scene
     * @return
     */
    JSONObject getPointConsumeRuleContent(Integer siteId, JSONObject rules, String scene);

    /**
     * 发放成长值
     *
     * @param siteId
     * @param memberId
     * @param key
     */
    void sendGrowth(Integer siteId, Integer memberId, String key, Map<String, Object> data);

    /**
     * 发放积分
     *
     * @param siteId
     * @param memberId
     * @param key
     */
    void sendPoint(Integer siteId, Integer memberId, String key, Map<String, Object> data);

    /**
     * 发放礼包
     *
     * @param siteId
     * @param memberId
     * @param gifts
     */
    void memberGiftGrant(Integer siteId, Integer memberId, JSONObject gifts, Map<String, Object> vars);
    /**
     * 根据id查询会员信息
     *
     * @param siteId
     * @param memberId
     */
    Member getInfoByMemberId(Integer siteId, Integer memberId);

    /**
     * 获取登录用户详情信息
     * @param memberId
     * @return
     */
    MemberInfoDto getMemberInfo(Integer memberId);
}
