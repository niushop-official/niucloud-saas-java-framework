package com.niu.shop.service.admin.goods.param;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 商品评价参数
*/
@Data
public class ShopGoodsEvaluateReplyParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer evaluateId;

    private String explainFirst;

}
