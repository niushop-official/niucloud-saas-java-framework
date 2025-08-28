package com.niu.core.service.admin.diy.vo;

import cn.hutool.json.JSONArray;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import lombok.Data;

import java.io.Serializable;

/**
 * diy主题色
 */
@Data
public class DiyThemeInfoVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    /**
     * 站点id
     */
    private Integer siteId;

    /**
     * 颜色名称
     */
    private String title;

    /**
     * 插件类型app，addon
     */
    private String type;

    /**
     * 所属应用，app：系统，shop：商城、o2o：上门服务
     */
    private String addon;

    /**
     * 模式，default：默认【跟随系统】，diy：自定义配色
     */
    private String mode;

    /**
     * 配色类型，default：默认，diy：自定义
     */
    private String themeType;

    /**
     * 当前色调的默认值
     */
    @JsonSerialize(using = BeanJsonSerializer.StringToJsonSerializer.class)
    private String defaultTheme;

    /**
     * 当前色调
     */
    @JsonSerialize(using = BeanJsonSerializer.StringToJsonSerializer.class)
    private String theme;

    /**
     * 新增颜色集合
     */
    @JsonSerialize(using = BeanJsonSerializer.StringToJsonSerializer.class)
    private String newTheme;

    /**
     * 已选色调，0：否，1.是
     */
    private Integer isSelected;

    private JSONArray themeField;


}