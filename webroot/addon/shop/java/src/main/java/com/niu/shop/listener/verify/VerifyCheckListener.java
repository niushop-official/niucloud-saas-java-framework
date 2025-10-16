package com.niu.shop.listener.verify;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.niu.core.common.annotation.EventCallback;
import com.niu.core.event.sys.VerifyCheckEventDefiner;
import com.niu.shop.entity.goods.ShopGoods;
import com.niu.shop.entity.order.ShopOrderGoods;
import com.niu.shop.enums.order.OrderDeliveryStatusEnum;
import com.niu.shop.enums.order.OrderGoodsRefundEnum;
import com.niu.shop.mapper.goods.ShopGoodsMapper;
import com.niu.shop.mapper.order.ShopOrderGoodsMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@EventCallback("shop")
@Component
public class VerifyCheckListener extends VerifyCheckEventDefiner {

    @Resource
    ShopOrderGoodsMapper shopOrderGoodsMapper;

    @Resource
    ShopGoodsMapper shopGoodsMapper;

    @Override
    public VerifyCheckEventResult handleCallback(VerifyCheckEvent event) {
        Integer siteId = event.getSiteId();
        Map<String, Object> data = event.getData();
        VerifyCheckEventResult result = new VerifyCheckEventResult();
        data.put("is_can_use", 1);
        data.put("un_use_msg", new ArrayList<>());
        result.setData(data);
        switch (data.get("type").toString()) {
            case "shopVirtualGoods":
                return handleVirtualGoods(siteId, data, result);
            case "shopPickUpOrder":
                return handlePickUpOrder(siteId, data, result);
            default:
                return null;
        }
    }

    private VerifyCheckEventResult handleVirtualGoods(Integer siteId, Map<String, Object> data, VerifyCheckEventResult result) {
        Integer orderGoodsId = getInteger((Map<String, Object>) data.get("data"), "order_goods_id");
        List<String> unUseMsgList = new ArrayList<>();
        ShopOrderGoods orderGoods = shopOrderGoodsMapper.selectOne(new LambdaQueryWrapper<ShopOrderGoods>()
                .eq(ShopOrderGoods::getSiteId, siteId)
                .eq(ShopOrderGoods::getOrderGoodsId, orderGoodsId));

        if (orderGoods == null) {
            return result;
        }
        ShopGoods goods = shopGoodsMapper.selectById(orderGoods.getGoodsId());

        String unUseMsg = "";

        // 检查配送状态
        if (OrderDeliveryStatusEnum.TAKED.getStatus().equals(orderGoods.getDeliveryStatus()) ||
                OrderDeliveryStatusEnum.EXPIRE.getStatus().equals(orderGoods.getDeliveryStatus())) {
            String msg = "商品已核销或已过期";
            unUseMsgList.add(msg);
            data.put("is_can_use", 0);
            unUseMsg = msg;
        }
        // 检查退款状态
        else if (OrderGoodsRefundEnum.REFUNDING.getStatus().equals(orderGoods.getStatus()) ||
                OrderGoodsRefundEnum.REFUND_FINISH.getStatus().equals(orderGoods.getStatus())) {
            String msg = "当前商品项存在退款";
            unUseMsgList.add(msg);
            data.put("is_can_use", 0);
            unUseMsg = msg;
        }
        // 检查商品状态
        else if (goods == null || goods.getStatus() != 1) {
            String msg = "商品已下架";
            unUseMsgList.add(msg);
            data.put("is_can_use", 0);
            unUseMsg = msg;
        }

        // 设置返回结果
        String valueStr = data.get("value").toString();
        JSONObject valueObj = JSONUtil.parseObj(valueStr);
        JSONArray listObj = valueObj.getJSONArray("list");
        Map<String, Object> firstItem = (Map<String, Object>) listObj.get(0);
        firstItem.put("un_use_msg", unUseMsg);
        listObj.set(0, firstItem);
        data.put("list", listObj);
        data.put("un_use_msg", unUseMsgList);
        result.setData(data);
        return result;
    }

    private VerifyCheckEventResult handlePickUpOrder(Integer siteId, Map<String, Object> data, VerifyCheckEventResult result) {
        Integer orderId = getInteger((Map<String, Object>) data.get("data"), "order_id");

        // 查询订单商品列表
        List<ShopOrderGoods> orderGoodsList = shopOrderGoodsMapper.selectList(new LambdaQueryWrapper<ShopOrderGoods>()
                .eq(ShopOrderGoods::getSiteId, siteId)
                .eq(ShopOrderGoods::getOrderId, orderId));

        String valueStr = result.getData().get("value").toString();
        JSONObject valueObj = JSONUtil.parseObj(valueStr);
        JSONArray listJson = valueObj.getJSONArray("list");
        List<Map> verifyList = JSONUtil.toList(listJson, Map.class);
        List<String> unUseMsgList = new ArrayList<>();
        for (Map<String, Object> item : verifyList) {
            Integer orderGoodsId = getInteger(item, "order_goods_id");
            item.put("un_use_msg", "");

            for (ShopOrderGoods orderGoods : orderGoodsList) {
                if (orderGoodsId.equals(orderGoods.getOrderGoodsId())) {
                    // 检查退款状态
                    if (OrderGoodsRefundEnum.REFUNDING.getStatus().equals(orderGoods.getStatus()) ||
                            OrderGoodsRefundEnum.REFUND_FINISH.getStatus().equals(orderGoods.getStatus())) {
                        String msg = "当前商品项存在退款";
                        data.put("is_can_use", 0);
                        item.put("un_use_msg", msg);
                        unUseMsgList.add(msg);
                    }
                    // 检查商品状态
                    else {
                        ShopGoods goods = shopGoodsMapper.selectById(orderGoods.getGoodsId());
                        if (goods == null || goods.getStatus() != 1) {
                            String msg = "商品已下架";
                            data.put("is_can_use", 0);
                            item.put("un_use_msg", msg);
                            unUseMsgList.add(msg);
                        }
                    }
                }
            }
        }
        data.put("list", verifyList);
        data.put("un_use_msg", unUseMsgList);
        result.setData(data);
        return result;
    }

    private Integer getInteger(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) {
            return 0;
        }
        if (value instanceof Integer) {
            return (Integer) value;
        }
        try {
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

}
