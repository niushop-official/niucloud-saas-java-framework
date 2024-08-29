package com.niu.core.entity.site;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import lombok.Data;
import java.io.Serializable;

/**
 * 店铺分组（分组权限）实体
 */
@Data
public class SiteGroup implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 分组ID */
    @TableId(value="group_id", type= IdType.AUTO)
    private Integer groupId;

    /** 分组名称 */
    private String groupName;

    /** 分组介绍 */
    private String groupDesc;

    /** 应用 */
    @JsonSerialize(using = BeanJsonSerializer.StringToJsonSerializer.class)
    private String app;

    /** 插件 */
    @JsonSerialize(using = BeanJsonSerializer.StringToJsonSerializer.class)
    private String addon;

    /** 添加时间 */
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long createTime;

    /** 更新时间 */
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long updateTime;

}