package com.niu.shop.service.admin.marketing.param;

import cn.hutool.json.JSONArray;
import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * 限时折扣参数
*/
@Data
public class ShopDiscountConfigParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private JSONArray list;


}
