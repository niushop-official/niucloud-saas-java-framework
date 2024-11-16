package com.niu.shop.service.core.order.param;

import lombok.Data;

import java.io.Serializable;

@Data
public class DeliverySearchParam implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 快递公司编码 */
    private String expressNo;

    /** 快递单号 */
    private String logisticNo;

    /** 寄件人or收件人 手机号后四位 */
    private String mobile;
}
