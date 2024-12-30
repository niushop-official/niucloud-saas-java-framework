package com.niu.shop.service.admin.goods.param;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 商品排行榜参数
 */
@Data
public class ShopGoodsRankParam implements Serializable {

    @Serial
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
    private List<GoodsJson> goodsJson;

    /**
     * 商品分类id
     */
    private List<Integer> categoryIds;

    /**
     * 商品品牌id
     */
    private List<Integer> brandIds;

    /**
     * 商品标签id，多个逗号隔开
     */
    private List<Integer> labelIds;

    /**
     * 排序号
     */
    private String sort;

    /**
     * 显示状态（0不显示 1显示）
     */
    private Integer status;

    @Data
    public static class GoodsJson {
        private Integer goodsId;
        private Integer sort;
    }

}
