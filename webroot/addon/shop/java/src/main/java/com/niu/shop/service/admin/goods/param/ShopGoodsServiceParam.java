package com.niu.shop.service.admin.goods.param;
import lombok.Data;
import java.io.Serializable;
import jakarta.validation.constraints.*;

/**
 * 商品服务参数
*/
@Data
public class ShopGoodsServiceParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "serviceName参数缺失")
    @NotEmpty(message = "服务名称不能为空")
    private String serviceName;

    @NotNull(message = "image参数缺失")
    private String image;

    @NotNull(message = "desc参数缺失")
    private String desc;

}
