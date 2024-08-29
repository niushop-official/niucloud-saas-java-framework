package com.niu.core.service.admin.sys.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户建站限制vo
 */
@Data
public class SysUserCreateSiteLimitVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Integer id;
    
    /**  权限id */
    private Integer groupId; 
    
    /** 用户id */
    private Integer uid; 
    
    /** 数量 */
    private Integer num; 
    
    /** 月 */
    private Integer month; 

}