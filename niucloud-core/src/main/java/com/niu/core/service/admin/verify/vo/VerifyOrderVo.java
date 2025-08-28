package com.niu.core.service.admin.verify.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 核销记录实体
 */
@Data
public class VerifyOrderVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;
    
    /** 站点id */
    private Integer siteId; 
    
    /** 核销码 */
    private String code; 
    
    /** 核销参数 */
    private String data; 
    
    /** 核销类型 */
    private String type; 
    
    /** 核销时间 */
    private Long createTime; 
    
    /** 核销会员id */
    private Integer verifierMemberId;

    private Integer memberId;
    
    /** 核销内容 */
    private String value; 
    
    /** 描述 */
    private String body; 
    
    /** 业务标识 */
    private String relateTag;

    private String memberNo;

    private String username;

    private String nickname;

    private String mobile;

    private String headimg;
}