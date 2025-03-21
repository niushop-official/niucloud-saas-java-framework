package com.niu.shop.service.api.goods.param;

import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * 商品评价添加
 */
@Data
public class ShopGoodsEvaluateParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "缺少参数evaluate_array")
    private List<Evaluate> evaluateArray;

    @Data
    public static class Evaluate {

        private Integer goodsId;

        private String content;

        private String[] images;

        private Integer isAnonymous;

        private Integer orderGoodsId;

        private Integer orderId;

        private Integer scores;
    }
}
