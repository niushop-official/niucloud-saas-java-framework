package com.niu.shop.service.admin.goods.vo;

import cn.hutool.json.JSONArray;
import lombok.Data;

import java.io.Serializable;

/**
 * 商品排行榜列表
 */
@Data
public class ShopGoodsRankListVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     *
     */
    private Integer rankId;

    /**
     * 站点id
     */
    private Integer siteId;

    /**
     * 榜单名称
     */
    private String name;

    /**
     * 排行周期 day=天，week=周，month=月, quarter=季度
     */
    private String rankType;

    /**
     * 来源类型 goods=指定商品，category=指定分类，brand=指定品牌, label=指定标签
     */
    private String goodsSource;

    /**
     * 排序规则 sale=按照销量，collect=按收藏数，evaluate=按评价数, access=按照浏览量
     */
    private String ruleType;

    /**
     * 商品信息
     */
    private JSONArray goodsJson;

    /**
     * 商品分类id
     */
    private JSONArray categoryIds;

    /**
     * 商品品牌id
     */
    private JSONArray brandIds;

    /**
     * 商品标签id，多个逗号隔开
     */
    private JSONArray labelIds;

    /**
     * 排序号
     */
    private Integer sort;

    /**
     * 显示状态（0不显示 1显示）
     */
    private Integer status;

    private String rankTypeName;
    private String goodsSourceName;
    private String ruleTypeName;
    private long showGoodsNum;
    private String createTime;
}
