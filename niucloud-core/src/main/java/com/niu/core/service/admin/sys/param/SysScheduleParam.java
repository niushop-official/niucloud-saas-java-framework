package com.niu.core.service.admin.sys.param;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;
import jakarta.validation.constraints.*;

/**
 * 系统任务参数
 */
@Data
public class SysScheduleParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    @NotNull(message = "key参数缺失")
    private String key;

    @NotNull(message = "status参数缺失")
    private Integer status;

    @NotNull(message = "time参数缺失")
    private Map<String, Object> time;

}
