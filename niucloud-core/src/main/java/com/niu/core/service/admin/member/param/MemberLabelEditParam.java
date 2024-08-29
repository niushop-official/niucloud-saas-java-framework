package com.niu.core.service.admin.member.param;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 会员标签参数
*/
@Data
public class MemberLabelEditParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer labelId;

    private Integer siteId;

    @NotNull(message = "labelName参数缺失")
    @NotBlank(message = "标签名称不能为空")
    private String labelName;

    private String memo = "";

    private Integer sort = 0;

}
