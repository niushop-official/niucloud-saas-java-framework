package com.niu.core.service.admin.sys.vo;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
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
public class SysScheduleListVo implements Serializable {

    private static final long serialVersionUID = 1L;

    // 主键id
    private Integer id;

    // 站点id
    private Integer siteId;

    // 所属插件
    private String addon;

    // 计划任务模板key
    private String key;

    // 任务状态 是否启用
    private Integer status;

    // 任务周期  json结构
    private Object time;

    // 执行次数
    private Integer count;

    // 最后执行时间
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long lastTime;

    // 下次执行时间
    private Integer nextTime;

    // 创建时间
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long createTime;

    // 删除时间
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long deleteTime;

    // 更新时间
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long updateTime;

    // 排序
    private Integer sort;

    // 名称
    private String name;

    // crontab内容
    private String crontabContent;

    // 描述
    private String desc;

    // 类
    private String clazz;

    // 过程
    private String function;


    /**
     * 状态名称
     *
     * @return
     */
    public String getStatusName() {
        if (status == 1) {
            return "启用";
        }
        return "关闭";
    }

    /**
     * @param timeString
     */
    public void setTimeObject(String timeString) {
        try {
            if (StrUtil.isNotEmpty(timeString)) {
                this.time = JSONUtil.parseObj(timeString);
            }
        } catch (Exception e) {
            log.error("JSON解析time字段错误：", e.getMessage());
        }
    }

}
