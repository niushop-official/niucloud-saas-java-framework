package com.niu.shop.service.admin.goods.param;

import lombok.Data;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 商品参数参数
*/
@Data
public class EditGoodsAttrNameParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "attr_id参数缺失")
    private Integer attrId;

    @NotNull(message = "attr_name参数缺失")
    @NotEmpty(message = "参数名称不能为空")
    private String attrName;

}
