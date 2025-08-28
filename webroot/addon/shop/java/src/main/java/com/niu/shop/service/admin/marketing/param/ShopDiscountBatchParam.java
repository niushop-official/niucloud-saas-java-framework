package com.niu.shop.service.admin.marketing.param;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 店铺营销活动参数
*/
@Data
public class ShopDiscountBatchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<Integer> ids;

}
