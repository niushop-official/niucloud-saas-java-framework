package com.niu.core.service.admin.sys.param;
import lombok.Data;
import java.io.Serializable;
import jakarta.validation.constraints.*;

/**
 * 短信发送参数
*/
@Data
public class SysNoticeSmsLogParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "siteId参数缺失")
    private Integer siteId;

    @NotNull(message = "mobile参数缺失")
    private String mobile;

    @NotNull(message = "smsType参数缺失")
    private String smsType;

    @NotNull(message = "key参数缺失")
    private String key;

    @NotNull(message = "templateId参数缺失")
    private String templateId;

    @NotNull(message = "content参数缺失")
    private String content;

    @NotNull(message = "params参数缺失")
    private String params;

    @NotNull(message = "status参数缺失")
    private String status;

    @NotNull(message = "result参数缺失")
    private String result;

    @NotNull(message = "sendTime参数缺失")
    private Integer sendTime;

}
