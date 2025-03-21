package com.niu.shop.service.admin.goods.param;

import lombok.Data;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 商品品牌参数
*/
@Data
public class EditGoodsBrandSortParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "brand_id参数缺失")
    private Integer brandId;

    @NotNull(message = "sort参数缺失")
    private Integer sort;

}
