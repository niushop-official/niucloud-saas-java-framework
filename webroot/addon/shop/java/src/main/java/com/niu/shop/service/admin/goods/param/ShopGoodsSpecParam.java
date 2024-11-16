package com.niu.shop.service.admin.goods.param;
import lombok.Data;
import java.io.Serializable;
import javax.validation.constraints.*;

/**
 * 商品规格项/值参数
*/
@Data
public class ShopGoodsSpecParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "specId参数缺失")
    private Integer specId;

    @NotNull(message = "siteId参数缺失")
    private Integer siteId;

    @NotNull(message = "goodsId参数缺失")
    private Integer goodsId;

    @NotNull(message = "specName参数缺失")
    private String specName;

    @NotNull(message = "specValues参数缺失")
    private String specValues;

}
