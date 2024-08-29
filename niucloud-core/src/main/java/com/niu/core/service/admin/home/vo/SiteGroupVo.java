package com.niu.core.service.admin.home.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import lombok.Data;

import java.io.Serializable;

/**
 * 套餐信息
 */
@Data
public class SiteGroupVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**套餐id */
    private Integer groupId;

    /**套餐名称 */
    private String groupName;

    /**应用集合 */
    @JsonSerialize(using = BeanJsonSerializer.StringToJSONObjectSerializer.class)
    private String appName;

    /**插件集合 */
    @JsonSerialize(using = BeanJsonSerializer.StringToJSONObjectSerializer.class)
    private String addonName;

    /**应用 */
    @JsonSerialize(using = BeanJsonSerializer.StringToJSONObjectSerializer.class)
    private String app;

    /**插件 */
    @JsonSerialize(using = BeanJsonSerializer.StringToJSONObjectSerializer.class)
    private String addon;




}
