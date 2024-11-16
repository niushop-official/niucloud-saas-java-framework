package com.niu.shop.service.admin.goods.param;
import lombok.Data;
import java.io.Serializable;
import javax.validation.constraints.*;

/**
 * 商品收藏记录参数
*/
@Data
public class ShopGoodsCollectParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "siteId参数缺失")
    private Integer siteId;

    @NotNull(message = "memberId参数缺失")
    private Integer memberId;

    @NotNull(message = "goodsId参数缺失")
    private Integer goodsId;

}
