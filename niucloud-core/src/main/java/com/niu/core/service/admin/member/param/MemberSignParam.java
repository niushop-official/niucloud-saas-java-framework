package com.niu.core.service.admin.member.param;
import lombok.Data;
import java.io.Serializable;
import jakarta.validation.constraints.*;

/**
 * 会员签到参数
*/
@Data
public class MemberSignParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "signId参数缺失")
    private Integer signId;

    @NotNull(message = "siteId参数缺失")
    private Integer siteId;

    @NotNull(message = "memberId参数缺失")
    private Integer memberId;

    @NotNull(message = "days参数缺失")
    private Integer days;

    @NotNull(message = "dayAward参数缺失")
    private String dayAward;

    @NotNull(message = "continueAward参数缺失")
    private String continueAward;

    @NotNull(message = "continueTag参数缺失")
    private String continueTag;

    @NotNull(message = "startTime参数缺失")
    private Long startTime;

    @NotNull(message = "isSign参数缺失")
    private Integer isSign;

}
