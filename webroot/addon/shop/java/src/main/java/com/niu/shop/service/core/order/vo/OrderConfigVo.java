package com.niu.shop.service.core.order.vo;

import com.niu.shop.entity.order.config.*;
import lombok.Data;

import java.io.Serializable;

/**
 * 订单配置数据
 */
@Data
public class OrderConfigVo implements Serializable {

    private static final long serialVersionUID = 1L;
    private ShopOrderClose closeOrderInfo; //订单关闭配置
    private ShopOrderRefund refund;  //订单退款配置
    private ShopOrderEvaluate evaluate; //订单评价配置
    private ShopOrderInvoice invoice;  //订单发票配置
    private ShopOrderFinish confirm;  //订单完成配置


}
