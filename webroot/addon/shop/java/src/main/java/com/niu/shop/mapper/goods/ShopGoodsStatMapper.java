package com.niu.shop.mapper.goods;

import com.github.yulichang.base.MPJBaseMapper;
import com.niu.shop.entity.goods.ShopGoodsStat;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品数据统计 Mapper
 */
@Mapper
public interface ShopGoodsStatMapper extends MPJBaseMapper<ShopGoodsStat> {
    void insertBatchSomeColumn(@Param("list") List<ShopGoodsStat> batchList);
}
