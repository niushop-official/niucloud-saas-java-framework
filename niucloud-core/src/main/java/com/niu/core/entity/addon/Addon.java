package com.niu.core.entity.addon;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import lombok.Data;

import java.io.Serializable;

/**
 * 插件实体
 */
@Data
public class Addon implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键 */
    @TableId(value="id", type= IdType.AUTO)
    private Integer id;

    /** 插件名称 */
    private String title;

    /** 插件图标 */
    private String icon;

    /** 插件标识 */
    @TableField(value = "`key`")
    private String key;

    /** 插件描述 */
    @TableField(value = "`desc`")
    private String desc;

    /** 状态 */
    @TableField(value = "`status`")
    private Integer status;

    /** 作者 */
    private String author;

    /** 版本号 */
    private String version;

    /** 创建时间 */
    private Long createTime;

    /** 安装时间 */
    private Long installTime;

    /** 更新时间 */
    private Long updateTime;

    /** 封面 */
    private String cover;

    /** 插件类型app，addon */
    private String type;

    /** 插件支持的应用空表示通用插件 */
    private String supportApp;

    /** 是否加星 */
    private Integer isStar;

    /** 编译端口 */
    private String compile;

}