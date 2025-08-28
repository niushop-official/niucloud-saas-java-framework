package com.niu.shop.enums.order;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum OrderRefundStatusEnum {
    BUYER_APPLY_WAIT_STORE("买家申请售后", 1),
    STORE_AGREE_REFUND_GOODS_APPLY_WAIT_BUYER("商家同意售后", 2),
    STORE_REFUSE_REFUND_GOODS_APPLY_WAIT_BUYER("商家拒绝售后", 3),
    BUYER_REFUND_GOODS_WAIT_STORE("买家已退货", 4),
    STORE_REFUSE_TAKE_REFUND_GOODS_WAIT_BUYER("商家拒绝收货", 5),
    STORE_AGREE_REFUND_WAIT_TRANSFER("同意售后申请", 6),
    STORE_REFUND_TRANSFERING("转账中", 7),
    FINISH("退款成功", 8),
    CLOSE("退款关闭", -1),
    SHOP_ACTIVE_CLOSE_REFUND("商家主动关闭售后", -3);

    private final String name;

    private final Integer status;

    public static String getNameByStatus(Integer status){
        for (OrderRefundStatusEnum item: OrderRefundStatusEnum.values()) {
            if(item.getStatus().equals(status))
            {
                return item.getName();
            }
        }
        return "";
    }

    public static Map<String, Object> getMapByStatus(Integer status){
        Map<String, Object> map = new HashMap<>();
        for (OrderRefundStatusEnum item: OrderRefundStatusEnum.values()) {
            if(item.getStatus().equals(status))
            {
                map.put("name", item.getName());
                map.put("status", item.getStatus());
            }
        }
        return map;
    }
}
