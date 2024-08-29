package com.niu.core.service.admin.sys.vo;
import lombok.Data;

import java.io.Serializable;

/**
 * SysNoticeSmsLog视图
 */
@Data
public class SysNoticeSmsLogInfoVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;  // id
    private Integer siteId;  // 
    private String mobile;  // 手机号码
    private String smsType;  // 发送关键字（注册、找回密码）
    private String key;  // 发送关键字（注册、找回密码）
    private String templateId;  // 
    private String content;  // 发送内容
    private String params;  // 数据参数
    private String status;  // 发送状态：sending-发送中；success-发送成功；fail-发送失败
    private String result;  // 短信结果
    private Integer sendTime;  // 发送时间

}
