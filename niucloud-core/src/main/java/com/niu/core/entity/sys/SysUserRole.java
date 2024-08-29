package com.niu.core.entity.sys;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户权限实体
 */
@Data
public class SysUserRole implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @TableId(value="id", type= IdType.AUTO)
    private Integer id; 
    
    /** 用户id */
    private Integer uid; 
    
    /** 站点id */
    private Integer siteId; 
    
    /** 角色id */
    private String roleIds; 
    
    /** 添加时间 */
    private Long createTime; 
    
    /** 是否是超级管理员 */
    private Integer isAdmin; 
    
    /** 状态 */
    private Integer status; 

}