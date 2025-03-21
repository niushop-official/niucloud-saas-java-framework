package com.niu.core.service.admin.member.param;

import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 会员参数
*/
@Data
public class MemberConfigParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "prefix参数缺失")
    private String prefix;  // 会员编码前缀

    @NotNull(message = "length参数缺失")
    private Integer length;  // 会员编码长度

}
