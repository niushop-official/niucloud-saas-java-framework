package com.niu.core.entity.sys;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

/**
 * 管理员操作记录实体
 */
@Data
public class SysUserLog implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 管理员操作记录ID */
    @TableId(value="id", type= IdType.AUTO)
    private Integer id;
    
    /** 登录IP */
    private String ip; 
    
    /** 站点id */
    private Integer siteId; 
    
    /** 管理员id */
    private Integer uid; 
    
    /** 管理员姓名 */
    private String username; 
    
    /** 链接 */
    private String url; 
    
    /** 参数 */
    private String params; 
    
    /** 请求方式 */
    private String type; 
    
    /** 操作时间 */
    private Long createTime; 

}