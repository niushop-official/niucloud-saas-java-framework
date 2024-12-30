package com.niu.shop.service.admin.goods.param;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 商品标签分组表参数
 */
@Data
public class LabelGroupModifySortParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "缺少必须参数group_id")
    private String groupId;

    private Integer sort = 0;

}
