package com.niu.core.entity.verify;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.io.Serializable;

/**
 * 核销记录实体
 */
@Data
public class Verify implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value="id", type= IdType.AUTO)
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
    
    /** 核销内容 */
    private String value; 
    
    /** 描述 */
    private String body; 
    
    /** 业务标识 */
    private String relateTag;

    /** 是否后台核销 */
    private Integer isAdmin;
}