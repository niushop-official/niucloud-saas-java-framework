package com.niu.shop.service.admin.goods.param;
import lombok.Data;
import java.io.Serializable;
import javax.validation.constraints.*;

/**
 * 商品标签参数
*/
@Data
public class ShopGoodsLabelParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "label_name参数缺失")
    @NotEmpty(message = "标签名称不能为空")
    private String labelName;

    @NotNull(message = "memo参数缺失")
    private String memo;

    @NotNull(message = "sort参数缺失")
    private String sort;

}
