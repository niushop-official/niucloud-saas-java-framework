package com.niu.shop.service.admin.goods.param;
import lombok.Data;
import java.io.Serializable;
import javax.validation.constraints.*;

/**
 * 商品分类参数
*/
@Data
public class ShopGoodsCategoryParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "category_name参数缺失")
    @NotEmpty(message = "分类名称不能为空")
    private String categoryName;

    @NotNull(message = "image参数缺失")
    private String image = "";

    @NotNull(message = "pid参数缺失")
    private Integer pid = 0;

    @NotNull(message = "isShow参数缺失")
    private Integer isShow = 0;

    @NotNull(message = "sort参数缺失")
    private Integer sort = 0;

}
