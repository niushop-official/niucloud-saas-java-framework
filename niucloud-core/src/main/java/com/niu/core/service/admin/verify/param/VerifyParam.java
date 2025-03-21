package com.niu.core.service.admin.verify.param;
import lombok.Data;
import java.io.Serializable;
import jakarta.validation.constraints.*;

/**
 * 核销记录参数
*/
@Data
public class VerifyParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "siteId参数缺失")
    private Integer siteId;

    @NotNull(message = "code参数缺失")
    private String code;

    @NotNull(message = "data参数缺失")
    private String data;

    @NotNull(message = "type参数缺失")
    private String type;

    @NotNull(message = "verifierMemberId参数缺失")
    private Integer verifierMemberId;

    @NotNull(message = "value参数缺失")
    private String value;

    @NotNull(message = "body参数缺失")
    private String body;

    @NotNull(message = "relateTag参数缺失")
    private String relateTag;

}
