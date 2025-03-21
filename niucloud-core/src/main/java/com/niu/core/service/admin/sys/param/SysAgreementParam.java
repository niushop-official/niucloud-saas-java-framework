package com.niu.core.service.admin.sys.param;
import lombok.Data;
import java.io.Serializable;
import jakarta.validation.constraints.*;

/**
 * 协议参数
*/
@Data
public class SysAgreementParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "title参数缺失")
    private String title;

    @NotNull(message = "content参数缺失")
    private String content;

}
