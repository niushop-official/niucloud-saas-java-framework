package com.niu.core.service.admin.sys.param;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 用户建站限制vo
 */
@Data
public class SysUserCreateSiteLimitEditParam implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Integer id;
    
    /** 数量 */
    @NotNull(message = "num参数缺失")
    private Integer num; 
    
    /** 月 */
    @NotNull(message = "month参数缺失")
    private Integer month; 

}