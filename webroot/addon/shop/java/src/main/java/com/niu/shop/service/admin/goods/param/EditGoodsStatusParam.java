package com.niu.shop.service.admin.goods.param;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 商品参数
*/
@Data
public class EditGoodsStatusParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "goods_ids参数缺失")
    private Object goodsIds;

    @NotNull(message = "status参数缺失")
    private Integer status;
}
