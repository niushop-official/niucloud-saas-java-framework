package com.niu.core.service.admin.sys.param;
import lombok.Data;
import java.io.Serializable;

/**
 * 短信发送参数
 */
@Data
public class SysNoticeSmsLogSearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer siteId;

    private String mobile;

    private String smsType;

    private String key;

    private String templateId;

    private String content;

    private String params;

    private String status;

    private String result;

    private String[] createTime;

}
