package com.niu.shop.service.admin.goods.param;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ShopGoodsEvaluateBatchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<String> evaluateIds;

}
