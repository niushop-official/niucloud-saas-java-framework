package com.niu.shop.service.admin.marketing.param;

import cn.hutool.json.JSONArray;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 限时折扣参数
*/
@Data
public class ShopDiscountParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer activeId;

    @NotNull(message = "限时折扣名称参数缺失")
    private String activeName;

    @NotNull(message = "限时折扣标题参数缺失")
    private String activeDesc;

    private List<String> discountTime;

    private List<Integer> goodsId;

    @NotNull(message = "startTime参数缺失")
    private String startTime;

    @NotNull(message = "endTime参数缺失")
    private String endTime;

    @NotNull(message = "商品数据参数缺失")
    private String goodsData;

    private JSONArray goodsList;


}
