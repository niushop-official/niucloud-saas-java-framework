package com.niu.shop.entity.goods;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.io.Serializable;

/**
 * 商品分类实体
 */
@Data
public class ShopGoodsCategory implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value="category_id", type= IdType.AUTO)
    private Integer categoryId; // 商品分类id
    private Integer siteId; // 站点id
    private String categoryName; // 分类名称
    private String image; // 分类图片
    private Integer level; // 层级
    private Integer pid; // 上级分类id
    private String categoryFullName; // 组装分类名称
    private Integer isShow; // 是否显示（1：显示，0：不显示）
    private Integer sort; // 排序号
    private Long createTime; // 创建时间
    private Long updateTime; // 修改时间

}