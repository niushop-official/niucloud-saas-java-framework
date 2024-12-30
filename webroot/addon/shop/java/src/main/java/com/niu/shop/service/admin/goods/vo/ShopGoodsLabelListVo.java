package com.niu.shop.service.admin.goods.vo;

import cn.hutool.json.JSONObject;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import lombok.Data;

import java.io.Serializable;

/**
 * ShopGoodsLabel列表视图
 */
@Data
public class ShopGoodsLabelListVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer labelId;  // 标签ID
    private Integer siteId;  // 站点id
    private String labelName;  // 标签名称
    private String styleType;
    private String colorJson;
    private String icon;
    private String memo;  // 标签说明
    private Integer sort;  // 排序
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long createTime; // 创建时间
    private Long updateTime; // 更新时间
    private Integer status;
    private Integer groupId;
    private String groupName;
    private Group group;

    @Data
    public static class Group {
        private Integer groupId;
        private String groupName;
    }

    public JSONObject getColorJson() {
        if (this.colorJson.isEmpty()) return new JSONObject();
        else return new JSONObject(this.colorJson);
    }
}
