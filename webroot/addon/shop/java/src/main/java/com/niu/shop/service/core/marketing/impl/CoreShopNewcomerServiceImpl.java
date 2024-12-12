package com.niu.shop.service.core.marketing.impl;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.niu.core.common.utils.date.DateUtils;
import com.niu.core.entity.member.Member;
import com.niu.core.mapper.member.MemberMapper;
import com.niu.shop.entity.active.ShopActive;
import com.niu.shop.entity.newconsumer.ShopNewcomerMemberRecords;
import com.niu.shop.entity.order.ShopOrder;
import com.niu.shop.enums.active.ActiveClassEnum;
import com.niu.shop.enums.active.ActiveStatusEnum;
import com.niu.shop.enums.order.OrderStatusEnum;
import com.niu.shop.mapper.active.ShopActiveMapper;
import com.niu.shop.mapper.newconsumer.ShopNewcomerMemberRecordsMapper;
import com.niu.shop.mapper.order.ShopOrderMapper;
import com.niu.shop.service.core.marketing.ICoreShopNewcomerService;
import com.niu.shop.service.core.marketing.model.ShopNewcomerActiveValue;
import com.niu.shop.service.core.marketing.model.ShopNewcomerBannerList;
import com.niu.shop.service.core.marketing.vo.CoreShopNewcomerConfigVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CoreShopNewcomerServiceImpl extends ServiceImpl<ShopNewcomerMemberRecordsMapper, ShopNewcomerMemberRecords> implements ICoreShopNewcomerService {
    private static final String NEVER_ORDER = "never_order";
    private static final String ASSIGN_TIME_ORDER = "assign_time_order";
    private static final String ASSIGN_TIME_REGISTER = "assign_time_register";

    private final MemberMapper memberMapper;
    private final ShopOrderMapper shopOrderMapper;
    private final ShopActiveMapper shopActiveMapper;
    private final ShopNewcomerMemberRecordsMapper shopNewcomerMemberRecordsMapper;

    @Override
    public CoreShopNewcomerConfigVo getConfig(Integer siteId) {
        LambdaQueryWrapper<ShopActive> activeLambdaQueryWrapper = new LambdaQueryWrapper<>();
        activeLambdaQueryWrapper.eq(ShopActive::getSiteId, siteId)
                .eq(ShopActive::getActiveClass, ActiveClassEnum.NEWCOMER_DISCOUNT.getType());
        List<ShopActive> actives = shopActiveMapper.selectList(activeLambdaQueryWrapper);
        CoreShopNewcomerConfigVo vo = new CoreShopNewcomerConfigVo();
        if (ObjectUtil.isEmpty(actives)) {
            vo.setActiveId(0);
            vo.setActiveStatus("close");
            vo.setValidityType("day");
            vo.setValidityDay(3);
            vo.setValidityTime(DateUtils.timestampToString((System.currentTimeMillis() / 1000) + 86400 * 3));
            vo.setParticipationWay("never_order");
            vo.setAppointTime(DateUtils.timestampToString(System.currentTimeMillis() / 1000));
            vo.setLimitNum(1);
            ShopNewcomerBannerList bannerList = new ShopNewcomerBannerList();
            bannerList.setImageUrl("addon/shop/newcomer/banner.jpg");

            ShopNewcomerBannerList.ToLink toLink = new ShopNewcomerBannerList.ToLink();
            toLink.setName("");
            bannerList.setToLink(toLink);
            vo.setBannerList(ListUtil.of(bannerList));

            String defaultActiveDesc = "1、新人价是面向" +
                    getParticipationWayContent(vo.getParticipationWay()) +
                    "提供的一种专属优惠价格，同一账号仅限享受一次优惠；\n" +
                    "2、仅限" +
                    getParticipationWayText(vo.getParticipationWay(), vo.getAppointTime()) +
                    "可参与；\n" +
                    "3、活动有效期：" +
                    getValidityTypeText(vo.getValidityType(), vo.getValidityDay().toString(), vo.getValidityTime());
            vo.setActiveDesc(defaultActiveDesc);
        } else {
            ShopActive active = actives.get(0);
            ShopNewcomerActiveValue activeValue = JSON.parseObject(active.getActiveValue(), ShopNewcomerActiveValue.class);
            if (activeValue == null) {
                vo.setValidityType("day");
                vo.setValidityDay(3);
                vo.setValidityTime(DateUtils.timestampToString((System.currentTimeMillis() / 1000) + 86400 * 3));
                vo.setParticipationWay("never_order");
                vo.setAppointTime(DateUtils.timestampToString(System.currentTimeMillis() / 1000));
                vo.setLimitNum(1);
                ShopNewcomerBannerList bannerList = new ShopNewcomerBannerList();
                bannerList.setImageUrl("addon/shop/newcomer/banner.jpg");
            } else {
                vo.setValidityType(Optional.ofNullable(activeValue.getValidityType()).orElse("day"));
                vo.setValidityDay(Optional.ofNullable(activeValue.getValidityDay()).orElse(3));

                long validityTime = Optional.of(activeValue.getValidityTime()).orElse((System.currentTimeMillis() / 1000) + 86400 * 3);
                vo.setValidityTime(DateUtils.timestampToString(validityTime));
                vo.setParticipationWay(Optional.ofNullable(activeValue.getParticipationWay()).orElse("never_order"));

                Long appointTime = Optional.of(activeValue.getAppointTime()).orElse(System.currentTimeMillis() / 1000);
                vo.setAppointTime(DateUtils.timestampToString(appointTime));
                vo.setLimitNum(Optional.ofNullable(activeValue.getLimitNum()).orElse(1));
                vo.setBannerList(activeValue.getBannerList());
            }

            vo.setSiteId(active.getSiteId());
            vo.setActiveId(Optional.ofNullable(active.getActiveId()).orElse(0));
            vo.setActiveStatus(Optional.ofNullable(active.getActiveStatus()).orElse("active"));
            vo.setActiveDesc(active.getActiveDesc());
        }

        return vo;
    }

    @Override
    public boolean checkIfNewcomer(Integer siteId, Integer memberId) {
        if (ObjectUtil.isEmpty(memberId)) {
            return false;
        }

        boolean isConsumer = false;
        CoreShopNewcomerConfigVo config = getConfig(siteId);
        if (ObjectUtil.isEmpty(config) || !ActiveStatusEnum.ACTIVE.getStatus().equals(config.getActiveStatus())) {
            return isConsumer;
        }

        LambdaQueryWrapper<ShopOrder> shopOrderLambdaQueryWrapper = new LambdaQueryWrapper<>();
        shopOrderLambdaQueryWrapper.eq(ShopOrder::getSiteId, siteId)
                .eq(ShopOrder::getMemberId, memberId)
                .in(ShopOrder::getStatus, ListUtil.of(OrderStatusEnum.WAIT_PAY.getStatus(),
                        OrderStatusEnum.WAIT_DELIVERY.getStatus(),
                        OrderStatusEnum.WAIT_TAKE.getStatus(), OrderStatusEnum.FINISH.getStatus()));

        Long appointTime = DateUtils.StringToTimestamp(config.getAppointTime());
        switch (config.getParticipationWay()) {
            case ASSIGN_TIME_ORDER:
                shopOrderLambdaQueryWrapper.lt(ShopOrder::getCreateTime, appointTime);
            case NEVER_ORDER:
                isConsumer = shopOrderMapper.selectCount(shopOrderLambdaQueryWrapper) == 0;
                break;
            case ASSIGN_TIME_REGISTER:
                Member member = memberMapper.selectById(memberId);
                isConsumer = member.getCreateTime() >= appointTime;
                break;
        }

        if (!isConsumer) {
            return isConsumer;
        }

        LambdaQueryWrapper<ShopNewcomerMemberRecords> shopNewcomerMemberRecordsLambdaQueryWrapper = new LambdaQueryWrapper<>();
        shopNewcomerMemberRecordsLambdaQueryWrapper.eq(ShopNewcomerMemberRecords::getSiteId, siteId)
                .eq(ShopNewcomerMemberRecords::getMemberId, memberId);

        List<ShopNewcomerMemberRecords> shopNewcomerMemberRecords = shopNewcomerMemberRecordsMapper.selectList(shopNewcomerMemberRecordsLambdaQueryWrapper);
        if (ObjectUtil.isEmpty(shopNewcomerMemberRecords)) {
            long validityTime = getNewcomerEndTime(config);
            ShopNewcomerMemberRecords records = new ShopNewcomerMemberRecords();
            records.setSiteId(siteId);
            records.setMemberId(memberId);
            records.setValidityTime(validityTime);
            shopNewcomerMemberRecordsMapper.insert(records);
            return isConsumer;
        }

        ShopNewcomerMemberRecords records = shopNewcomerMemberRecords.get(0);
        return records.getValidityTime() >= System.currentTimeMillis() / 1000 && records.getIsJoin() != 1;
    }

    @Override
    public void afterSave() {
        List<CoreShopNewcomerConfigVo> configs = getConfig();
        List<ShopNewcomerMemberRecords> list = new LinkedList<>();
        for (CoreShopNewcomerConfigVo config : configs) {
            LambdaQueryWrapper<ShopNewcomerMemberRecords> shopNewcomerMemberRecordsLambdaQueryWrapper = new LambdaQueryWrapper<>();
            shopNewcomerMemberRecordsLambdaQueryWrapper.eq(ShopNewcomerMemberRecords::getSiteId, config.getSiteId())
                    .eq(ShopNewcomerMemberRecords::getIsJoin, 0)
                    .eq(ShopNewcomerMemberRecords::getOrderId, 0);

            List<ShopNewcomerMemberRecords> records = shopNewcomerMemberRecordsMapper.selectList(shopNewcomerMemberRecordsLambdaQueryWrapper);
            records.forEach(record -> {
                record.setValidityTime(getNewcomerEndTime(config));
                record.setUpdateTime(System.currentTimeMillis() / 1000);
            });
            list.addAll(records);
        }
        updateBatchById(list);
    }

    private long getNewcomerEndTime(CoreShopNewcomerConfigVo config) {
        switch (config.getValidityType()) {
            case "day":
                return System.currentTimeMillis() / 1000 + config.getValidityDay() * 86400;
            case "date":
                return DateUtils.StringToTimestamp(config.getValidityTime());
            default:
                return 0L;
        }
    }

    private String getParticipationWayContent(String participationWay) {
        switch (participationWay) {
            case NEVER_ORDER:
                return "从未下过单的会员";
            case ASSIGN_TIME_ORDER:
                return "指定时间内未下过单的会员";
            case ASSIGN_TIME_REGISTER:
                return "指定时间内注册的会员";
            default:
                return "";
        }
    }

    private String getParticipationWayText(String participationWay, String appointTime) {
        switch (participationWay) {
            case NEVER_ORDER:
                return "从未下过单的会员";
            case ASSIGN_TIME_ORDER:
                return appointTime + "之前未下过单的会员";
            case ASSIGN_TIME_REGISTER:
                return appointTime + "之后注册的会员";
            default:
                return "";
        }
    }

    private String getValidityTypeText(String validityType, String validityDay, String validityTime) {
        switch (validityType) {
            case "day":
                return "参与活动后" + validityDay + "天内。";
            case "date":
                return validityTime + "后截止。";
            default:
                return "";
        }
    }

    private List<CoreShopNewcomerConfigVo> getConfig() {
        LambdaQueryWrapper<ShopActive> activeLambdaQueryWrapper = new LambdaQueryWrapper<>();
        activeLambdaQueryWrapper.eq(ShopActive::getActiveClass, ActiveClassEnum.NEWCOMER_DISCOUNT.getType());
        List<ShopActive> actives = shopActiveMapper.selectList(activeLambdaQueryWrapper);

        List<CoreShopNewcomerConfigVo> list = new ArrayList<>(actives.size());
        for (ShopActive active : actives) {
            ShopNewcomerActiveValue activeValue = JSON.parseObject(active.getActiveValue(), ShopNewcomerActiveValue.class);

            CoreShopNewcomerConfigVo vo = new CoreShopNewcomerConfigVo();
            vo.setActiveId(Optional.ofNullable(active.getActiveId()).orElse(0));
            vo.setActiveStatus(Optional.ofNullable(active.getActiveStatus()).orElse(ActiveStatusEnum.ACTIVE.getStatus()));
            vo.setValidityType(Optional.ofNullable(activeValue.getValidityType()).orElse("day"));
            vo.setValidityDay(Optional.ofNullable(activeValue.getValidityDay()).orElse(3));

            long validityTime = Optional.of(activeValue.getValidityTime()).orElse((System.currentTimeMillis() / 1000) + 86400 * 3);
            vo.setValidityTime(DateUtils.timestampToString(validityTime));
            vo.setParticipationWay(Optional.ofNullable(activeValue.getParticipationWay()).orElse("never_order"));

            Long appointTime = Optional.of(activeValue.getAppointTime()).orElse(System.currentTimeMillis() / 1000);
            vo.setAppointTime(DateUtils.timestampToString(appointTime));
            vo.setLimitNum(Optional.ofNullable(activeValue.getLimitNum()).orElse(1));

            ShopNewcomerBannerList bannerList = new ShopNewcomerBannerList();
            bannerList.setImageUrl("addon/shop/newcomer/banner.jpg");

            ShopNewcomerBannerList.ToLink toLink = new ShopNewcomerBannerList.ToLink();
            toLink.setName("");
            bannerList.setToLink(toLink);
            vo.setBannerList(ListUtil.of(bannerList));

            String defaultActiveDesc = "1、新人价是面向" +
                    getParticipationWayContent(vo.getParticipationWay()) +
                    "提供的一种专属优惠价格，同一账号仅限享受一次优惠；\n" +
                    "2、仅限" +
                    getParticipationWayText(vo.getParticipationWay(), vo.getAppointTime()) +
                    "可参与；\n" +
                    "3、活动有效期：" +
                    getValidityTypeText(vo.getValidityType(), vo.getValidityDay().toString(), vo.getValidityTime());

            vo.setActiveDesc(Optional.ofNullable(active.getActiveDesc()).orElse(defaultActiveDesc));
            vo.setSiteId(active.getSiteId());
            list.add(vo);
        }

        return list;
    }

}
