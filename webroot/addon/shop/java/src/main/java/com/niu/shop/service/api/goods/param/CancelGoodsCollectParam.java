package com.niu.shop.service.api.goods.param;

import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class CancelGoodsCollectParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "goods_ids参数缺失")
    private Integer[] goodsIds;
}
