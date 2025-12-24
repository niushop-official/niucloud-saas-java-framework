package com.niu.shop.service.core.delivery.vo;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * ShopGoodsAttr视图
 */
@Data
public class DeliverySearchConfigVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer interfaceType = 1;

    private String kdniaoId = "";

    private String kdniaoAppKey = "";

    private Integer kdniaoIsPay = 1;

    private String kd100AppKey = "";

    private String kd100Customer = "";
}
