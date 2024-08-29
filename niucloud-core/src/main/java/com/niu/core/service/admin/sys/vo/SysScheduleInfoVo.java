package com.niu.core.service.admin.sys.vo;
import lombok.Data;

import java.io.Serializable;

/**
 * SysSchedule视图
 */
@Data
public class SysScheduleInfoVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;  // 
    private Integer siteId;  // 
    private String addon;  // 所属插件
    private String key;  // 计划任务模板key
    private Integer status;  // 任务状态 是否启用
    private String time;  // 任务周期  json结构
    private Integer count;  // 执行次数
    private Integer lastTime;  // 最后执行时间
    private Integer nextTime;  // 下次执行时间
    private Integer sort;  // 排序

}
