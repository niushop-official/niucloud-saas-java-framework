package com.niu.shop.service.admin.goods.param;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 商品标签分组表参数
*/
@Data
public class LabelGroupParam implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 分组名称 */
    @NotNull(message = "缺少必须参数group_name")
    @NotEmpty(message = "分组名称不能为空")
    private String groupName;

    /** 排序 */
    private Integer sort = 0;


}
