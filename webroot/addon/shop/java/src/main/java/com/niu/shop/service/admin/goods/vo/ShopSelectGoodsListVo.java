package com.niu.shop.service.admin.goods.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import com.niu.shop.entity.goods.ShopGoodsSku;
import com.niu.shop.enums.goods.GoodsTypeEnum;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * ShopGoods列表视图
 */
@Data
public class ShopSelectGoodsListVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private long currentPage;

    // 每页大小
    private long perPage;

    // 总记录数
    private long total;

    // 记录结果
    private List<ListVo> data;

    private List<Integer> verifyGoodsIds;

    private List<Integer> verifySkuIds;

    @Data
    public static class ListVo {
        private Integer goodsId;  // 商品id
        private Integer siteId;  // 站点id
        private String goodsName;  // 商品名称
        private String goodsType;  // 商品类型
        private String goodsCover;  // 商品封面
        private String goodsImage;
        private String goodsCoverThumbSmall;
        private String goodsCoverThumbMid;
        private Integer saleNum;  // 销量
        private Integer status;  // 商品状态（1.正常0下架）
        private Integer stock;
        private Integer sort;  // 排序
        private String goodsTypeName;
        private String goodsEditPath;
        @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
        private Long createTime; // 创建时间
        private GoodsSku goodsSku;
        private List<ShopGoodsSku> skuList;

        public String getGoodsTypeName() {
            return GoodsTypeEnum.getNameByType(this.goodsType);
        }

        public String getGoodsEditPath() {
            Map<String, Map<String, Object>> map = GoodsTypeEnum.getMap();
            return map.get(this.goodsType).get("path").toString();
        }
    }

    @Data
    public static class GoodsSku {
        private Integer goodsId;
        private Integer skuId;
        private String skuName;
        private String skuSpecFormat;
        private BigDecimal price;
        private Integer stock;
    }
}
