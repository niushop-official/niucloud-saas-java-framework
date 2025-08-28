package com.niu.core.service.admin.sys.param;
import lombok.Data;
import java.io.Serializable;

/**
 * 系统任务参数
 */
@Data
public class SysScheduleSearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer siteId;

    private String addon;

    private String key;

    private String status;

    private String time;

    private Integer count;

    private Integer lastTime;

    private Integer nextTime;

    private Integer sort;

}
