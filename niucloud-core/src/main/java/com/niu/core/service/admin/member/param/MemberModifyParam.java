package com.niu.core.service.admin.member.param;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 会员参数
*/
@Data
public class MemberModifyParam<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer memberId;

    private String field;

    @NotNull(message = "value参数缺失")
    private Object value;

}
