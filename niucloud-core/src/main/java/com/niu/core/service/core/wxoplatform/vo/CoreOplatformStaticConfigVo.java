package com.niu.core.service.core.wxoplatform.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 查询微信需要的静态信息
 */
@Data
public class CoreOplatformStaticConfigVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 授权事件接收配置 */
    private String authServeUrl;
    /** 消息与事件接收配置 */
    private String messageServeUrl;
    /** 授权发起页域名 */
    private String authLaunchDomain;
    /** 公众号开发域名 */
    private String wechatAuthDomain;
    /** 上传ip */
    private String uploadIp;

}
