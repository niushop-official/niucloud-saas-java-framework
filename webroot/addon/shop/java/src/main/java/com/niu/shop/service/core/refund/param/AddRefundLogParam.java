package com.niu.shop.service.core.refund.param;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class AddRefundLogParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private String orderRefundNo; // 退款编号
    private String mainType; //
    private Integer mainId; // 操作人id
    private Integer status; // 退款状态
    private String type; //
    private String content; // 日志内容
}
