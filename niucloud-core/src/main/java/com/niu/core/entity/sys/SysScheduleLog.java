package com.niu.core.entity.sys;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 系统任务实体
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class SysScheduleLog implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
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
    @TableField(value = "`key`")
    private String key;
    /**
     * 计划任务名称
     */
    @TableField(value = "`name`")
    private String name;
    /**
     * 执行时间
     */
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
    @TableField(value = "`class`")
    private String className;
    /**
     * 任务
     */
    private String job;

}