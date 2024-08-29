package com.niu.core.service.admin.sys.param;
import lombok.Data;
import java.io.Serializable;

/**
 * 通知模型参数
 */
@Data
public class SysNoticeSearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer siteId;

    private String key;

    private String smsContent;

    private Integer isWechat;

    private Integer isWeapp;

    private Integer isSms;

    private String wechatTemplateId;

    private String weappTemplateId;

    private String smsId;

    private String wechatFirst;

    private String wechatRemark;

}
