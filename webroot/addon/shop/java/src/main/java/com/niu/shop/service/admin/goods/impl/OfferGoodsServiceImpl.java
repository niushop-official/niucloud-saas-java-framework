package com.niu.shop.service.admin.goods.impl;

import cn.hutool.core.util.ObjectUtil;
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
import com.niu.shop.service.admin.goods.IOfferGoodsService;
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
public class OfferGoodsServiceImpl implements IOfferGoodsService {

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


    @Override
    public Map<String, Object> getWhereInit() {
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

        QueryWrapper<ShopGoods> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("site_id,goods_id,goods_name,sub_title,goods_image,unit,goods_video,goods_desc,goods_id,goods_cover,stock");
        queryWrapper.eq("delete_time", 0);

        queryWrapper.eq("status", searchParam.getStatus() != null ? searchParam.getStatus() : 1);
        if (StringUtils.isNotBlank(searchParam.getGoodsName())) {
            queryWrapper.like("goods_name", searchParam.getGoodsName());
        }
        if (searchParam.getSiteId() != null) {
            queryWrapper.eq("site_id", searchParam.getSiteId());
        }
        if (StringUtils.isNotBlank(searchParam.getGoodsType())) {
            queryWrapper.eq("goods_type", searchParam.getGoodsType());
        }
        if (searchParam.getBrandId() != null) {
            queryWrapper.eq("brand_id", searchParam.getBrandId());
        }
        if (searchParam.getGoodsCategory() != null) {
            queryWrapper.eq("goods_category", searchParam.getGoodsCategory());
        }
        if (CollectionUtils.isNotEmpty(searchParam.getLabelIds())) {
            queryWrapper.in("label_ids", searchParam.getLabelIds());
        }
        if (searchParam.getGoodsId() != null){
            queryWrapper.eq("goods_id", searchParam.getGoodsId());
        }

        queryWrapper.orderByDesc("create_time");

        IPage<ShopGoods> iPage = shopGoodsMapper.selectPage(new Page<>(page, limit), queryWrapper);
        if (iPage.getTotal() == 0 || iPage.getRecords().isEmpty()) {
            return PageResult.build(iPage, List.of());
        }
        List<SelectGoodsListVo> goodsList = iPage.getRecords().stream().map(item ->{
            SelectGoodsListVo selectGoodsListVo = new SelectGoodsListVo();
            BeanUtils.copyProperties(item, selectGoodsListVo);
            selectGoodsListVo.setSourceId(item.getGoodsId());
            return selectGoodsListVo;
        }).collect(Collectors.toList());
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
     * @param
     * @return ShopGoodsInfoVo
     */
    @Override
    public SelectGoodsListVo getOfferGoodsInfo(Integer goodsId) {
        PageParam pageParam = new PageParam();
        pageParam.setPage(1);
        pageParam.setLimit(1);
        GoodsOutPutParam goodsOutPutParam = new GoodsOutPutParam();
        goodsOutPutParam.setGoodsId(goodsId);
        PageResult<SelectGoodsListVo> offerGoodsList = getOfferGoodsList(pageParam, goodsOutPutParam);

        return offerGoodsList.getData().get(0);
    }

    @Override
    public CheckGoodsVo checkGoods(CheckGoodsParam param) {
       ShopGoods shopGoods = shopGoodsMapper.selectById(param.getGoodsId());
       ShopGoodsSku shopGoodsSku = shopGoodsSkuMapper.selectById(param.getSkuId());
       CheckGoodsVo checkGoodsVo = new CheckGoodsVo();
       if(ObjectUtil.isEmpty(shopGoods) || ObjectUtil.isEmpty(shopGoodsSku)){
           checkGoodsVo.setCode(-1);
           checkGoodsVo.setMessage("商品不存在或已下架");
           return checkGoodsVo;
       }
       if(shopGoodsSku.getStock() < param.getNum()) {
           checkGoodsVo.setCode(-1);
           checkGoodsVo.setMessage("商品库存不足");
           return checkGoodsVo;
       }

       checkGoodsVo.setCode(1);
       checkGoodsVo.setMessage("成功");
       return checkGoodsVo;

    }
}
