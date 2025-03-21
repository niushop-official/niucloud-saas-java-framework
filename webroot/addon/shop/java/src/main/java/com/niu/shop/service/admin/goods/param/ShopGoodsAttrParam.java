package com.niu.shop.service.admin.goods.param;
import lombok.Data;
import java.io.Serializable;
import jakarta.validation.constraints.*;

/**
 * 商品参数参数
*/
@Data
public class ShopGoodsAttrParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "attr_name参数缺失")
    @NotEmpty(message = "参数名称不能为空")
    private String attrName;

    @NotNull(message = "sort参数缺失")
    private Integer sort = 0;

}
