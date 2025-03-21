package com.niu.shop.service.admin.goods.param;

import lombok.Data;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 商品参数参数
*/
@Data
public class EditGoodsAttrValueParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "attr_id参数缺失")
    private Integer attrId;

    @NotNull(message = "attr_value_format参数缺失")
    private String attrValueFormat = "";

}
