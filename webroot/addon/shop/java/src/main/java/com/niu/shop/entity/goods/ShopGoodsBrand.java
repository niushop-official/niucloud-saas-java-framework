package com.niu.shop.entity.goods;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import lombok.Data;
import java.io.Serializable;

/**
 * 商品品牌实体
 */
@Data
public class ShopGoodsBrand implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value="brand_id", type= IdType.AUTO)
    private Integer brandId; // 品牌ID
    private Integer siteId; // 站点id
    private String brandName; // 品牌名称
    private String logo; // 品牌logo
    @TableField(value = "`desc`")
    private String desc; // 品牌介绍
    private Integer sort; // 排序
    private Long createTime; // 创建时间
    private Long updateTime; // 更新时间
    private Long deleteTime; // 删除时间
    private String colorJson; // 品牌颜色

}