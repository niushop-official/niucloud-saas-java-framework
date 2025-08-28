package com.niu.core.service.admin.sys.vo;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

/**
 * SysSchedule列表视图
 */
@Data
@Slf4j
public class SysScheduleLogListVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;
    /**
     * 任务id
     */
    private Integer scheduleId;
    /**
     * 所属插件
     */
    private String addon;
    /**
     * 计划任务模板key
     */
    private String key;
    /**
     * 计划任务名称
     */
    private String name;
    /**
     * 执行时间
     */
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long executeTime;
    /**
     * 日志信息
     */
    private String executeResult;
    /**
     * 执行状态
     */
    private String status;
    /**
     * 类
     */
    @JsonProperty("class")
    private String className;
    /**
     * 任务
     */
    private String job;

    private String statusName;


    /**
     * 状态名称
     *
     * @return
     */
    public String getStatusName() {
        if ("success".equals(status)) {
            return this.statusName = "成功";
        }
        return this.statusName = "失败";
    }



}
