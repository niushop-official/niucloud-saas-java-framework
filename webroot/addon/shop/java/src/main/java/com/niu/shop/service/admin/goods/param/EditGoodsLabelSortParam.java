package com.niu.shop.service.admin.goods.param;

import io.swagger.models.auth.In;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 商品标签参数
*/
@Data
public class EditGoodsLabelSortParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "label_id参数缺失")
    private Integer labelId;

    @NotNull(message = "sort参数缺失")
    private Integer sort;

}
