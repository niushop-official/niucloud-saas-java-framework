package com.niu.core.service.admin.verify.param;
import lombok.Data;
import java.io.Serializable;
import javax.validation.constraints.*;

/**
 * 核销员参数
*/
@Data
public class VerifierParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "memberId参数缺失")
    private Integer memberId;

    @NotNull(message = "verifyType参数缺失")
    private String[] verifyType;

}
