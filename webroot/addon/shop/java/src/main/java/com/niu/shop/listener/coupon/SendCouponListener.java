package com.niu.shop.listener.coupon;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.niu.core.common.annotation.EventListen;
import com.niu.core.common.component.context.SpringContext;
import com.niu.core.common.component.context.listener.AbstractListener;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.StringUtils;
import com.niu.core.entity.member.Member;
import com.niu.core.entity.member.MemberLabel;
import com.niu.core.entity.member.MemberLevel;
import com.niu.core.mapper.member.MemberLabelMapper;
import com.niu.core.mapper.member.MemberLevelMapper;
import com.niu.core.mapper.member.MemberMapper;
import com.niu.core.service.admin.sys.ISysUserService;
import com.niu.shop.entity.coupon.ShopCouponSendRecords;
import com.niu.shop.enums.active.ActiveClassEnum;
import com.niu.shop.enums.coupon.SendCouponRangeTypeEnum;
import com.niu.shop.event.conpon.SendCouponEvent;
import com.niu.shop.event.marketing.ActiveStartAfterEvent;
import com.niu.shop.mapper.coupon.ShopCouponSendRecordsMapper;
import com.niu.shop.service.admin.marketing.IShopDiscountService;
import com.niu.shop.service.core.marketing.ICoreCouponMemberService;
import com.niu.shop.service.core.marketing.ICoreCouponService;
import jakarta.annotation.Resource;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@EventListen("shop")
@Component
public class SendCouponListener extends AbstractListener {

    private ShopCouponSendRecordsMapper shopCouponSendRecordsMapper;

    private MemberLabelMapper memberLabelMapper;

    private MemberLevelMapper memberLevelMapper;

    private MemberMapper memberMapper;

    private ICoreCouponMemberService coreCouponMemberService;


    @Autowired
    public void setShopCouponSendRecordsMapper(ShopCouponSendRecordsMapper shopCouponSendRecordsMapper) {
        this.shopCouponSendRecordsMapper = shopCouponSendRecordsMapper;
    }

    @Autowired
    public void setMemberLabelMapper(MemberLabelMapper memberLabelMapper) {
        this.memberLabelMapper = memberLabelMapper;
    }

    @Autowired
    public void setMemberLevelMapper(MemberLevelMapper memberLevelMapper) {
        this.memberLevelMapper = memberLevelMapper;
    }

    @Autowired
    public void setMemberMapper(MemberMapper memberMapper) {
        this.memberMapper = memberMapper;
    }

    @Autowired
    public void setCoreCouponMemberService(ICoreCouponMemberService coreCouponMemberService){
        this.coreCouponMemberService = coreCouponMemberService;
    }


    @Transactional(rollbackFor = Exception.class)
    public void handleEvent(SendCouponEvent event) {
        if (event.getRecordId() == null || event.getSiteId() == null) {
            log.warn("CouponSend 参数为空 结束");
            return;
        }
        log.info("CouponSend 发送优惠券开始");
        LambdaUpdateWrapper<ShopCouponSendRecords> uw = new LambdaUpdateWrapper<>();
        uw.eq(ShopCouponSendRecords::getId, event.getRecordId()).set(ShopCouponSendRecords::getStatus, "progress");
        shopCouponSendRecordsMapper.update(uw);

        ShopCouponSendRecords shopCouponSendRecords = shopCouponSendRecordsMapper.selectById(event.getRecordId());
        List<Integer> memberIds = getMemberIds(shopCouponSendRecords.getRangeType(), JSONUtil.parseObj(shopCouponSendRecords.getRangeParam()), event.getSiteId());
        if (memberIds == null || memberIds.isEmpty()){
            log.warn("CouponSend 优惠券没有查到发卷对象 结束");
            return;
        }
        log.info("CouponSend 优惠券开始发放，发放会员id为：{}", memberIds.stream().map(Object::toString).collect(Collectors.joining(",")));
        Integer successNum = coreCouponMemberService.sendCouponBatch(shopCouponSendRecords, memberIds);
        shopCouponSendRecords.setSuccessNum(successNum);
        shopCouponSendRecords.setStatus("success");
        shopCouponSendRecords.setUpdateTime(System.currentTimeMillis() / 1000);
        shopCouponSendRecords.setEndTime(System.currentTimeMillis() / 1000);
        shopCouponSendRecordsMapper.updateById(shopCouponSendRecords);
        log.info("CouponSend 优惠券发放结束");
    }

    private List<Integer> getMemberIds(String rangeType, JSONObject jsonObject, Integer siteId) {
        List<Integer> memberIds = new ArrayList<>();
        List<Member> members = new ArrayList<>();
        if (rangeType.equals(SendCouponRangeTypeEnum.ALL.getCode())) {
            // 全部会员
            members = memberMapper.selectList(new QueryWrapper<Member>().eq("site_id", siteId));
        } else if (rangeType.equals(SendCouponRangeTypeEnum.MEMBER.getCode())) {
            // 指定会员
            memberIds = (List<Integer>) jsonObject.get("member_ids");
        } else if (rangeType.equals(SendCouponRangeTypeEnum.MEMBER_LEVEL.getCode())) {
            // 会员等级
            JSONArray memberLevels = jsonObject.getJSONArray("member_level");
            if (memberLevels != null && !memberLevels.isEmpty()) {
                // 查询符合条件的会员
                members = memberMapper.selectList(new QueryWrapper<Member>()
                        .in("member_level", memberLevels)
                        .eq("site_id", siteId));

            }
        } else if (rangeType.equals(SendCouponRangeTypeEnum.MEMBER_LABEL.getCode())) {
            // 会员标签
            JSONArray memberLabels = jsonObject.getJSONArray("member_label");
            if (memberLabels != null && !memberLabels.isEmpty()) {
                // 查询符合条件的会员
                List<Member> memberAll = memberMapper.selectList(new QueryWrapper<Member>().eq("site_id", siteId));
                members = memberAll.stream()
                        .filter(member -> {
                            // 解析 member_label JSON 数组
                            JSONArray labels = JSONUtil.parseArray(member.getMemberLabel());
                            // 判断是否有交集
                            return !labels.isEmpty() &&
                                    labels.stream().anyMatch(memberLabels::contains);
                        })
                        .collect(Collectors.toList());
            }
        }
        if (members != null && !members.isEmpty()) {
            memberIds = members.stream().map(Member::getMemberId).collect(Collectors.toList());
        }
        return memberIds.isEmpty() ? null : memberIds;
    }


}
