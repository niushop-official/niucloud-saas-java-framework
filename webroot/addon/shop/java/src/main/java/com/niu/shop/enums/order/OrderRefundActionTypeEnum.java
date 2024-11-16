package com.niu.shop.enums.order;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum OrderRefundActionTypeEnum {
    APPLY_ACTION("买家申请退款", "apply"),
    EDIT_APPLY_ACTION("买家修改退款", "edit_apply"),
    CLOSE_ACTION("关闭退款", "close"),
    DELIVERY_ACTION("买家退货", "delivery"),
    EDIT_DELIVERY_ACTION("买家修改退货", "edit_delivery"),
    AGREE_AUDIT_APPLY_ACTION("卖家同意退款", "agree_audit_apply"),
    REFUSE_AUDIT_APPLY_ACTION("卖家拒绝退款申请", "refuse_audit_apply"),
    AGREE_AUDIT_REFUND_GOODS_ACTION("卖家同意退货", "agree_audit_refund_goods"),
    REFUSE_AUDIT_REFUND_GOODS_ACTION("卖家拒绝退货", "refuse_audit_refund_goods"),
    FINISH_ACTION("退款完成", "finish"),
    ACTIVE_REFUND_ACTION("卖家主动退款", "active_refund");

    private final String name;

    private final String type;

    public static String getNameByType(String type){
        for (OrderRefundActionTypeEnum item: OrderRefundActionTypeEnum.values()) {
            if(item.getType().equals(type))
            {
                return item.getName();
            }
        }
        return "";
    }

    public static Map<String, String> getMap(){
        Map<String, String> map = new HashMap<>();
        for (OrderRefundActionTypeEnum item: OrderRefundActionTypeEnum.values()) {
            map.put(item.getType(), item.getName());
        }
        return map;
    }
}
