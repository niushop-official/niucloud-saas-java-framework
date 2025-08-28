package com.niu.shop.service.admin.marketing.param;

import cn.hutool.json.JSONArray;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 限时折扣参数
*/
@Data
public class ShopDiscountCheckGoodsParam implements Serializable {

    private static final long serialVersionUID = 1L;


    private Integer DiscountId;

    @NotNull(message = "goodsIds参数缺失")
    private List<Integer> goodsIds;

    @NotEmpty(message = "startTime参数缺失")
    private String startTime;

    @NotEmpty(message = "endTime参数缺失")
    private String endTime;



}
