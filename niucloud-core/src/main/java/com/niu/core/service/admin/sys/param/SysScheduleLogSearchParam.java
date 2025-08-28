package com.niu.core.service.admin.sys.param;

import lombok.Data;

import java.io.Serializable;

/**
 * 系统任务参数
 */
@Data
public class SysScheduleLogSearchParam implements Serializable {

    private static final long serialVersionUID = 1L;
    private Integer scheduleId;
    private String key;
    private String status;
    private String[] executeTime;
}
