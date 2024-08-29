package com.niu.core.entity.sys;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.io.Serializable;

/**
 * 通知模型实体
 */
@Data
public class SysNotice implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value="id", type= IdType.AUTO)
    private Integer id;

    /** 站点ID */
    private Integer siteId;

    /** 标识 */
    @TableField(value = "`key`")
    private String key;

    /** 短信配置参数 */
    private String smsContent;

    /** 公众号模板消息（0：关闭，1：开启） */
    private Integer isWechat;

    /** 小程序订阅消息（0：关闭，1：开启） */
    private Integer isWeapp;

    /** 发送短信（0：关闭，1：开启） */
    private Integer isSms;

    /** 微信模版消息id */
    private String wechatTemplateId;

    /** 微信小程序订阅消息id */
    private String weappTemplateId;

    /** 短信id（对应短信配置） */
    private String smsId;

    /** 添加时间 */
    private Long createTime;

    /** 微信头部 */
    private String wechatFirst;

    /** 微信说明 */
    private String wechatRemark;

}