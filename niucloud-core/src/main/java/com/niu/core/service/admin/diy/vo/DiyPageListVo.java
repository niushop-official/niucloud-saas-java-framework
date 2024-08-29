package com.niu.core.service.admin.diy.vo;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import com.niu.core.enums.diy.TemplateEnum;
import lombok.Data;

import java.io.Serializable;

/**
 * DiyPageVo
 */
@Data
public class DiyPageListVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;  // 
    private Integer siteId;  // 站点id
    private String pageTitle;  // 页面名称（用于后台展示）
    private String title;  // 页面标题（用于前台展示）
    private String name;  // 页面标识
    private String type;  // 页面模板
    private String template;  // 模板名称
    private String mode;  // 页面展示模式，diy：自定义，fixed：固定
    private String value;  // 页面数据，json格式
    private Integer isDefault;  // 是否默认页面，1：是，0：否
    private Integer isChange;  // 数据是否发生过变化，1：变化了，2：没有
    private String share;  // 分享内容
    private Integer visitCount;  // 访问量
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long createTime; // 创建时间
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long updateTime; // 更新时间
    private String typeName;

    public String getTypeName() {
        JSONObject template = TemplateEnum.getTemplate();
        return ObjectUtil.defaultIfNull(template.getByPath(this.type + ".title", String.class), "");
    }
}
