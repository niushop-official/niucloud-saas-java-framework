package com.niu.core.service.admin.wechat.vo;
import lombok.Data;

import java.io.Serializable;

/**
 * WechatMedia视图
 */
@Data
public class WechatMediaInfoVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;  // 
    private Integer siteId;  // 站点id
    private String type;  // 类型
    private String value;  // 值
    private String mediaId;  // 微信端返回的素材id

}
