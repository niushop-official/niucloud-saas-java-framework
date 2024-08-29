package com.niu.core.service.admin.wechat.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * WechatMedia列表视图
 */
@Data
public class WechatMediaListVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;  // 
    private Integer siteId;  // 站点id
    private String type;  // 类型
    private String value;  // 值
    private String createTime; // 创建时间
    private String updateTime; // 修改时间
    private String mediaId;  // 微信端返回的素材id

}
