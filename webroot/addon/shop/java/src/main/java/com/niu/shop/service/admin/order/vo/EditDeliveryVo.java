package com.niu.shop.service.admin.order.vo;

import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class EditDeliveryVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer orderId;

    private String deliveryType;

    private Integer takeStoreId; // 自提点

    private String takerName; // 收货人

    private String takerMobile; // 收货人手机号

    private Integer takerProvince; // 收货省

    private Integer takerCity; // 收货市

    private Integer takerDistrict; // 收货区县

    private String takerAddress; // 收货地址

    private String takerFullAddress; // 收货详细地址

    private String takerLongitude; // 收货地址经度

    private String takerLatitude; // 收货详细纬度
}
