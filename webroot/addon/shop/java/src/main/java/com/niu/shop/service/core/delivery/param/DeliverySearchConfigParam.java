package com.niu.shop.service.core.delivery.param;

import cn.hutool.json.JSONArray;
import lombok.Data;

import java.io.Serializable;

/**
 * 配送方式设置参数
*/
@Data
public class DeliverySearchConfigParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer interfaceType = 1;

    private String kdniaoId = "";

    private String kdniaoAppKey = "";

    private Integer kdniaoIsPay = 1;

    private String kd100AppKey = "";

    private Integer kd100Customer = 0;

}
