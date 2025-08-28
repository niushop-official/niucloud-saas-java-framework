package com.niu.core.service.admin.sys.param;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 系统任务参数
 */
@Data
public class SysScheduleLogDelParam implements Serializable {

    private Integer[] ids;


    private Integer scheduleId;
}
