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
    private Integer labelId;

    /** 站点id */
    private Integer siteId;

    /** 标签名称 */
    private String labelName;

    /** 标签分组id */
    private Integer groupId;

    /** 效果设置，diy：自定义，icon：图片 */
    private String styleType;

    /** 自定义颜色（文字、背景、边框），json格式 */
    private String colorJson;

    /** 图标 */
    private String icon;

    /** 状态，1：启用，0；关闭 */
    private Integer status;

    /** 标签说明 */
    private String memo;

    /** 排序 */
    private Integer sort;

    /** 创建时间 */
    private Long createTime;

    /** 更新时间 */
    private Long updateTime;

}