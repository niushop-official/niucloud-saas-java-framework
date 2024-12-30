package com.niu.shop.service.core.refund.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONUtil;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.service.core.app.helper.EventAndSubscribeOfPublisher;
import com.niu.shop.entity.order.ShopOrderGoods;
import com.niu.shop.entity.order.ShopOrderRefund;
import com.niu.shop.enums.order.OrderGoodsRefundEnum;
import com.niu.shop.enums.order.OrderRefundSourceEnum;
import com.niu.shop.enums.order.OrderRefundStatusEnum;
import com.niu.shop.event.order.OrderRefundFinishAfterEvent;
import com.niu.shop.event.order.ShopOrderRefundActiveCreateAfterEvent;
import com.niu.shop.mapper.order.ShopOrderGoodsMapper;
import com.niu.shop.mapper.order.ShopOrderRefundMapper;
import com.niu.shop.service.core.refund.ICoreRefundActionService;
import com.niu.shop.service.core.refund.ICoreRefundService;
import com.niu.shop.service.core.refund.param.ActiveRefundParam;
import com.niu.shop.service.core.refund.vo.OrderRefundMoneyVo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

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
            model.setRemark(param.getShopActiveRefundRemark());
            model.setSource(OrderRefundSourceEnum.ACTIVE_REFUND.getType().toString());
            model.setIsRefundDelivery(orderGoods.getIsRefundDelivery());
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

    private String createRefundNo() {
        Snowflake snowflake = IdUtil.getSnowflake(1, 1);
        long id = snowflake.nextId();
        return DateUtil.format(DateUtil.date(System.currentTimeMillis()), "yyyyMMdd") + id;
    }
}
