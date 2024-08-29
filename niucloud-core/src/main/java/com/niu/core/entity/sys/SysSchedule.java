package com.niu.core.entity.sys;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.io.Serializable;

/**
 * 系统任务实体
 */
@Data
public class SysSchedule implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键id */
    @TableId(value="id", type= IdType.AUTO)
    private Integer id; 
    
    /** 站点id */
    private Integer siteId;
    
    /** 所属插件 */
    private String addon;
    
    /** 计划任务模板key */
    @TableField(value = "`key`")
    private String key;
    
    /** 任务状态 是否启用 */
    private Integer status;
    
    /** 任务周期  json结构 */
    private String time;
    
    /** 执行次数 */
    private Integer count;
    
    /** 最后执行时间 */
    private Integer lastTime;
    
    /** 下次执行时间 */
    private Integer nextTime;
    
    /** 创建时间 */
    private Long createTime;
    
    /** 删除时间 */
    private Long deleteTime;
    
    /** 更新时间 */
    private Long updateTime;
    
    /** 排序 */
    private Integer sort;

}