package com.niu.core.service.admin.addon.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import com.niu.core.common.utils.date.DateUtils;
import lombok.Data;

import java.io.Serializable;

/**
 * AddonVo
 */
@Data
public class AddonListVo implements Serializable {

    private static final long serialVersionUID = 1L;


    /** 主键 */
    private Integer id;

    /** 插件名称 */
    private String title;

    /** 插件图标 */
    private String icon;

    /** 插件标识 */
    private String key;

    /** 插件描述 */
    private String desc;

    /** 状态 */
    private Integer status;

    /** 作者 */
    private String author;

    /** 版本号 */
    private String version;

    /** 创建时间 */
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long createTime;

    /** 安装时间 */
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long installTime;

    /** 更新时间 */
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
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

    public String getTypeName() {
        if (type != null) {
            if (type.equals("app")) {
                return "应用";
            } else {
                return "插件";
            }
        }
        return "";
    }

}
