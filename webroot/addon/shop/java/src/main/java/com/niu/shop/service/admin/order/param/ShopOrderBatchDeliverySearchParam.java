package com.niu.shop.service.admin.order.param;
import lombok.Data;
import java.io.Serializable;

/**
 * 订单批量发货参数
 */
@Data
public class ShopOrderBatchDeliverySearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer siteId;

    private Integer mainId;

    private Integer status;

    private String type;

    private Integer totalNum;

    private Integer successNum;

    private Integer failNum;

    private String data;

    private String output;

    private String failOutput;

    private String failRemark;

}
