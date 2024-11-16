package com.niu.core.service.admin.sys.vo;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import com.niu.core.enums.poster.PosterTypeEnum;
import lombok.Data;

import java.io.Serializable;

/**
 * SysPoster列表视图
 */
@Data
public class SysPosterInitVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id = 0;  // 主键
    private String name = "";  // 海报名称
    private String type = "";  // 海报类型
    private String channel = "";  // 海报支持渠道
    @JsonSerialize(using = BeanJsonSerializer.StringToJSONObjectSerializer.class)
    private String value = "";  // 配置值json
    private Integer status = 1;  // 是否启用 1启用 2不启用
    private String addon = "";  // 所属插件
    private Integer isDefault = 0;  // 是否默认海报，1：是，0：否
    private JSONObject posterType;
    private JSONObject component;
}
