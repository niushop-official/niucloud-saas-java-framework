package com.niu.shop.service.core.order.param;

import lombok.Data;

import java.io.Serializable;

/**
 * 订单发货参数
 */
@Data
public class OrderDeliveryParam implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer orderId;

    private Integer siteId;

    private Integer[] orderGoodsIds;

    private String deliveryType;

    private String deliveryWay;

    private String remark;

    private Integer electronicSheetId;

    /**
     * 物流配送填写物流公司以及编码
     */
    private Integer expressCompanyId;

    private String expressNumber;

    /**
     * 同城配送填写配送人
     */
    private Integer localDeliverId;

    private String mainType;

    private Integer mainId;
}
