package com.niu.shop.service.admin.marketing.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.date.DateUtils;
import com.niu.core.common.utils.mapper.QueryMapperUtils;
import com.niu.core.enums.upload.UploadThumbTypeEnum;
import com.niu.core.service.core.sys.ICoreUploadService;
import com.niu.shop.entity.active.ShopActive;
import com.niu.shop.entity.active.ShopActiveGoods;
import com.niu.shop.entity.exchange.ShopPointExchange;
import com.niu.shop.entity.goods.ShopGoods;
import com.niu.shop.entity.goods.ShopGoodsSku;
import com.niu.shop.enums.active.ActiveClassEnum;
import com.niu.shop.enums.active.ActiveGoodsTypeEnum;
import com.niu.shop.enums.active.ActiveStatusEnum;
import com.niu.shop.enums.active.ActiveTypeEnum;
import com.niu.shop.enums.marketing.ShopPointExchangeStatusEnum;
import com.niu.shop.enums.marketing.ShopPointExchangeTypeEnum;
import com.niu.shop.mapper.active.ShopActiveGoodsMapper;
import com.niu.shop.mapper.exchange.ShopPointExchangeMapper;
import com.niu.shop.mapper.goods.ShopGoodsMapper;
import com.niu.shop.mapper.goods.ShopGoodsSkuMapper;
import com.niu.shop.service.admin.marketing.IShopPointExchangeService;
import com.niu.shop.service.admin.marketing.param.ShopPointExchangeEditStatusParam;
import com.niu.shop.service.admin.marketing.param.ShopPointExchangeModifySortParam;
import com.niu.shop.service.admin.marketing.param.ShopPointExchangeParam;
import com.niu.shop.service.admin.marketing.param.ShopPointExchangeSearchParam;
import com.niu.shop.service.admin.marketing.vo.ShopPointExchangeInfoVo;
import com.niu.shop.service.admin.marketing.vo.ShopPointExchangeListVo;
import com.niu.shop.service.core.marketing.ICoreActiveService;
import com.niu.shop.service.core.marketing.vo.ProductDetail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import jakarta.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;


/**
 * 积分兑换实现
 */
@Slf4j
@Service
public class ShopPointExchangeServiceImpl implements IShopPointExchangeService {

    @Resource
    ShopPointExchangeMapper shopPointExchangeMapper;

    @Resource
    ShopGoodsSkuMapper shopGoodsSkuMapper;

    @Resource
    ShopGoodsMapper shopGoodsMapper;

    @Resource
    ShopActiveGoodsMapper shopActiveGoodsMapper;

    @Resource
    ICoreActiveService coreActiveService;

    @Resource
    ICoreUploadService coreUploadService;

    @Lazy
    @Resource
    private ShopPointExchangeServiceImpl shopPointExchangeServiceImpl;

    /**
     * 积分兑换列表
     *
     * @param pageParam   分页参数
     * @param searchParam 搜索参数
     * @return PageResult<ShopPointExchangeListVo>
     */
    @Override
    public PageResult<ShopPointExchangeListVo> list(PageParam pageParam, ShopPointExchangeSearchParam searchParam) {
        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();

        QueryWrapper<ShopPointExchange> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("site_id", RequestUtils.siteId());

        if (searchParam.getStatus() != null) {
            queryWrapper.eq("status", searchParam.getStatus());
        }

        if (searchParam.getNames() != null) {
            queryWrapper.like("names", searchParam.getNames());
        }

        if (ObjectUtil.isNotEmpty(searchParam.getCreateTime())) {
            QueryMapperUtils.buildByTime(queryWrapper, "create_time", searchParam.getCreateTime());
        }

        queryWrapper.select("total_exchange_num,stock,id,site_id,type,names,title,image,status,product_detail,point,price,limit_num,content,sort,total_point_num,total_price_num,total_order_num,total_member_num,update_time,create_time");
        queryWrapper.orderByDesc("id");

        IPage<ShopPointExchange> iPage = shopPointExchangeMapper.selectPage(new Page<>(page, limit), queryWrapper);
        List<ShopPointExchangeListVo> list = new LinkedList<>();
        for (ShopPointExchange item : iPage.getRecords()) {
            ShopPointExchangeListVo vo = new ShopPointExchangeListVo();
            BeanUtils.copyProperties(item, vo);
            vo.setType(ShopPointExchangeTypeEnum.getNameByType(item.getType()));
            vo.setStatusName(ShopPointExchangeStatusEnum.getNameByStatus(item.getStatus()));

            if (ObjectUtil.isNotEmpty(vo.getImage())) {
                String image = vo.getImage().split(",")[0];
                vo.setGoodsCoverThumbBig(coreUploadService.thumb(RequestUtils.siteId(), image, UploadThumbTypeEnum.BIG.getType()).getDataMap().get(UploadThumbTypeEnum.SMALL.getType()));
                vo.setGoodsCoverThumbSmall(coreUploadService.thumb(RequestUtils.siteId(), image, UploadThumbTypeEnum.SMALL.getType()).getDataMap().get(UploadThumbTypeEnum.SMALL.getType()));
            }

            vo.setCreateTime(DateUtils.timestampToString(item.getCreateTime()));
            vo.setUpdateTime(DateUtils.timestampToString(item.getUpdateTime()));
            list.add(vo);
        }
        return PageResult.build(page, limit, iPage.getTotal()).setData(list);
    }

    /**
     * 积分兑换详情
     *
     * @param id 主键
     * @return ShopPointExchangeInfoVo
     */
    @Override
    public ShopPointExchangeInfoVo info(Integer id) {
        ShopPointExchange model = shopPointExchangeMapper.selectOne(
                new QueryWrapper<ShopPointExchange>()
                        .eq("site_id", RequestUtils.siteId())
                        .eq("id", id)
                        .last("limit 1")
                        .select("stock,total_exchange_num,id,site_id,type,names,title,image,status,product_detail,point,price,limit_num,content,sort,total_point_num,total_price_num,total_order_num,total_member_num,update_time,create_time"));

        Assert.notNull(model, "数据不存在");

        ShopPointExchangeInfoVo vo = new ShopPointExchangeInfoVo();
        BeanUtils.copyProperties(model, vo);
        vo.setTypeName(ShopPointExchangeTypeEnum.getNameByType(vo.getType()));

        if (ShopPointExchangeTypeEnum.GOODS.getType().equals(vo.getType())) {
            List<ProductDetail> productDetails = JSONArray.parseArray(vo.getProductDetail(), ProductDetail.class);
            ProductDetail productDetail = Optional.ofNullable(CollectionUtil.get(productDetails, 0)).orElse(new ProductDetail());

            // --- goods list ---
            QueryWrapper<ShopGoodsSku> shopGoodsSkuQueryWrapper = new QueryWrapper<>();
            shopGoodsSkuQueryWrapper.eq("site_id", RequestUtils.siteId())
                    .eq("goods_id", productDetail.getGoodsId());

            List<ShopGoodsSku> shopGoodsSkus = shopGoodsSkuMapper.selectList(shopGoodsSkuQueryWrapper);
            List<ShopPointExchangeInfoVo.GoodsSku> goodsList = shopGoodsSkus.stream()
                    .map(goodsSku -> {
                        ShopPointExchangeInfoVo.GoodsSku goodsSkuVo = new ShopPointExchangeInfoVo.GoodsSku();
                        BeanUtils.copyProperties(goodsSku, goodsSkuVo);
                        goodsSkuVo.setGoodsPrice(goodsSku.getPrice());
                        goodsSkuVo.setGoodsStock(goodsSku.getStock());
                        return goodsSkuVo;
                    })
                    .collect(Collectors.toList());
            vo.setGoodsList(goodsList);

            // --- goods info ---
            QueryWrapper<ShopGoods> goodsInfoQueryWrapper = new QueryWrapper<>();
            goodsInfoQueryWrapper.eq("goods_id", productDetail.getGoodsId());
            goodsInfoQueryWrapper.eq("site_id", RequestUtils.siteId());
            goodsInfoQueryWrapper.select("goods_id,goods_name,goods_image,goods_cover");

            ShopGoods shopGoods = shopGoodsMapper.selectOne(goodsInfoQueryWrapper);
            ShopPointExchangeInfoVo.GoodsInfo goodsInfo = new ShopPointExchangeInfoVo.GoodsInfo();
            BeanUtils.copyProperties(shopGoods, goodsInfo);
            vo.setGoodsInfo(goodsInfo);

            Optional<ShopPointExchangeInfoVo.GoodsSku> goodsSkuOptional = Optional.ofNullable(CollectionUtil.get(vo.getGoodsList(), 0));

            // --- spec type ---
            String specType = goodsSkuOptional.isPresent() && StringUtils.hasText(goodsSkuOptional.get().getSkuSpecFormat()) ? "multi" : "single";
            vo.getGoodsInfo().setSpecType(specType);

            // --- goods price ---
            goodsSkuOptional.ifPresent(goodsSku -> vo.getGoodsInfo().setGoodsPrice(goodsSkuOptional.get().getGoodsPrice()));

            // --- is enable ---
            for (ShopPointExchangeInfoVo.GoodsSku goodsSku : vo.getGoodsList()) {
                Optional<ProductDetail> first = productDetails.stream().filter(detail -> Objects.equals(detail.getSkuId(), goodsSku.getSkuId())).findFirst();

                if (first.isPresent()) {
                    goodsSku.setIsEnabled(1);
                    BeanUtils.copyProperties(first.get(), goodsSku);
                    continue;
                }

                goodsSku.setIsEnabled(0);
            }
        }

        return vo;
    }

    /**
     * 积分兑换添加
     *
     * @param addParam 参数
     */
    @Override
    @Transactional
    public void add(ShopPointExchangeParam addParam) {
        addParam = verifyData(addParam);

        List<ProductDetail> productDetails = JSONArray.parseArray(addParam.getProductDetail(), ProductDetail.class);

        MPJLambdaWrapper<ShopActiveGoods> queryWrapper = new MPJLambdaWrapper<>();
        queryWrapper.eq(ShopActiveGoods::getSiteId, RequestUtils.siteId());
        queryWrapper.eq(ShopActiveGoods::getActiveClass, ActiveClassEnum.EXCHANGE.toString());
        queryWrapper.in(ShopActiveGoods::getGoodsId, productDetails.get(0).getGoodsId());
        queryWrapper.leftJoin(ShopActive.class, ShopActive::getActiveId, ShopActiveGoods::getActiveId);
        queryWrapper.select(ShopActiveGoods::getGoodsId);
        Long count = shopActiveGoodsMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new RuntimeException("此商品已经参与积分兑换活动了");
        }

        int stock = CharSequenceUtil.isBlank(addParam.getStock()) ? 0 : Integer.parseInt(addParam.getStock());

        ShopPointExchange model = new ShopPointExchange();
        model.setSiteId(RequestUtils.siteId());
        model.setType(addParam.getType());
        model.setNames(addParam.getNames());
        model.setTitle(CharSequenceUtil.isBlank(addParam.getTitle()) ? "" : addParam.getTitle());
        model.setImage(addParam.getImage());
        model.setStatus(ShopPointExchangeStatusEnum.UP.getStatus());
        model.setProductDetail(addParam.getProductDetail());
        model.setPoint(addParam.getPoint());
        model.setPrice(addParam.getPrice());
        model.setLimitNum(addParam.getLimitNum());
        model.setContent(addParam.getContent());
        model.setSort(addParam.getSort());
        model.setTotalPointNum(addParam.getTotalPointNum());
        model.setTotalOrderNum(addParam.getTotalOrderNum());
        model.setTotalMemberNum(addParam.getTotalMemberNum());
        model.setUpdateTime(System.currentTimeMillis() / 1000);
        model.setCreateTime(System.currentTimeMillis() / 1000);
        model.setStock(stock);
        model.setTotalExchangeNum(addParam.getTotalExchangeNum());
        shopPointExchangeMapper.insert(model);

        shopPointExchangeServiceImpl.joinShopActive(addParam, productDetails);
    }

    /**
     * 积分兑换编辑
     *
     * @param editParam 参数
     */
    @Override
    public void edit(Integer id, ShopPointExchangeParam editParam) {
        QueryWrapper<ShopPointExchange> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id);
        queryWrapper.eq("site_id", RequestUtils.siteId());

        ShopPointExchange model = shopPointExchangeMapper.selectOne(queryWrapper);
        Assert.notNull(model, "积分信息活动未找到!");

        editParam = verifyData(editParam);

        int stock = CharSequenceUtil.isBlank(editParam.getStock()) ? 0 : Integer.parseInt(editParam.getStock());

        model.setSiteId(editParam.getSiteId());
        model.setNames(editParam.getNames());
        model.setType(editParam.getType());
        model.setTitle(editParam.getTitle());
        model.setImage(editParam.getImage());
        model.setProductDetail(editParam.getProductDetail());
        model.setPoint(editParam.getPoint());
        model.setPrice(editParam.getPrice());
        model.setStock(stock);
        model.setLimitNum(editParam.getLimitNum());
        model.setContent(editParam.getContent());
        model.setUpdateTime(System.currentTimeMillis() / 1000);

        shopPointExchangeMapper.updateById(model);
    }

    /**
     * 积分兑换删除
     *
     * @param id 主键ID
     */
    @Override
    @Transactional
    public void del(Integer id) {
        QueryWrapper<ShopPointExchange> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id);
        queryWrapper.eq("site_id", RequestUtils.siteId());

        ShopPointExchange model = shopPointExchangeMapper.selectOne(queryWrapper);
        Assert.notNull(model, "积分信息活动未找到!");

        List<ProductDetail> productDetails = JSONArray.parseArray(model.getProductDetail(), ProductDetail.class);
        QueryWrapper<ShopActiveGoods> activeGoodsQueryWrapper = new QueryWrapper<>();
        activeGoodsQueryWrapper.eq("site_id", RequestUtils.siteId());
        activeGoodsQueryWrapper.in("goods_id", productDetails.get(0).getGoodsId());
        activeGoodsQueryWrapper.eq("active_class", ActiveClassEnum.EXCHANGE.toString());

        ShopActiveGoods shopActiveGoods = shopActiveGoodsMapper.selectOne(activeGoodsQueryWrapper);
        if (shopActiveGoods != null) {
            coreActiveService.del(shopActiveGoods.getActiveId(), RequestUtils.siteId(), 1);
        }

        QueryWrapper<ShopPointExchange> shopPointExchangeQueryWrapper = new QueryWrapper<>();
        shopPointExchangeQueryWrapper.eq("site_id", RequestUtils.siteId());
        shopPointExchangeQueryWrapper.eq("id", id);
        shopPointExchangeMapper.delete(shopPointExchangeQueryWrapper);
    }

    @Override
    public Map<String, String> type() {
        return Arrays.stream(ShopPointExchangeTypeEnum.values())
                .collect(Collectors.toMap(ShopPointExchangeTypeEnum::getType, ShopPointExchangeTypeEnum::getName));
    }

    @Override
    public Map<Integer, String> status() {
        return Arrays.stream(ShopPointExchangeStatusEnum.values())
                .collect(Collectors.toMap(ShopPointExchangeStatusEnum::getStatus, ShopPointExchangeStatusEnum::getName));
    }

    @Override
    public void editStatus(ShopPointExchangeEditStatusParam editStatusParam) {
        ShopPointExchange info = shopPointExchangeMapper.selectById(editStatusParam.getId());

        if (info == null) {
            log.warn("未找到积分兑换信息");
            return;
        }

        if (editStatusParam.getStatus().equals(1)) {
            List<ProductDetail> productDetails = JSONArray.parseArray(info.getProductDetail(), ProductDetail.class);

            QueryWrapper<ShopGoods> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("goods_id", productDetails.get(0).getGoodsId());

            ShopGoods shopGoods = shopGoodsMapper.selectOne(queryWrapper);
            if (shopGoods != null && shopGoods.getStatus() == 0) {
                throw new RuntimeException("此商品已下架");
            }
        }

        info.setStatus(editStatusParam.getStatus());
        shopPointExchangeMapper.updateById(info);
    }

    @Override
    public void modifySort(ShopPointExchangeModifySortParam modifySortParam) {
        ShopPointExchange info = shopPointExchangeMapper.selectById(modifySortParam.getId());

        if (info == null) {
            log.warn("未找到积分兑换信息");
            return;
        }

        info.setSort(modifySortParam.getSort());
        shopPointExchangeMapper.updateById(info);
    }

    @Transactional
    public void joinShopActive(ShopPointExchangeParam addParam, List<ProductDetail> productDetails) {
        ShopActive shopActive = new ShopActive();
        shopActive.setSiteId(RequestUtils.siteId());
        shopActive.setActiveStatus(ActiveStatusEnum.ACTIVE.getStatus());
        shopActive.setActiveType(ActiveTypeEnum.GOODS.getType());
        shopActive.setActiveGoodsType(ActiveGoodsTypeEnum.GOODS_SINGLE.getType());
        shopActive.setActiveClass(ActiveClassEnum.EXCHANGE.getType());
        shopActive.setActiveName(addParam.getNames());
        shopActive.setActiveDesc(addParam.getContent());
        shopActive.setStartTime(System.currentTimeMillis() / 1000);
        shopActive.setEndTime(0L);

        ShopActiveGoods shopActiveGoods = new ShopActiveGoods();
        shopActiveGoods.setSiteId(RequestUtils.siteId());
        shopActiveGoods.setGoodsId(productDetails.get(0).getGoodsId());
        shopActiveGoods.setActiveGoodsType(ActiveGoodsTypeEnum.GOODS_SINGLE.getType());
        shopActiveGoods.setActiveClass(ActiveClassEnum.EXCHANGE.getType());
        shopActiveGoods.setActiveGoodsValue(JSONArray.toJSONString(productDetails));
        shopActiveGoods.setActiveGoodsStatus(ActiveStatusEnum.ACTIVE.getStatus());
        shopActiveGoods.setActiveGoodsPrice(addParam.getPrice());
        shopActiveGoods.setActiveGoodsPoint(NumberUtil.toBigDecimal(addParam.getPoint()));

        coreActiveService.add(shopActive, Collections.singletonList(shopActiveGoods));
    }

    private ShopPointExchangeParam verifyData(ShopPointExchangeParam param) {
        List<ProductDetail> productDetails = JSONArray.parseArray(param.getProductDetail(), ProductDetail.class);

        String nameByType = ShopPointExchangeTypeEnum.getNameByType(param.getType());
        if (CharSequenceUtil.isBlank(nameByType)) {
            throw new RuntimeException("请确认兑换的类型");
        }

        if (ShopPointExchangeTypeEnum.GOODS.getName().equals(nameByType)) {
            if (CollectionUtil.isEmpty(productDetails)) {
                throw new RuntimeException("请选择参与活动商品");
            }

            int sumStock = 0;
            param.setStock("0");
            for (ProductDetail productDetail : productDetails) {
                if (Integer.parseInt(productDetail.getPoint()) <= 0) {
                    throw new RuntimeException("商品兑换积分要大于零");
                }

                int stock = Integer.parseInt(productDetail.getStock());
                if (stock < 0) {
                    throw new RuntimeException("商品兑换库存要大于零");
                }

                sumStock += stock;

                if (CharSequenceUtil.isBlank(productDetail.getPrice())) {
                    productDetail.setPrice("0");
                }
            }

            param.setStock(Integer.toString(sumStock));

            productDetails.sort(Comparator.comparing(ProductDetail::getPoint));
            ProductDetail productDetail = CollectionUtil.get(productDetails, 0);
            param.setPoint(Integer.parseInt(productDetail.getPoint()));
            param.setPrice(NumberUtil.toBigDecimal(productDetail.getPrice()));
            param.setLimitNum(Integer.parseInt(productDetail.getLimitNum()));
            param.setProductDetail(JSONArray.toJSONString(productDetails));
        }
        return param;
    }

}
