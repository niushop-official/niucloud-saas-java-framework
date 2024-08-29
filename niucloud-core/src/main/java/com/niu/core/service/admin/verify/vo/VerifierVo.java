package com.niu.core.service.admin.verify.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

/**
 * 核销员实体
 */
@Data
public class VerifierVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;
    
    /** 站点id */
    private Integer siteId; 
    
    /** 会员id */
    private Integer memberId; 
    
    /** 用户id */
    private Integer uid; 

    /** 添加时间 */
    private Long createTime;
    
    /** 核销类型 */
    private String verifyType;

    private String memberNo;

    private String username;

    private String nickname;

    private String mobile;

    private String headimg;
}