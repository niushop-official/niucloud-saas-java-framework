package com.niu.shop.service.admin.goods.param;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * 商品标签参数
*/
@Data
public class EditGoodsLabelStatusParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "label_id参数缺失")
    private Integer labelId;

    @NotNull(message = "sort参数缺失")
    private Integer status;

}
