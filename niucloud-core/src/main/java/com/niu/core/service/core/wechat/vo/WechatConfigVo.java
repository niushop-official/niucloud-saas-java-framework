package com.niu.core.service.core.wechat.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

/**
 * 微信配置视图
 */

@Data
public class WechatConfigVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String wechatName = ""; // 公众号名称
    private String wechatOriginal = ""; // 原始ID
    private String appId = ""; // APPID
    private String appSecret = ""; // APPSECRET
    private String qrCode = ""; // 公众号二维码
    private String token = ""; // 公众号token
    private String encodingAesKey = ""; // 加密密钥
    private String encryptionType = "not_encrypt"; // 加密方式
    private Integer isAuthorization = 0; // 权限设置

}