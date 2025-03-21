package com.niu.core.service.admin.member.param;
import lombok.Data;
import java.io.Serializable;
import jakarta.validation.constraints.*;

/**
 * 会员标签参数
*/
@Data
public class MemberLabelParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "labelId参数缺失")
    private Integer labelId;

    @NotNull(message = "siteId参数缺失")
    private Integer siteId;

    @NotNull(message = "labelName参数缺失")
    private String labelName;

    @NotNull(message = "memo参数缺失")
    private String memo;

    @NotNull(message = "sort参数缺失")
    private Integer sort;

}
