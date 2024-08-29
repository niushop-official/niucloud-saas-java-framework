package com.niu.core.service.admin.sys.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * SysNotice列表视图
 */
@Data
public class SysNoticeListVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;  // 
    private Integer siteId;  // 站点ID
    private String key;  // 标识
    private String smsContent;  // 短信配置参数
    private Integer isWechat;  // 公众号模板消息（0：关闭，1：开启）
    private Integer isWeapp;  // 小程序订阅消息（0：关闭，1：开启）
    private Integer isSms;  // 发送短信（0：关闭，1：开启）
    private String wechatTemplateId;  // 微信模版消息id
    private String weappTemplateId;  // 微信小程序订阅消息id
    private String smsId;  // 短信id（对应短信配置）
    private String createTime; // 添加时间
    private String wechatFirst;  // 微信头部
    private String wechatRemark;  // 微信说明

}
