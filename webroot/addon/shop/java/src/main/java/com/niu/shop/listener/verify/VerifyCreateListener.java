package com.niu.shop.listener.verify;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.niu.core.common.annotation.EventCallback;
import com.niu.core.common.exception.CommonException;
import com.niu.core.event.sys.VerifyCreateEventDefiner;
import com.niu.shop.entity.goods.ShopGoods;
import com.niu.shop.entity.order.ShopOrder;
import com.niu.shop.entity.order.ShopOrderGoods;
import com.niu.shop.enums.order.OrderDeliveryStatusEnum;
import com.niu.shop.enums.order.OrderStatusEnum;
import com.niu.shop.mapper.goods.ShopGoodsMapper;
import com.niu.shop.mapper.order.ShopOrderGoodsMapper;
import com.niu.shop.mapper.order.ShopOrderMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
        Integer siteId = event.getSiteId();
        Integer memberId = event.getMemberId();
        Map<String, Object> eventData = event.getData();

        if ("shopVirtualGoods".equals(event.getType())) {
            // 虚拟商品核销逻辑
            Integer orderGoodsId = Integer.valueOf(String.valueOf(eventData.getOrDefault("order_goods_id", 0)));

            // 查询订单商品信息
            ShopOrderGoods orderGoods = shopOrderGoodsMapper.selectOne(new LambdaQueryWrapper<ShopOrderGoods>()
                    .eq(ShopOrderGoods::getSiteId, siteId)
                    .eq(ShopOrderGoods::getOrderGoodsId, orderGoodsId)
                    .eq(ShopOrderGoods::getMemberId, memberId));
            if (orderGoods == null) {
                throw new CommonException("订单不存在");
            }

            // 检查配送状态
            if (OrderDeliveryStatusEnum.TAKED.getStatus().equals(orderGoods.getDeliveryStatus()) ||
                    OrderDeliveryStatusEnum.EXPIRE.getStatus().equals(orderGoods.getDeliveryStatus())) {
                throw new CommonException("商品已核销或已过期");
            }

            // 查询订单信息
            ShopOrder order = shopOrderMapper.selectOne(new LambdaQueryWrapper<ShopOrder>()
                    .eq(ShopOrder::getSiteId, siteId)
                    .eq(ShopOrder::getOrderId, orderGoods.getOrderId())
                    .eq(ShopOrder::getMemberId, memberId));
            if (order == null) {
                throw new CommonException("订单不存在");
            }

            // 检查订单状态
            if (OrderStatusEnum.FINISH.getStatus().equals(order.getStatus()) ||
                    OrderStatusEnum.CLOSE.getStatus().equals(order.getStatus())) {
                throw new CommonException("订单已关闭或已完成");
            }

            // 查询商品信息
            ShopGoods goods = shopGoodsMapper.selectById(orderGoods.getGoodsId());
            if (goods == null) {
                throw new CommonException("商品不存在");
            }

            // 检查商品核销类型
            if (!"verify".equals(goods.getVirtualReceiveType())) {
                throw new CommonException("当前商品不支持核销");
            }

            int count = orderGoods.getNum() - orderGoods.getVerifyCount();

            // 构建返回数据
            JSONObject data = new JSONObject();

            // 构建商品列表
            JSONArray goodsList = new JSONArray();
            JSONObject goodsItem = new JSONObject();
            goodsItem.putOpt("name", orderGoods.getGoodsName());
            goodsItem.putOpt("sub_name", orderGoods.getSkuName());
            goodsItem.putOpt("cover", orderGoods.getGoodsImage());
            goodsItem.putOpt("num", orderGoods.getNum());
            goodsItem.putOpt("verify_num", 1);
            goodsList.add(goodsItem);
            data.putOpt("list", goodsList);

            // 构建 content 部分
            JSONObject content = new JSONObject();
            content.putOpt("fixed", new JSONArray());

            JSONArray diyArray = new JSONArray();
            JSONObject orderInfoItem = new JSONObject();
            orderInfoItem.putOpt("title", "订单信息");

            JSONArray orderInfoList = new JSONArray();
            JSONObject orderNoItem = new JSONObject();
            orderNoItem.putOpt("title", "订单编号");
            orderNoItem.putOpt("value", order.getOrderNo());
            orderInfoList.add(orderNoItem);

            JSONObject payTimeItem = new JSONObject();
            payTimeItem.putOpt("title", "支付时间");
            payTimeItem.putOpt("value", order.getPayTime());
            orderInfoList.add(payTimeItem);

            orderInfoItem.putOpt("list", orderInfoList);
            diyArray.add(orderInfoItem);
            content.putOpt("diy", diyArray);

            data.putOpt("content", content);

            VerifyCreateEventResult result = new VerifyCreateEventResult();
            result.setCount(count);
            result.setRelateTag(String.valueOf(orderGoodsId));
            result.setBody(orderGoods.getGoodsName() + orderGoods.getSkuName());
            result.setExpireTime(orderGoods.getVerifyExpireTime() != null ?
                    orderGoods.getVerifyExpireTime() : 0);
            result.setData(data.toString());

            return result;


        } else if ("shopPickUpOrder".equals(event.getType())) {
            // 自提订单核销逻辑
            Integer orderId = Integer.valueOf(String.valueOf(eventData.getOrDefault("order_id", 0)));

            // 查询订单信息
            ShopOrder order = shopOrderMapper.selectOne(new LambdaQueryWrapper<ShopOrder>()
                    .eq(ShopOrder::getSiteId, siteId)
                    .eq(ShopOrder::getOrderId, orderId)
                    .eq(ShopOrder::getMemberId, memberId));
            if (order == null) {
                throw new CommonException("订单不存在");
            }

            // 检查配送类型
            if (!"store".equals(order.getDeliveryType())) {
                throw new CommonException("订单已关闭或已完成");
            }

            // 检查订单状态
            if (OrderStatusEnum.FINISH.getStatus().equals(order.getStatus()) ||
                    OrderStatusEnum.CLOSE.getStatus().equals(order.getStatus())) {
                throw new CommonException("订单已关闭或已完成");
            }

            // 查询订单商品列表
            List<ShopOrderGoods> orderGoodsList = shopOrderGoodsMapper.selectList(
                    new LambdaQueryWrapper<ShopOrderGoods>()
                            .eq(ShopOrderGoods::getSiteId, siteId)
                            .eq(ShopOrderGoods::getOrderId, orderId)
                            .eq(ShopOrderGoods::getMemberId, memberId));

            JSONArray goodsList = new JSONArray();
            long expireTime = 0;
            List<String> bodyParts = new ArrayList<>();

            for (ShopOrderGoods orderGoods : orderGoodsList) {
                // 跳过已核销或已过期的商品
                if (OrderDeliveryStatusEnum.TAKED.getStatus().equals(orderGoods.getDeliveryStatus()) ||
                        OrderDeliveryStatusEnum.EXPIRE.getStatus().equals(orderGoods.getDeliveryStatus())) {
                    continue;
                }

                JSONObject goodsItem = new JSONObject();
                goodsItem.putOpt("order_goods_id", orderGoods.getOrderGoodsId());
                goodsItem.putOpt("name", orderGoods.getGoodsName());
                goodsItem.putOpt("sub_name", orderGoods.getSkuName());
                goodsItem.putOpt("cover", orderGoods.getGoodsImage());
                goodsItem.putOpt("num", orderGoods.getNum());
                goodsItem.putOpt("verify_num", orderGoods.getNum());
                goodsList.add(goodsItem);

                // 计算最大过期时间
                if (orderGoods.getVerifyExpireTime() != null &&
                        orderGoods.getVerifyExpireTime() > expireTime) {
                    expireTime = orderGoods.getVerifyExpireTime();
                }

                bodyParts.add(orderGoods.getGoodsName() + orderGoods.getSkuName());
            }

            // 构建返回数据
            // 构建返回数据
            JSONObject data = new JSONObject();
            data.putOpt("list", goodsList);

            // 构建 content 部分
            JSONObject content = new JSONObject();
            content.putOpt("fixed", new JSONArray());

            JSONArray diyArray = new JSONArray();
            JSONObject orderInfoItem = new JSONObject();
            orderInfoItem.putOpt("title", "订单信息");

            JSONArray orderInfoList = new JSONArray();
            JSONObject orderNoItem = new JSONObject();
            orderNoItem.putOpt("title", "订单编号");
            orderNoItem.putOpt("value", order.getOrderNo());
            orderInfoList.add(orderNoItem);

            JSONObject payTimeItem = new JSONObject();
            payTimeItem.putOpt("title", "支付时间");
            payTimeItem.putOpt("value", order.getPayTime());
            orderInfoList.add(payTimeItem);

            orderInfoItem.putOpt("list", orderInfoList);
            diyArray.add(orderInfoItem);

            JSONObject takerInfo = new JSONObject();
            takerInfo.set("title", "提货人信息");
            JSONArray takerInfoItem = new JSONArray();
            takerInfoItem.add(new JSONObject()
                    .putOpt("title", "提货人")
                    .putOpt("value", order.getTakerName()));
            takerInfoItem.add(new JSONObject()
                    .putOpt("title", "预留手机")
                    .putOpt("value", order.getTakerMobile()));
            takerInfoItem.add(new JSONObject()
                    .putOpt("title", "预约提货时间")
                    .putOpt("value", order.getBuyerAskDeliveryTime()));
            takerInfo.set("list", takerInfoItem);
            diyArray.add(takerInfo);

            content.putOpt("diy", diyArray);

            data.putOpt("content", content);

            VerifyCreateEventResult result = new VerifyCreateEventResult();
            result.setCount(1);
            result.setRelateTag(String.valueOf(orderId));
            result.setBody(String.join(",", bodyParts).substring(0, Math.min(100, String.join(",", bodyParts).length())));
            result.setExpireTime(expireTime / 1000);
            result.setData(data.toString());

            return result;
        }

        return null;
    }
}
