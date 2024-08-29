package com.niu.core.service.admin.sys.param;
import lombok.Data;
import java.io.Serializable;
import javax.validation.constraints.*;

/**
 * 通知模型参数
*/
@Data
public class SysNoticeParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "siteId参数缺失")
    private Integer siteId;

    @NotNull(message = "key参数缺失")
    private String key;

    @NotNull(message = "smsContent参数缺失")
    private String smsContent;

    @NotNull(message = "isWechat参数缺失")
    private Integer isWechat;

    @NotNull(message = "isWeapp参数缺失")
    private Integer isWeapp;

    @NotNull(message = "isSms参数缺失")
    private Integer isSms;

    @NotNull(message = "wechatTemplateId参数缺失")
    private String wechatTemplateId;

    @NotNull(message = "weappTemplateId参数缺失")
    private String weappTemplateId;

    @NotNull(message = "smsId参数缺失")
    private String smsId;

    @NotNull(message = "wechatFirst参数缺失")
    private String wechatFirst;

    @NotNull(message = "wechatRemark参数缺失")
    private String wechatRemark;

}
