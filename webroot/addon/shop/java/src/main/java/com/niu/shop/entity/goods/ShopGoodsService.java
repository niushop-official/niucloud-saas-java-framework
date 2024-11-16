package com.niu.shop.entity.goods;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.io.Serializable;

/**
 * 商品服务实体
 */
@Data
public class ShopGoodsService implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value="service_id", type= IdType.AUTO)
    private Integer serviceId; // 
    private Integer siteId; // 站点id
    private String serviceName; // 服务名称
    private String image; // 图片
    @TableField(value = "`desc`")
    private String desc; // 描述
    private Long createTime; // 创建时间
    private Long updateTime; // 更新时间

}