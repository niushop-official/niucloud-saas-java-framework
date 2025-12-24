package com.niu.shop.service.core.refund.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.service.core.app.helper.EventAndSubscribeOfPublisher;
import com.niu.shop.entity.order.ShopOrderGoods;
import com.niu.shop.entity.order.ShopOrderRefund;
import com.niu.shop.enums.order.*;
import com.niu.shop.event.order.OrderRefundFinishAfterEvent;
import com.niu.shop.event.order.ShopOrderRefundActiveCreateAfterEvent;
import com.niu.shop.event.refund.AfterShopOrderRefundCloseEvent;
import com.niu.shop.mapper.order.ShopOrderGoodsMapper;
import com.niu.shop.mapper.order.ShopOrderRefundMapper;
import com.niu.shop.service.core.refund.ICoreRefundActionService;
import com.niu.shop.service.core.refund.ICoreRefundService;
import com.niu.shop.service.core.refund.param.ActiveRefundCloseParam;
import com.niu.shop.service.core.refund.param.ActiveRefundParam;
import com.niu.shop.service.core.refund.vo.OrderRefundMoneyVo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CoreRefundActionServiceImpl implements ICoreRefundActionService {

    @Resource
    ICoreRefundService coreRefundService;

    @Resource
    ShopOrderRefundMapper shopOrderRefundMapper;

    @Resource
    ShopOrderGoodsMapper shopOrderGoodsMapper;

    /**
     * 商家主动退款
     * @param param
     */
    @Override
    @Transactional
    public void shopActiveRefund(ActiveRefundParam param) {
        OrderRefundMoneyVo checkData = coreRefundService.refundCheck(param.getOrderGoodsIds(), param.getRefundMoney());

        List<OrderRefundMoneyVo.ShopOrderGoods> orderGoodsList = checkData.getOrderGoodsList();

        for (OrderRefundMoneyVo.ShopOrderGoods orderGoods : orderGoodsList) {
            ShopOrderRefund model = new ShopOrderRefund();
            BeanUtil.copyProperties(orderGoods, model);
            model.setOrderRefundNo(createRefundNo());
            model.setStatus(OrderRefundStatusEnum.STORE_AGREE_REFUND_WAIT_TRANSFER.getStatus().toString());
            model.setCreateTime(System.currentTimeMillis() / 1000);
            model.setApplyMoney(orderGoods.getItemRefundMoney());
            model.setMoney(orderGoods.getItemRefundMoney());
            model.setVoucher("");
            model.setReason("商家主动退款");
            model.setRemark(param.getShopActiveRefundRemark());
            model.setSource(OrderRefundSourceEnum.ACTIVE_REFUND.getType().toString());
            model.setIsRefundDelivery(orderGoods.getIsRefundDelivery());
            model.setRefundType(OrderRefundTypeEnum.ONLY_REFUND.getType().toString());
            model.setOrderId(orderGoods.getOrderId());
            model.setOrderGoodsId(orderGoods.getOrderGoodsId());
            model.setMemberId(orderGoods.getMemberId());
            shopOrderRefundMapper.insert(model);

            ShopOrderGoods orderGoodsModel = new ShopOrderGoods();
            orderGoodsModel.setOrderGoodsId(orderGoods.getOrderGoodsId());
            orderGoodsModel.setOrderRefundNo(model.getOrderRefundNo());
            orderGoodsModel.setShopActiveRefund(1);
            orderGoodsModel.setShopActiveRefundMoney(orderGoods.getItemRefundMoney());
            orderGoodsModel.setStatus(OrderGoodsRefundEnum.REFUNDING.getStatus());
            shopOrderGoodsMapper.updateById(orderGoodsModel);

            ShopOrderRefundActiveCreateAfterEvent event = new ShopOrderRefundActiveCreateAfterEvent();
            event.setSiteId(model.getSiteId());
            event.addAppSign("shop");
            event.setName("ShopOrderRefundActiveCreateAfterEvent");
            event.setRefundParam(param);
            event.setRefund(model);
            event.setOrderGoods(orderGoods);
            EventAndSubscribeOfPublisher.publishAll(event);
        }
    }

    @Override
    public void close(ActiveRefundCloseParam data, Integer refundStatus) {
        String orderRefundNo = data.getOrderRefundNo();
        String mainType = data.getMainType() != null ? data.getMainType() : "";

        // 查询退款信息
        LambdaQueryWrapper<ShopOrderRefund> queryWrapper = new LambdaQueryWrapper<ShopOrderRefund>()
                .eq(ShopOrderRefund::getOrderRefundNo, orderRefundNo);

        if (OrderLogMainTypeEnum.MEMBER.getType().equals(mainType)) {
            queryWrapper.eq(ShopOrderRefund::getMemberId, data.getMainId());
        }

        ShopOrderRefund orderRefund = shopOrderRefundMapper.selectOne(queryWrapper);
        if (orderRefund == null) {
            throw new CommonException("退款已失效");
        }

        if (Arrays.asList(
                OrderRefundStatusEnum.STORE_AGREE_REFUND_WAIT_TRANSFER.getStatus(),
                OrderRefundStatusEnum.STORE_REFUND_TRANSFERING.getStatus(),
                OrderRefundStatusEnum.FINISH.getStatus(),
                OrderRefundStatusEnum.CLOSE.getStatus(),
                OrderRefundStatusEnum.SHOP_ACTIVE_CLOSE_REFUND.getStatus()
        ).contains(orderRefund.getStatus())) {
            throw new CommonException("退款已完成或已关闭");
        }

        // 更新退款状态
        ShopOrderRefund updateRefund = new ShopOrderRefund();
        updateRefund.setRefundId(orderRefund.getRefundId());
        updateRefund.setStatus(refundStatus.toString());
        shopOrderRefundMapper.updateById(updateRefund);

        // 更新订单项状态
        LambdaUpdateWrapper<ShopOrderGoods> updateWrapper = new LambdaUpdateWrapper<ShopOrderGoods>()
                .eq(ShopOrderGoods::getOrderRefundNo, orderRefundNo)
                .set(ShopOrderGoods::getStatus, OrderGoodsRefundEnum.NORMAL.getStatus());
        shopOrderGoodsMapper.update(updateWrapper);

        // 触发事件
        Map<String, Object> refundData = new HashMap<>();
        refundData.putAll(BeanUtil.beanToMap(orderRefund));
        refundData.putAll(BeanUtil.beanToMap(updateRefund));

        AfterShopOrderRefundCloseEvent event = new AfterShopOrderRefundCloseEvent();
        event.setOrderRefundNo(orderRefundNo);
        event.setRefundData(refundData);
        event.setMainType(mainType);
        event.setMainId(data.getMainId());
        event.setSiteId(orderRefund.getSiteId());
        EventAndSubscribeOfPublisher.publishAll(event);
    }

    private String createRefundNo() {
        Snowflake snowflake = IdUtil.getSnowflake(1, 1);
        long id = snowflake.nextId();
        return DateUtil.format(DateUtil.date(System.currentTimeMillis()), "yyyyMMdd") + id;
    }
}
