package com.niu.core.service.core.member.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import cn.hutool.core.util.ClassLoaderUtil;
import com.niu.core.common.utils.StringUtils;
import com.niu.core.common.utils.date.DateUtils;
import com.niu.core.entity.member.Member;
import com.niu.core.entity.site.Site;
import com.niu.core.enums.member.*;
import com.niu.core.mapper.member.MemberMapper;
import com.niu.core.mapper.site.SiteMapper;
import com.niu.core.service.admin.member.vo.MemberConfigVo;
import com.niu.core.service.core.member.ICoreMemberAccountService;
import com.niu.core.service.core.member.ICoreMemberConfigService;
import com.niu.core.service.core.member.ICoreMemberService;
import com.niu.core.service.core.member.param.MemberStatSearchParam;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.lang.reflect.Method;
import java.util.Map;

@Service
public class CoreMemberServiceImpl implements ICoreMemberService {

    @Resource
    SiteMapper siteMapper;

    @Resource
    MemberMapper memberMapper;

    @Resource
    ICoreMemberConfigService coreMemberConfigService;

    @Resource
    ICoreMemberAccountService coreMemberAccountService;

    @Override
    public Member getMember(Integer siteId, Integer memberId) {
        QueryWrapper<Member> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("site_id", siteId);
        queryWrapper.eq("member_id", memberId);
        return memberMapper.selectOne(queryWrapper);
    }

    /**
     * 创建会员码
     *
     * @param siteId
     * @return
     */
    @Override
    public String createMemberNo(Integer siteId) {
        Site site = siteMapper.selectById(siteId);
        MemberConfigVo memberConfig = coreMemberConfigService.getMemberConfig(siteId);

        String no = (site.getMemberNo() + 1) + "";
        String memberNo = memberConfig.getPrefix() + (memberConfig.getPrefix().length() > memberConfig.getLength() ? no : StringUtils.leftPad(no, memberConfig.getLength()-memberConfig.getPrefix().length(), "0"));

        Member member = memberMapper.selectOne(new QueryWrapper<Member>()
                .eq("site_id", siteId).eq("member_no", memberNo).last("limit 1"));

        if (member == null) {
            return memberNo;
        }

        site.setMemberNo(Integer.parseInt(no));
        siteMapper.updateById(site);

        return this.createMemberNo(siteId);
    }

    /**
     * 通过条件统计会员
     *
     * @param memberStatSearchParam
     * @return
     */
    @Override
    public Integer getMemberCount(MemberStatSearchParam memberStatSearchParam) {
        QueryWrapper<Member> queryWrapper = new QueryWrapper<>();
        if (ObjectUtil.isNotEmpty(memberStatSearchParam.getSiteId())) {
            queryWrapper.eq("site_id", memberStatSearchParam.getSiteId());
        }
        if (ObjectUtil.isNotNull(memberStatSearchParam.getCreateTime())) {
            String[] createTime = memberStatSearchParam.getCreateTime();
            Long startTime = (createTime[0] == "") ? 0 : DateUtils.StringToTimestamp(createTime[0]);
            Long endTime = (createTime[1] == "") ? 0 : DateUtils.StringToTimestamp(createTime[1]);
            if (startTime > 0 && endTime > 0) {
                queryWrapper.between("create_time", startTime, endTime);
            } else if (startTime > 0 && endTime == 0) {
                queryWrapper.ge("create_time", startTime);
            } else if (startTime == 0 && endTime > 0) {
                queryWrapper.le("create_time", endTime);
            }
        }
        if (ObjectUtil.isNotNull(memberStatSearchParam.getSex())) {
            queryWrapper.eq("sex", memberStatSearchParam.getSex());
        }
        if (ObjectUtil.isNotNull(memberStatSearchParam.getLastVisitTime())) {
            String[] lastVisitTime = memberStatSearchParam.getLastVisitTime();
            Long startLastVisitTime = (lastVisitTime[0] == "") ? 0 : DateUtils.StringToTimestamp(lastVisitTime[0]);
            Long endLastVisitTime = (lastVisitTime[1] == "") ? 0 : DateUtils.StringToTimestamp(lastVisitTime[1]);
            if (startLastVisitTime > 0 && endLastVisitTime > 0) {
                queryWrapper.between("last_visit_time", startLastVisitTime, endLastVisitTime);
            } else if (startLastVisitTime > 0 && endLastVisitTime == 0) {
                queryWrapper.ge("last_visit_time", startLastVisitTime);
            } else if (startLastVisitTime == 0 && endLastVisitTime > 0) {
                queryWrapper.le("last_visit_time", endLastVisitTime);
            }
        }
        Long memberCount = memberMapper.selectCount(queryWrapper);
        return memberCount.intValue();
    }

    /**
     * 获取礼包内容
     *
     * @param siteId
     * @param gifts
     * @param scene
     * @return
     */
    public JSONObject getGiftContent(Integer siteId, JSONObject gifts, String scene) {
        if (gifts == null) gifts = new JSONObject();

        JSONObject giftEnum = GiftEnum.getType();

        for (String key : gifts.keySet()) {
            try {
                JSONObject config = gifts.getJSONObject(key);
                String driver = (String) giftEnum.getByPath(key + ".content." + scene + ".driver");
                if (config.getInt("is_use") != null && config.getInt("is_use") > 0 && driver != null && !driver.isEmpty()) {
                    Class<?> clazz = ClassLoaderUtil.loadClass(driver);
                    Object obj = clazz.getDeclaredConstructor().newInstance();
                    Method method = clazz.getMethod("content", Integer.class, JSONObject.class, String.class);
                    Object[] params = {siteId, config, scene};
                    Object content = method.invoke(obj, params);
                    gifts.putByPath(key + ".content", content);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return gifts;
    }

    /**
     * 获取权益内容
     *
     * @param siteId
     * @param benefits
     * @param scene
     * @return
     */
    public JSONObject getBenefitsContent(Integer siteId, JSONObject benefits, String scene) {
        if (benefits == null) benefits = new JSONObject();
        JSONObject benefitsEnum = BenefitsEnum.getType();

        for (String key : benefits.keySet()) {
            try {
                JSONObject config = benefits.getJSONObject(key);
                String driver = (String) benefitsEnum.getByPath(key + ".content." + scene + ".driver");
                if (config.getInt("is_use") != null && config.getInt("is_use") > 0 && driver != null && !driver.isEmpty()) {
                    Class<?> clazz = ClassLoaderUtil.loadClass(driver);
                    Object obj = clazz.getDeclaredConstructor().newInstance();
                    Method method = clazz.getMethod("content", Integer.class, JSONObject.class, String.class);
                    Object[] params = {siteId, config, scene};
                    Object content = method.invoke(obj, params);
                    benefits.putByPath(key + ".content", content);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return benefits;
    }

    /**
     * 获取成长值规则内容
     *
     * @param siteId
     * @param rules
     * @param scene
     * @return
     */
    public JSONObject getGrowthRuleContent(Integer siteId, JSONObject rules, String scene) {
        JSONObject growthRuleEnum = GrowthRuleEnum.getType();

        for (String key : rules.keySet()) {
            try {
                JSONObject config = rules.getJSONObject(key);
                String driver = (String) growthRuleEnum.getByPath(key + ".content." + scene + ".driver");
                if (config.getInt("is_use") != null && config.getInt("is_use") > 0 && driver != null && !driver.isEmpty()) {
                    Class<?> clazz = ClassLoaderUtil.loadClass(driver);
                    Object obj = clazz.getDeclaredConstructor().newInstance();
                    Method method = clazz.getMethod("content", Integer.class, JSONObject.class, String.class);
                    Object[] params = {siteId, config, scene};
                    Object content = method.invoke(obj, params);
                    rules.putByPath(key + ".content", content);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return rules;
    }

    /**
     * 获取积分发放规则内容
     *
     * @param siteId
     * @param rules
     * @param scene
     * @return
     */
    public JSONObject getPointGrantRuleContent(Integer siteId, JSONObject rules, String scene) {
        JSONObject pointRuleEnum = PointRuleEnum.getType().getJSONObject("grant");

        for (String key : rules.keySet()) {
            try {
                JSONObject config = rules.getJSONObject(key);
                String driver = (String) pointRuleEnum.getByPath(key + ".content." + scene + ".driver");
                if (config.getInt("is_use") != null && config.getInt("is_use") > 0 && driver != null && !driver.isEmpty()) {
                    Class<?> clazz = ClassLoaderUtil.loadClass(driver);
                    Object obj = clazz.getDeclaredConstructor().newInstance();
                    Method method = clazz.getMethod("content", Integer.class, JSONObject.class, String.class);
                    Object[] params = {siteId, config, scene};
                    Object content = method.invoke(obj, params);
                    rules.putByPath(key + ".content", content);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return rules;
    }

    /**
     * 获取积分消费规则内容
     *
     * @param siteId
     * @param rules
     * @param scene
     * @return
     */
    public JSONObject getPointConsumeRuleContent(Integer siteId, JSONObject rules, String scene) {
        JSONObject pointRuleEnum = PointRuleEnum.getType().getJSONObject("consume");

        for (String key : rules.keySet()) {
            try {
                JSONObject config = rules.getJSONObject(key);
                String driver = (String) pointRuleEnum.getByPath(key + ".content." + scene + ".driver");
                if (config.getInt("is_use") != null && config.getInt("is_use") > 0 && driver != null && !driver.isEmpty()) {
                    Class<?> clazz = ClassLoaderUtil.loadClass(driver);
                    Object obj = clazz.getDeclaredConstructor().newInstance();
                    Method method = clazz.getMethod("content", Integer.class, JSONObject.class, String.class);
                    Object[] params = {siteId, config, scene};
                    Object content = method.invoke(obj, params);
                    rules.putByPath(key + ".content", content);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return rules;
    }

    /**
     * 发放成长值
     *
     * @param siteId
     * @param memberId
     * @param key
     */
    public void sendGrowth(Integer siteId, Integer memberId, String key, Map<String, Object> data) {
        JSONObject ruleConfig = coreMemberConfigService.getGrowthRuleConfig(siteId);
        if (ruleConfig == null) return;

        JSONObject config = ruleConfig.getJSONObject(key);
        if (config == null || config.getInt("is_use", 0).equals(0)) return;

        JSONObject rule = GrowthRuleEnum.getType().getJSONObject(key);
        if (rule == null) return;

        String driver = ObjectUtil.defaultIfNull(rule.getByPath("calculate.driver", String.class), "");
        if (driver.isEmpty()) return;

        try {
            Class<?> clazz = ClassLoaderUtil.loadClass(driver);
            Object obj = clazz.getDeclaredConstructor().newInstance();
            Method method = clazz.getMethod("calculate", Integer.class, String.class, JSONObject.class, Map.class);
            Object[] params = {siteId, key, config, data};
            Object growth = method.invoke(obj, params);

            if (growth instanceof Integer && (Integer) growth > 0) {
                Double accountData = Double.parseDouble(growth.toString());
                coreMemberAccountService.addLog(siteId, memberId, AccountTypeEnum.GROWTH.getType(), accountData, String.valueOf(ObjectUtil.defaultIfNull(data.get("from_type"), "")), String.valueOf(ObjectUtil.defaultIfNull(data.get("memo"), "")), String.valueOf(ObjectUtil.defaultIfNull(data.get("related_id"), "")));
            }
        } catch (Exception e) {
            System.out.println("成长值发放异常");
            e.printStackTrace();
        }
    }

    /**
     * 发放积分
     *
     * @param siteId
     * @param memberId
     * @param key
     */
    public void sendPoint(Integer siteId, Integer memberId, String key, Map<String, Object> data) {
        JSONObject ruleConfig = coreMemberConfigService.getPointRuleConfig(siteId).getJSONObject("grant");
        if (ruleConfig == null) return;

        JSONObject config = ruleConfig.getJSONObject(key);
        if (config == null || config.getInt("is_use", 0).equals(0)) return;

        JSONObject rule = PointRuleEnum.getType().getJSONObject("grant");
        if (rule == null) return;

        String driver = ObjectUtil.defaultIfNull(rule.getByPath(key + ".calculate.driver", String.class), "");
        if (driver.isEmpty()) return;

        try {
            Class<?> clazz = ClassLoaderUtil.loadClass(driver);
            Object obj = clazz.getDeclaredConstructor().newInstance();
            Method method = clazz.getMethod("calculate", Integer.class, String.class, JSONObject.class, Map.class);
            Object[] params = {siteId, key, config, data};
            Object growth = method.invoke(obj, params);

            if (growth instanceof Integer && (Integer) growth > 0) {
                Double accountData = Double.parseDouble(growth.toString());
                coreMemberAccountService.addLog(siteId, memberId, AccountTypeEnum.POINT.getType(), accountData, String.valueOf(ObjectUtil.defaultIfNull(data.get("from_type"), "")), String.valueOf(ObjectUtil.defaultIfNull(data.get("memo"), "")), String.valueOf(ObjectUtil.defaultIfNull(data.get("related_id"), "")));
            }
        } catch (Exception e) {
            System.out.println("积分发放异常");
            e.printStackTrace();
        }
    }

    /**
     * 会员礼包发放
     *
     * @param siteId
     * @param memberId
     * @param gifts
     */
    public void memberGiftGrant(Integer siteId, Integer memberId, JSONObject gifts, Map<String, Object> vars) {
        if (gifts == null) return;

        JSONObject giftEnum = GiftEnum.getType();

        try {
            for (String key : gifts.keySet()) {
                JSONObject config = gifts.getJSONObject(key);
                String driver = (String) giftEnum.getByPath(key + ".grant.driver");
                if (config.getInt("is_use") != null && config.getInt("is_use") > 0 && driver != null && !driver.isEmpty()) {
                    Class<?> clazz = ClassLoaderUtil.loadClass(driver);
                    Object obj = clazz.getDeclaredConstructor().newInstance();
                    Method method = clazz.getMethod("grant", Integer.class, Integer.class, JSONObject.class, Map.class);
                    Object[] params = {siteId, memberId, config, vars};
                    method.invoke(obj, params);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("会员礼包发放失败");
        }
    }
}
