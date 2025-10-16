package com.niu.shop.listener.third_addon.order;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.niu.core.common.annotation.EventCallback;
import com.niu.core.event.common.CommonEventDefiner;
import com.niu.shop.entity.order.ShopOrder;
import com.niu.shop.entity.order.ShopOrderRefund;
import com.niu.shop.enums.order.OrderRefundStatusEnum;
import com.niu.shop.mapper.order.ShopOrderMapper;
import com.niu.shop.mapper.order.ShopOrderRefundMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Component
@EventCallback("shop")
public class OrderServicePathListener extends CommonEventDefiner {
    private final String ADMIN_DETAIL_PATH = "/site/shop/order/detail?order_id=%d";
    private final String DETAIL_PATH = "/addon/shop/pages/order/detail?order_id=%d";
    private final String REFUND_APPLY_PATH = "/addon/shop/pages/refund/apply?order_goods_id=%s&order_id=%s";
    private final String REFUND_DETAIL_PATH = "/addon/shop/pages/refund/detail?order_refund_no=%s";

    @Resource
    private ShopOrderRefundMapper shopOrderRefundMapper;

    @Resource
    private ShopOrderMapper shopOrderMapper;

    @Override
    public CommonEventResult handleCallback(CommonEvent event) {
        if ("ThirdAddonOrderPath".equals(event.getEventName()) && ObjectUtil.equal("shop", event.getSource())) {
            CommonEventResult result = new CommonEventResult();
            result.setName("shop");
            Map<String, Object> data = (Map<String, Object>) event.getData();
            if (!data.containsKey("sourceId") || !data.containsKey("sourceOrderItemId")) {
                return result;
            }
            Integer orderId = (Integer) data.get("sourceId");
            Integer orderGoodsId = (Integer) data.get("sourceOrderItemId");
            LambdaQueryWrapper<ShopOrderRefund> refundWrapper = new LambdaQueryWrapper<>();
            refundWrapper.eq(ShopOrderRefund::getOrderId, orderId)
                    .notIn(ShopOrderRefund::getStatus,
                            Arrays.asList(OrderRefundStatusEnum.CLOSE.getStatus()
                                    , OrderRefundStatusEnum.SHOP_ACTIVE_CLOSE_REFUND.getStatus()));
            ShopOrderRefund refundInfo = shopOrderRefundMapper.selectOne(refundWrapper);
            String refundPath;
            if (refundInfo == null) {
                refundPath = String.format(REFUND_APPLY_PATH, orderGoodsId, orderId);
            } else {
                refundPath = String.format(REFUND_DETAIL_PATH, refundInfo.getOrderRefundNo());
            }

            // 检查订单是否允许退款
            ShopOrder orderInfo = shopOrderMapper.selectById(orderId);
            if (orderInfo != null && orderInfo.getIsEnableRefund() == 0) {
                refundPath = "";
            }

            String detailPath = String.format(DETAIL_PATH, orderId);
            String adminDetailPath = String.format(ADMIN_DETAIL_PATH, orderId);

            Map<String, Object> resultData = new HashMap<>();
            resultData.put("refund", Map.of("name", "商城-退款相关地址", "url", refundPath));
            resultData.put("detail", Map.of("name", "查看关联订单", "url", detailPath));
            resultData.put("adminDetail", Map.of("name", "查看关联订单", "url", adminDetailPath));
            result.setDataMap(resultData);
            return result;
        }
        return null;
    }
}
