package com.niu.shop.service.admin.marketing.param;

import cn.hutool.json.JSONArray;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 优惠券参数
*/
@Data
public class ShopCouponDelParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "请选择要删除的优惠券")
    private List<Integer> ids;

}
