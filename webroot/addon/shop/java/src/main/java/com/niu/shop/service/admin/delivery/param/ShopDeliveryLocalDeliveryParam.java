package com.niu.shop.service.admin.delivery.param;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.util.Map;

import jakarta.validation.constraints.*;

/**
 * 【请填写功能名称】参数
*/
@Data
public class ShopDeliveryLocalDeliveryParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "fee_type参数缺失")
    private String feeType;

    private BigDecimal baseDist = new BigDecimal(0);

    private BigDecimal basePrice = new BigDecimal(0);

    private BigDecimal gradDist = new BigDecimal(0);

    private BigDecimal gradPrice = new BigDecimal(0);

    private BigDecimal weightStart = new BigDecimal(0);

    private BigDecimal weightUnit = new BigDecimal(0);

    private BigDecimal weightPrice = new BigDecimal(0);

    @NotNull(message = "delivery_type参数缺失")
    private JSONArray deliveryType;

    @NotNull(message = "area参数缺失")
    private JSONArray area;

    @NotNull(message = "center参数缺失")
    private JSONObject center;

    private Integer timeIsOpen = 0;

    private Integer timeType = 0;

    private String timeWeek;

    private Integer timeInterval = 30;

    private Integer advanceDay = 0;

    private Integer mostDay = 7;

    private Long startTime;

    private Long endTime;

    private JSONArray deliveryTime;

    private Map<String, Object> thirdPartyConfig;
}
