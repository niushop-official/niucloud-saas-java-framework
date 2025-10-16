package com.niu.shop.service.admin.goods.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.niu.core.common.config.GlobalConfig;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.utils.CollectUtils;
import com.niu.core.common.utils.CommonUtils;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.StringUtils;
import com.niu.shop.entity.goods.ShopGoods;
import com.niu.shop.entity.goods.ShopGoodsSku;
import com.niu.shop.entity.goods.ShopGoodsSpec;
import com.niu.shop.enums.goods.GoodsTypeEnum;
import com.niu.shop.mapper.goods.ShopGoodsMapper;
import com.niu.shop.mapper.goods.ShopGoodsSkuMapper;
import com.niu.shop.mapper.goods.ShopGoodsSpecMapper;
import com.niu.shop.service.admin.goods.IGoodsCategoryService;
import com.niu.shop.service.admin.goods.IShopGoodsBrandService;
import com.niu.shop.service.admin.goods.IShopGoodsLabelService;
import com.niu.shop.service.admin.goods.param.*;
import com.niu.shop.service.admin.goods.vo.*;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 输出商品服务层
 */
@Service
public class OfferGoodsServiceImpl {

    @Resource
    ShopGoodsMapper shopGoodsMapper;

    @Resource
    ShopGoodsSkuMapper shopGoodsSkuMapper;

    @Resource
    IShopGoodsLabelService labelService;

    @Resource
    IShopGoodsBrandService brandService;

    @Resource
    IGoodsCategoryService categoryService;

    @Resource
    private ShopGoodsSpecMapper shopGoodsSpecMapper;


    public Map<String, Object> getWhereInit() {
        return getLists();
    }

    public Map<String, Object> getLists() {
        Map<String, Object> result = new HashMap<>();
        ShopGoodsLabelSearchParam shopGoodsLabel = new ShopGoodsLabelSearchParam();
        List<ShopGoodsLabelListVo> shopGoodsLabelListVoList = labelService.list(shopGoodsLabel);
        result.put("label_list", shopGoodsLabelListVoList);
        result.put("brand_list", brandService.list(new ShopGoodsBrandSearchParam()));
        result.put("category_list", categoryService.tree());
        result.put("type_list", GoodsTypeEnum.getMap());
        return result;
    }

    public PageResult<SelectGoodsListVo> getOfferGoodsList(PageParam pageParam, GoodsOutPutParam searchParam) {
        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();

        MPJQueryWrapper<ShopGoods> queryWrapper = new MPJQueryWrapper<>();
        queryWrapper.setAlias("g")
                .innerJoin("?_shop_goods_sku gs ON g.goods_id = gs.goods_id AND gs.is_default = 1".replace("?_", GlobalConfig.tablePrefix));
        queryWrapper.select("g.site_id, g.goods_id, g.goods_name, g.sub_title, g.goods_image, g.unit, g.goods_video, g.goods_desc, g.goods_id as source_id, g.goods_cover, g.stock");
        queryWrapper.eq("delete_time", 0);

        queryWrapper.eq("g.status", searchParam.getStatus() != null ? searchParam.getStatus() : 1);
        if (StringUtils.isNotBlank(searchParam.getGoodsName())) {
            queryWrapper.like("g.goods_name", searchParam.getGoodsName());
        }
        if (searchParam.getSiteId() != null) {
            queryWrapper.eq("g.site_id", searchParam.getSiteId());
        }
        if (StringUtils.isNotBlank(searchParam.getGoodsType())) {
            queryWrapper.eq("g.goods_type", searchParam.getGoodsType());
        }
        if (searchParam.getBrandId() != null) {
            queryWrapper.eq("g.brand_id", searchParam.getBrandId());
        }
        if (searchParam.getGoodsCategory() != null) {
            queryWrapper.eq("g.goods_category", searchParam.getGoodsCategory());
        }
        if (CollectionUtils.isNotEmpty(searchParam.getLabelIds())) {
            queryWrapper.in("g.label_ids", searchParam.getLabelIds());
        }

        queryWrapper.orderByDesc("g.create_time");

        IPage<SelectGoodsListVo> iPage = shopGoodsMapper.selectJoinPage(new Page<>(page, limit), SelectGoodsListVo.class, queryWrapper);
        if (iPage.getTotal() == 0 || iPage.getRecords().isEmpty()) {
            return PageResult.build(iPage, List.of());
        }

        List<SelectGoodsListVo> goodsList = iPage.getRecords();
        List<Integer> goodsIds = goodsList.stream()
                .map(SelectGoodsListVo::getGoodsId)
                .collect(Collectors.toList());

        QueryWrapper<ShopGoodsSku> skuQueryWrapper = new QueryWrapper<>();
        skuQueryWrapper.in("goods_id", goodsIds)
                .select("goods_id", "sku_id", "is_default", "sku_name", "sku_image",
                        "price", "is_default", "sku_spec_format", "sku_id", "stock")
                .orderByDesc("is_default");
        List<ShopGoodsSku> shopGoodsSkus = shopGoodsSkuMapper.selectList(skuQueryWrapper);
        List<SelectGoodsListVo.GoodsSkuVO> allSkus = shopGoodsSkus.stream().map(sku -> {
            SelectGoodsListVo.GoodsSkuVO goodsSkuVO = new SelectGoodsListVo.GoodsSkuVO();
            BeanUtils.copyProperties(sku, goodsSkuVO);
            goodsSkuVO.setSourceSkuId(sku.getSkuId());
            return goodsSkuVO;
        }).collect(Collectors.toList());

        QueryWrapper<ShopGoodsSpec> specQueryWrapper = new QueryWrapper<>();
        specQueryWrapper.in("goods_id", goodsIds)
                .select("goods_id", "spec_name", "spec_values");
        List<ShopGoodsSpec> shopGoodsSpecs = shopGoodsSpecMapper.selectList(specQueryWrapper);
        List<SelectGoodsListVo.GoodsSpecVO> allSpecs = CollectUtils.convert(shopGoodsSpecs, SelectGoodsListVo.GoodsSpecVO.class);

        Map<Integer, List<SelectGoodsListVo.GoodsSkuVO>> skuMap = allSkus.stream()
                .collect(Collectors.groupingBy(SelectGoodsListVo.GoodsSkuVO::getGoodsId));

        Map<Integer, List<SelectGoodsListVo.GoodsSpecVO>> specMap = allSpecs.stream()
                .collect(Collectors.groupingBy(SelectGoodsListVo.GoodsSpecVO::getGoodsId));

        for (SelectGoodsListVo goods : goodsList) {
            // 设置SKU列表
            List<SelectGoodsListVo.GoodsSkuVO> skuList = skuMap.getOrDefault(goods.getGoodsId(), new ArrayList<>());
            goods.setSkuList(skuList);

            // 设置规格信息
            List<SelectGoodsListVo.GoodsSpecVO> specList = specMap.getOrDefault(goods.getGoodsId(), new ArrayList<>());
            goods.setGoodsSpec(specList);

            // 判断规格类型
            boolean hasMultiSpec = skuList.stream()
                    .anyMatch(sku -> StringUtils.isNotBlank(sku.getSkuSpecFormat()));
            goods.setSpecType(hasMultiSpec ? "multi" : "single");

            // 设置缩略图（根据实际业务实现）
            if (StringUtils.isNotBlank(goods.getGoodsCover())) {
                goods.setGoodsCoverThumbSmall(CommonUtils.thumbImageSmall(goods.getSiteId(), goods.getGoodsCover()));
            }
        }
        return PageResult.build(page, limit, iPage.getTotal(), goodsList);
    }

    /**
     * 商品详情
     *
     * @param id 主键
     * @return ShopGoodsInfoVo
     */
    public ShopGoodsInfoVo getOfferGoodsInfo(Integer id) {
        ShopGoods model = shopGoodsMapper.selectOne(
                new QueryWrapper<ShopGoods>()
                        .eq("goods_id", id)
                        .eq("site_id", RequestUtils.siteId())
                        .eq("delete_time", 0));
        if (model == null) return null;

        ShopGoodsInfoVo vo = new ShopGoodsInfoVo();
        BeanUtils.copyProperties(model, vo);

        return vo;
    }
}
