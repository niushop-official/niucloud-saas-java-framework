package com.niu.shop.entity.goods;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import java.io.Serializable;
import lombok.Data;

/**
* 商品标签分组表实体
*/
@Data
public class ShopGoodsLabelGroup implements Serializable {

private static final long serialVersionUID = 1L;

    /** 分组ID */
    @TableId(value="group_id", type= IdType.AUTO)
    private Integer groupId;

    /** 站点id */
    private Integer siteId;

    /** 分组名称 */
    private String groupName;

    /** 排序 */
    private Integer sort;

    /** 创建时间 */
    private Long createTime;

    /** 更新时间 */
    private Long updateTime;

}