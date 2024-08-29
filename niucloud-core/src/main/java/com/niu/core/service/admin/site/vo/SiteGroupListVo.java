package com.niu.core.service.admin.site.vo;

import cn.hutool.json.JSONArray;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * SiteGroup列表视图
 */
@Data
public class SiteGroupListVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**分组ID */
    private Integer groupId;

    /**分组名称 */
    private String groupName;

    /**分组介绍 */
    private String groupDesc;

    /**应用 */
    private JSONArray app;

    /**应用名称 */
    private List<String> appName;

    /**插件 */
    private JSONArray addon;

    /**插件名称 */
    private List<String> addonName;

    /**创建时间 */
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long createTime;

    /**更新时间 */
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long updateTime;

    /**站点数量 */
    private Integer siteNum;

}
