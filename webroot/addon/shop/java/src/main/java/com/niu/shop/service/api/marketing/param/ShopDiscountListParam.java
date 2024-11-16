package com.niu.shop.service.api.marketing.param;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import com.niu.shop.enums.active.ActiveGoodsTypeEnum;
import com.niu.shop.enums.active.ActiveStatusEnum;
import com.niu.shop.enums.active.ActiveTypeEnum;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 限时折扣参数
 */
@Data
public class ShopDiscountListParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer limit;
    private String activeName;  // 活动名称


}
