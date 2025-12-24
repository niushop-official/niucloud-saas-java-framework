package com.niu.core.service.core.notice.vo;
import cn.hutool.json.JSONObject;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * SysNotice视图
 */
@Data
public class NoticeInfoVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String key;  // 标识
    private String addon;
    private Integer receiver_type;
    private String name;
    private String title;
    private Boolean async;
    private JSONObject variable;
    private List<String> support_type = new ArrayList<>();
    private String smsContent = "";  // 短信配置参数
    private Integer isWechat = 0;  // 公众号模板消息（0：关闭，1：开启）
    private Integer isWeapp = 0;  // 小程序订阅消息（0：关闭，1：开启）
    private Integer isSms = 0;  // 发送短信（0：关闭，1：开启）
    private String wechatTemplateId = "";  // 微信模版消息id
    private String weappTemplateId = "";  // 微信小程序订阅消息id
    private String smsId = "";  // 短信id（对应短信配置）
    private String wechatFirst = "";  // 微信头部
    private String wechatRemark = "";  // 微信说明
    private JSONObject sms;
    private JSONObject wechat;
    private JSONObject weapp;
    private String auditStatus = "wait";
    private String sign = "";

}
