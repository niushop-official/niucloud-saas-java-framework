package com.niu.shop.entity.goods;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.io.Serializable;

/**
 * 商品标签实体
 */
@Data
public class ShopGoodsLabel implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value="label_id", type= IdType.AUTO)
    private Integer labelId; // 标签ID
    private Integer siteId; // 站点id
    private String labelName; // 标签名称
    private String memo; // 标签说明
    private Integer sort; // 排序
    private Long createTime; // 创建时间
    private Long updateTime; // 更新时间

}