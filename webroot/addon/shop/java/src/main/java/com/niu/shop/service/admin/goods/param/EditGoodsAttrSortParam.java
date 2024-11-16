package com.niu.shop.service.admin.goods.param;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 商品参数参数
*/
@Data
public class EditGoodsAttrSortParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "attr_id参数缺失")
    private Integer attrId;

    @NotNull(message = "sort参数缺失")
    private Integer sort = 0;

}
