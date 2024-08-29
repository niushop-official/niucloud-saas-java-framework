package com.niu.core.entity.sys;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

/**
 * 角色实体
 */
@Data
public class SysRole implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 角色id */
    @TableId(value="role_id", type= IdType.AUTO)
    private Integer roleId;
    
    /** 站点id */
    private Integer siteId; 
    
    /** 角色名称 */
    private String roleName; 
    
    /** 角色权限(menus_id) */
    private String rules; 
    
    /** 状态 */
    private Integer status; 
    
    /** 添加时间 */
    private Long createTime; 
    
    /** 最后修改时间 */
    private Long updateTime; 

}