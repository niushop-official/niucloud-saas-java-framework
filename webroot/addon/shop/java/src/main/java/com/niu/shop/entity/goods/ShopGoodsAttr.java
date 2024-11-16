package com.niu.shop.entity.goods;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.io.Serializable;

/**
 * 商品参数实体
 */
@Data
public class ShopGoodsAttr implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value="attr_id", type= IdType.AUTO)
    private Integer attrId; // 商品参数id
    private Integer siteId; // 站点id
    private String attrName; // 参数名称
    private String attrValueFormat; // 参数值，json格式
    private Integer sort; // 参数排序号

}