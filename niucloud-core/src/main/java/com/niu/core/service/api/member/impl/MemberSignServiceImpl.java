package com.niu.core.service.api.member.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.utils.CollectUtils;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.date.DateUtils;
import com.niu.core.common.utils.json.JsonModuleLoader;
import com.niu.core.entity.member.MemberSign;
import com.niu.core.mapper.member.MemberSignMapper;
import com.niu.core.service.api.member.IMemberSignService;
import com.niu.core.service.api.member.param.*;
import com.niu.core.service.api.member.vo.*;
import com.niu.core.service.core.member.ICoreMemberService;
import com.niu.core.service.core.sys.ICoreConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Slf4j
public class MemberSignServiceImpl implements IMemberSignService {

    @Autowired
    private MemberSignMapper memberSignMapper;

    @Autowired
    private ICoreConfigService coreConfigService;

    @Autowired
    private ICoreMemberService coreMemberService;

    @Override
    public PageResult<MemberSignRecordVo> signRecord(PageParam pageParam, MemberSignRecordParam param) {
        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();

        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.select("sign_id, site_id, member_id, days, day_award, continue_award, continue_tag, create_time, is_sign");
        queryWrapper.eq("site_id", param.siteId());
        queryWrapper.eq("member_id", param.memberId());
        queryWrapper.orderByDesc(Arrays.asList("create_time"));

        IPage<MemberSign> iPage = this.memberSignMapper.selectPage(new Page<>(page, limit), queryWrapper);
        List<MemberSignRecordVo> dataList = CollectUtils.convert(iPage.getRecords(), MemberSignRecordVo.class);
        return PageResult.build(page, limit, iPage.getTotal()).setData(dataList);
    }

    /**
     * 会员签到详情
     *
     * @param param
     * @return
     */
    @Override
    public MemberSignDetailsVo signDetails(MemberSignDetailsParam param) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.select("sign_id, site_id, member_id, days, day_award, continue_award, continue_tag, create_time, is_sign");
        queryWrapper.eq("sign_id", param.getSignId());
        queryWrapper.eq("site_id", param.siteId());
        queryWrapper.eq("member_id", param.memberId());
        List<MemberSign> memberSignList = this.memberSignMapper.selectList(queryWrapper);
        MemberSignDetailsVo memberSignDetailsVo = null;
        if (memberSignList != null && memberSignList.size() > 0) {
            memberSignDetailsVo = new MemberSignDetailsVo();
            BeanUtils.copyProperties(memberSignList.get(0), memberSignDetailsVo);
        }
        return memberSignDetailsVo;
    }

    /**
     * 签到
     *
     * @param param
     * @return
     */
    @Override
    @Transactional
    public MemberSignOperateVo signOperate(MemberSignOperateParam param) {
        JSONObject signConfig = coreConfigService.getConfigValue(RequestUtils.siteId(), "SIGN_CONFIG");
        MemberSignConfigVo config = JSONUtil.toBean(signConfig, MemberSignConfigVo.class);

        if (!config.getIsUse()) throw new RuntimeException("签到未开启");

        // 获取今天的签到
        MemberSign todayMemberSign = this.queryMemberSign(param.siteId(), param.memberId(), DateUtils.getFormatTodayString());
        if (todayMemberSign != null) throw new RuntimeException("今日已签到");

        MemberSignOperateVo vo = new MemberSignOperateVo();
        vo.setTitle("签到成功");
        vo.setInfo("恭喜您获得以下奖励");

        // 获取昨天的签到
        MemberSign yesterdayMemberSign = this.queryMemberSign(param.siteId(), param.memberId(), DateUtils.getFormatYesterdayString());

        Integer days = 1;
        if (yesterdayMemberSign != null) {
            //连签天数大于签到周期，连签天数重置为1
            days = yesterdayMemberSign.getDays() + 1;
            if (days > config.getSignPeriod()) {
                days = 1;
            }
            if (ObjectUtil.isNotEmpty(config.getContinueAward())) {
                JSONArray continueSigns = config.getContinueAward();
                CollUtil.sort(continueSigns, Comparator.comparing(o -> ((JSONObject) o).getInt("continue_sign")).reversed());
                // 获取最大的连签天数
                Integer maxContinueSign = continueSigns.getJSONObject(0).getInt("continue_sign");
                if (days > maxContinueSign) {
                    days = 1;
                }
            }
        }

        // 添加签到记录
        MemberSign model = new MemberSign();
        model.setSiteId(param.siteId());
        model.setMemberId(param.memberId());
        model.setIsSign(1);
        model.setCreateTime(System.currentTimeMillis() / 1000);
        model.setDays(days);
        model.setDayAward(config.getDayAward().toString());
        model.setContinueAward("");
        if (days.equals(1)) model.setStartTime(System.currentTimeMillis() / 1000);
        memberSignMapper.insert(model);

        // 发放日签奖励
        Map<String, Object> dayAwardVar = new HashMap<>();
        dayAwardVar.put("from_type", "day_sign_award");
        dayAwardVar.put("memo", "日签奖励");
        coreMemberService.memberGiftGrant(param.siteId(), param.memberId(), config.getDayAward(), dayAwardVar);
        vo.setAwards(coreMemberService.getGiftContent(param.siteId(), config.getDayAward(), "member_sign"));

        // 发放连签奖励
        if (config.getContinueAward().size() > 0) {
            JSONObject continueAward = null;
            for (int i = 0; i < config.getContinueAward().size(); i++) {
                JSONObject item = config.getContinueAward().getJSONObject(i);
                if (item.getInt("continue_sign").equals(days)) {
                    // 是否限制领取
                    if (item.getInt("receive_limit").equals(2)) {
                        // 周期开始时间
                        Long periodStartTime = memberSignMapper.selectOne(new QueryWrapper<MemberSign>()
                            .select("start_time")
                            .eq("member_id", param.memberId())
                            .eq("site_id", param.siteId())
                            .eq("days", 1)
                            .gt("start_time", 0)
                            .orderByDesc("sign_id")
                            .last("limit 1")
                        ).getStartTime();
                        // 周期结束时间
                        Long periodEndTime = periodStartTime + (config.getSignPeriod() * 86400);
                        // 领取次数
                        Long receiveNum = memberSignMapper.selectCount(new QueryWrapper<MemberSign>()
                           .eq("member_id", param.memberId())
                           .eq("site_id", param.siteId())
                           .between("create_time", periodStartTime, periodEndTime)
                           .eq("continue_tag", item.getStr("continue_tag"))
                        );
                        if (receiveNum < item.getInt("receive_num")) {
                            continueAward = item;
                        }
                    } else {
                        continueAward = item;
                    }
                    break;
                }
            }

            if (continueAward != null) {
                model.setContinueTag(continueAward.getStr("continue_tag"));
                continueAward.remove("continue_sign");
                continueAward.remove("continue_tag");
                continueAward.remove("receive_limit");
                continueAward.remove("receive_num");

                Map<String, Object> continueAwardVar = new HashMap<>();
                continueAwardVar.put("from_type", "continue_sign_award");
                continueAwardVar.put("memo", "连签奖励");
                coreMemberService.memberGiftGrant(param.siteId(), param.memberId(), continueAward, continueAwardVar);

                model.setContinueAward(continueAward.toString());
                memberSignMapper.updateById(model);

                vo.setInfo("连签" + days.toString() + "天恭喜您获得以下奖励");
                vo.setAwards(coreMemberService.getGiftContent(param.siteId(), continueAward, "member_sign"));
            }
        }

        return vo;
    }

    /**
     * 获取月签到数据
     *
     * @param param
     * @return
     */
    @Override
    public MemberSignMonthRecordVo signMonthRecord(MemberSignMonthRecordParam param) {
        MemberSignMonthRecordVo vo = new MemberSignMonthRecordVo();
        List<Integer> days = new ArrayList<>();
        List<MemberSignMonthRecordVo.Period> period = new ArrayList<>();

        Date date = DateUtil.parse(param.getYear() + "-" + param.getMonth() + "-1");

        List<MemberSign> signRecord = this.memberSignMapper.selectList(new QueryWrapper<MemberSign>()
                .eq("member_id", param.memberId())
                .between("create_time",
                        DateUtil.beginOfMonth(date).getTime() / 1000,
                        DateUtil.endOfMonth(date).getTime() / 1000)
                .orderByAsc("create_time"));

        if (signRecord.size() > 0) {
            for (MemberSign item : signRecord) {
                days.add(Integer.parseInt(DateUtil.format(new Date(item.getCreateTime() * 1000), "dd")));
            }
        }

        vo.setDays(days);
        vo.setPeriod(period);
        return vo;
    }

    /**
     * 获取日签到奖励
     *
     * @param param
     * @return
     */
    @Override
    public List<MemberSignDayRecordVo> signDayRecord(MemberSignDayRecordParam param) {

        return null;
    }

    /**
     * 获取用户签到设置
     *
     * @param param
     * @return
     */
    @Override
    public MemberSignConfigVo signConfig(MemberSignConfigParam param) {
        JSONObject config = coreConfigService.getConfigValue(RequestUtils.siteId(), "SIGN_CONFIG");
        MemberSignConfigVo signConfig = JSONUtil.toBean(config, MemberSignConfigVo.class);

        if (!signConfig.getDayAward().isEmpty()) {
            signConfig.setDayAward(coreMemberService.getGiftContent(RequestUtils.siteId(), signConfig.getDayAward(), "member_sign"));
        }
        if (!signConfig.getContinueAward().isEmpty()) {
            JSONArray continueAward = new JSONArray();
            for (int i = 0; i < signConfig.getContinueAward().size(); i++) {
                JSONObject awardItem = new JSONObject();
                JSONObject item = signConfig.getContinueAward().getJSONObject(i);
                awardItem.set("continue_sign", item.get("continue_sign"));

                item.remove("continue_sign");
                item.remove("continue_tag");
                item.remove("receive_limit");
                item.remove("receive_num");

                JSONObject award = coreMemberService.getGiftContent(RequestUtils.siteId(), item, "member_sign_continue");

                List<String> content = new ArrayList<>();
                String icon = "";

                for (String key : award.keySet()) {
                    if (award.getJSONObject(key).get("content") != null) {
                        content.add(award.getByPath(key + ".content.text", String.class));
                        icon = award.getByPath(key + ".content.icon", String.class);
                    }
                }
                JSONObject gift = new JSONObject();
                if (content.size() > 0) {
                    gift.set("text", String.join("+", content));
                    gift.set("icon", content.size() > 1 ? "static/resource/images/member/sign/pack01.png" : icon);
                }

                awardItem.set("gift", new JSONObject().set("total", gift));
                continueAward.add(awardItem);
            }
            signConfig.setContinueAward(continueAward);
        }

        // 获取今天的签到
        MemberSign todayMemberSign = this.queryMemberSign(param.siteId(), param.memberId(), DateUtils.getFormatTodayString());
        signConfig.setIsSign(todayMemberSign == null ? false : true);

        // 获取昨天的签到
        MemberSign yesterdayMemberSign = this.queryMemberSign(param.siteId(), param.memberId(), DateUtils.getFormatYesterdayString());
        if (todayMemberSign == null) {
            signConfig.setDays(yesterdayMemberSign == null ? 0 : yesterdayMemberSign.getDays());
        } else {
            signConfig.setDays(todayMemberSign.getDays());
        }
        return signConfig;
    }

    /**
     * @param siteId
     * @return
     */
    private MemberSignConfigVo getSystemSignConfig(Integer siteId) {
        JSONObject config = coreConfigService.getConfigValue(RequestUtils.siteId(), "SIGN_CONFIG");
        MemberSignConfigVo signConfig = JSONUtil.toBean(config, MemberSignConfigVo.class);

        if (!signConfig.getDayAward().isEmpty()) {
            signConfig.setDayAward(coreMemberService.getGiftContent(RequestUtils.siteId(), signConfig.getDayAward(), "member_sign"));
        }
        if (!signConfig.getContinueAward().isEmpty()) {
            JSONArray continueAward = new JSONArray();
            for (int i = 0; i < signConfig.getContinueAward().size(); i++) {
                JSONObject item = signConfig.getContinueAward().getJSONObject(i);
                JSONObject gift = JSONUtil.parseObj(signConfig.getContinueAward().getJSONObject(i).toString());
                gift.remove("continue_sign");
                gift.remove("continue_tag");
                gift.remove("receive_limit");
                gift.remove("receive_num");
                JSONObject award = coreMemberService.getGiftContent(RequestUtils.siteId(), gift, "member_sign_continue");
                continueAward.add(JsonModuleLoader.deepMerge(item, award));
            }
            signConfig.setContinueAward(continueAward);
        }
        return signConfig;
    }

    /**
     * @param siteId
     * @param memberId
     * @param formatDayTime
     * @return
     */
    private MemberSign queryMemberSign(Integer siteId, Integer memberId, String formatDayTime) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("site_id", siteId);
        queryWrapper.eq("member_id", memberId);
        queryWrapper.eq("DATE_FORMAT(FROM_UNIXTIME(create_time), '%Y-%m-%d')", formatDayTime);
        return this.memberSignMapper.selectOne(queryWrapper);
    }

}
