package com.niu.core.service.admin.wechat.param;
import lombok.Data;
import java.io.Serializable;
import jakarta.validation.constraints.*;

/**
 * 微信素材参数
*/
@Data
public class WechatMediaParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "siteId参数缺失")
    private Integer siteId;

    @NotNull(message = "type参数缺失")
    private String type;

    @NotNull(message = "value参数缺失")
    private String value;

    @NotNull(message = "mediaId参数缺失")
    private String mediaId;

}
