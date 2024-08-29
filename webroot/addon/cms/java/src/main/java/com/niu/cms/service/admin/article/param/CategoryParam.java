package com.niu.cms.service.admin.article.param;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 会员标签参数
*/
@Data
public class CategoryParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "name参数缺失")
    @NotBlank(message = "分类名称不能为空")
    private String name;

    private Integer isShow = 1;

    private Integer sort = 0;

}
