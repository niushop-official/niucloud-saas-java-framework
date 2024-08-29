package com.niu.core.entity.verify;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import lombok.Data;
import java.io.Serializable;

/**
 * 核销员实体
 */
@Data
public class Verifier implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value="id", type= IdType.AUTO)
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

}