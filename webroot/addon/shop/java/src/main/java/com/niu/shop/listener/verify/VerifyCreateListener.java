package com.niu.shop.listener.verify;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.annotation.EventCallback;
import com.niu.core.common.component.context.event.Event;
import com.niu.core.common.component.context.event.EventResult;
import com.niu.core.common.exception.CommonException;
import com.niu.core.event.sys.VerifyCreateEventDefiner;
import com.niu.shop.entity.goods.ShopGoods;
import com.niu.shop.entity.order.ShopOrder;
import com.niu.shop.entity.order.ShopOrderGoods;
import com.niu.shop.enums.order.OrderDeliveryStatusEnum;
import com.niu.shop.enums.order.OrderGoodsRefundEnum;
import com.niu.shop.enums.order.OrderStatusEnum;
import com.niu.shop.mapper.goods.ShopGoodsMapper;
import com.niu.shop.mapper.order.ShopOrderGoodsMapper;
import com.niu.shop.mapper.order.ShopOrderMapper;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.Date;

@EventCallback("shop")
@Component
public class VerifyCreateListener extends VerifyCreateEventDefiner {

    @Resource
    ShopOrderGoodsMapper shopOrderGoodsMapper;

    @Resource
    ShopOrderMapper shopOrderMapper;

    @Resource
    ShopGoodsMapper shopGoodsMapper;

    @Override
    public VerifyCreateEventResult handleCallback(VerifyCreateEvent event) {
        if (event.getType().equals("shopVirtualGoods")) {
            Integer memberId = event.getMemberId();
            Integer orderGoodsId = Integer.valueOf((String) ObjectUtil.defaultIfNull(event.getData().get("order_goods_id"), 0));

            ShopOrderGoods orderGoods = shopOrderGoodsMapper.selectOne(new QueryWrapper<ShopOrderGoods>().eq("order_goods_id", orderGoodsId).eq("member_id", memberId));
            Assert.notNull(orderGoods, "订单不存在");

            if (orderGoods.getDeliveryStatus().equals(OrderDeliveryStatusEnum.TAKED.getStatus().toString()) || orderGoods.getDeliveryStatus().equals(OrderDeliveryStatusEnum.EXPIRE.getStatus().toString())) {
                throw new CommonException("商品已核销或已过期");
            }
            if (orderGoods.getStatus().equals(OrderGoodsRefundEnum.REFUNDING.getStatus()) || orderGoods.getStatus().equals(OrderGoodsRefundEnum.REFUND_FINISH.getStatus())) {
                throw new CommonException("当前订单项正在退款或者已退款");
            }
            if (orderGoods.getVerifyCount() >= orderGoods.getNum()) throw new CommonException("已达到最大核销次数");

            ShopOrder order = shopOrderMapper.selectById(orderGoods.getOrderId());
            Assert.notNull(order, "订单不存在");

            if (order.getStatus().equals(OrderStatusEnum.FINISH.getStatus().toString()) || order.getStatus().equals(OrderStatusEnum.CLOSE.getStatus().toString())) {
                throw new CommonException("订单已关闭或已完成");
            }

            ShopGoods goods = shopGoodsMapper.selectById(orderGoods.getGoodsId());
            Assert.notNull(goods, "商品不存在");

            if (!goods.getVirtualReceiveType().equals("verify")) throw new CommonException("当前商品不支持核销");

            Integer count = orderGoods.getNum() - orderGoods.getVerifyCount();

            JSONObject data = new JSONObject();
            JSONArray list = new JSONArray();
            list.add(new JSONObject()
                    .set("name", orderGoods.getGoodsName())
                    .set("sub_name", orderGoods.getSkuName())
                    .set("cover", orderGoods.getGoodsImage())
                    .set("num", orderGoods.getNum())
            );
            data.put("list", list);
            data.putByPath("content.fixed", new JSONArray());
            JSONArray diy = new JSONArray();
            JSONObject diyItem = new JSONObject();
            JSONArray diyList = new JSONArray();
            diyList.add(new JSONObject().set("title", "订单编号").set("value", order.getOrderNo()));
            diyList.add(new JSONObject().set("title", "支付时间").set("value", DateUtil.formatDateTime(new Date(order.getPayTime() * 1000))));
            diyItem.set("title", "订单信息");
            diyItem.set("list", diyList);
            diy.add(diyItem);
            data.putByPath("content.diy", diy);

            VerifyCreateEventResult result = new VerifyCreateEventResult();
            result.setCount(count);
            result.setRelateTag(orderGoodsId.toString());
            result.setExpireTime(orderGoods.getVerifyExpireTime());
            result.setBody(orderGoods.getGoodsName() + orderGoods.getSkuName());
            result.setData(data.toString());

            return result;
        }
        return null;
    }

}
