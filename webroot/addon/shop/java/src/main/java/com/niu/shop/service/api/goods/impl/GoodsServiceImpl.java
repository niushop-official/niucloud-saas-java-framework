package com.niu.shop.service.api.goods.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.niu.core.common.component.context.WebAppEnvs;
import com.niu.core.common.config.GlobalConfig;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.exception.AdminException;
import com.niu.core.common.utils.CollectUtils;
import com.niu.core.common.utils.CommonUtils;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.image.ImageUtils;
import com.niu.core.common.utils.mapper.QueryMapperUtils;
import com.niu.core.entity.sys.SysConfig;
import com.niu.core.event.common.CommonEventDefiner;
import com.niu.core.mapper.member.MemberLevelMapper;
import com.niu.core.mapper.member.MemberMapper;
import com.niu.core.mapper.sys.SysConfigMapper;
import com.niu.core.service.api.diy.IDiyService;
import com.niu.core.service.api.diy.param.DiyInfoParam;
import com.niu.core.service.api.member.IMemberLevelService;
import com.niu.core.service.api.member.vo.MemberLevelInfoVo;
import com.niu.core.service.core.app.helper.EventAndSubscribeOfPublisher;
import com.niu.core.service.core.sys.ICoreConfigService;
import com.niu.core.service.core.sys.ICoreUploadService;
import com.niu.core.service.core.sys.vo.CoreSysConfigVo;
import com.niu.shop.entity.coupon.ShopCouponGoods;
import com.niu.shop.entity.goods.*;
import com.niu.shop.entity.order.config.ShopOrderEvaluate;
import com.niu.shop.enums.active.ActiveClassEnum;
import com.niu.shop.enums.active.ActiveTypeEnum;
import com.niu.shop.enums.active.MarketingActivityTypeEnum;
import com.niu.shop.mapper.coupon.ShopCouponGoodsMapper;
import com.niu.shop.mapper.goods.*;
import com.niu.shop.service.api.goods.IGoodsService;
import com.niu.shop.service.api.goods.param.ShopGoodsComponentsSearchParam;
import com.niu.shop.service.api.goods.param.ShopGoodsDetailParam;
import com.niu.shop.service.api.goods.param.ShopGoodsSearchParam;
import com.niu.shop.service.api.goods.vo.GoodsLabelListVo;
import com.niu.shop.service.api.goods.vo.ShopGoodsDetailVo;
import com.niu.shop.service.api.goods.vo.ShopGoodsListVo;
import com.niu.shop.service.api.goods.vo.ShopGoodsSkuVo;
import com.niu.shop.service.api.marketing.IShopDiscountService;
import com.niu.shop.service.api.marketing.IShopNewcomerService;
import com.niu.shop.service.api.marketing.vo.ShopDiscountGoodsInfoVo;
import com.niu.shop.service.api.marketing.vo.ShopNewcomerInfoVo;
import com.niu.shop.service.core.goods.ICoreGoodsActivePriceService;
import com.niu.shop.service.core.goods.ICoreGoodsLimitBuyService;
import com.niu.shop.service.core.goods.ICoreGoodsStatService;
import com.niu.shop.service.core.goods.param.GetShowPriceParam;
import com.niu.shop.service.core.goods.param.GoodsAddStatParam;
import com.niu.shop.service.core.goods.vo.ShowPriceVo;
import com.niu.shop.service.core.order.ICoreOrderConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.math.BigDecimal;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class GoodsServiceImpl implements IGoodsService {

    @Resource
    ShopGoodsMapper shopGoodsMapper;

    @Resource
    ShopGoodsSkuMapper shopGoodsSkuMapper;

    @Resource
    ShopGoodsSpecMapper shopGoodsSpecMapper;

    @Resource
    ICoreUploadService coreUploadService;

    @Resource
    IMemberLevelService memberLevelService;

    @Resource
    ShopGoodsServiceMapper shopGoodsServiceMapper;

    @Resource
    ShopGoodsLabelMapper shopGoodsLabelMapper;

    @Resource
    ShopGoodsBrandMapper shopGoodsBrandMapper;

    @Resource
    ShopGoodsCollectMapper shopGoodsCollectMapper;

    @Resource
    ICoreOrderConfigService coreOrderConfigService;

    @Resource
    IShopDiscountService discountService;

    @Resource
    MemberMapper memberMapper;

    @Resource
    MemberLevelMapper memberLevelMapper;

    @Resource
    IShopNewcomerService shopNewcomerService;

    @Resource
    ShopCouponGoodsMapper shopCouponGoodsMapper;

    @Resource
    ICoreGoodsStatService coreGoodsStatService;

    @Resource
    IDiyService diyService;

    @Resource
    SysConfigMapper sysConfigMapper;

    @Resource
    ICoreGoodsActivePriceService coreGoodsActivePriceService;

    @Resource
    ICoreGoodsLimitBuyService coreGoodsLimitBuyService;

    @Resource
    ICoreConfigService coreConfigService;

    /**
     * 查询商品列表
     *
     * @param pageParam
     * @param searchParam
     * @return
     */
    @Override
    public PageResult<ShopGoodsListVo> page(PageParam pageParam, ShopGoodsSearchParam searchParam) {
        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();

        MPJQueryWrapper<ShopGoods> queryWrapper = new MPJQueryWrapper<>();
        queryWrapper.setAlias("g")
                .innerJoin("?_shop_goods_sku gs ON g.goods_id = gs.goods_id AND gs.is_default = 1".replace("?_", GlobalConfig.tablePrefix))
                .leftJoin("?_shop_goods_brand sgb ON g.brand_id = sgb.brand_id".replace("?_", GlobalConfig.tablePrefix));
        queryWrapper.select("g.goods_id,g.site_id,goods_name,sub_title,goods_type,goods_cover,g.stock,g.sale_num + g.virtual_sale_num as sale_num,status,g.sort,g.create_time,member_discount,virtual_verify_type,unit,is_discount,label_ids,g.stock,g.brand_id,sgb.brand_name,sgb.logo");
        queryWrapper.eq("g.site_id", RequestUtils.siteId());
        queryWrapper.eq("g.status", 1);
        queryWrapper.eq("g.is_gift", 0);
        queryWrapper.eq("g.delete_time", 0);
        if (ObjectUtil.isNotEmpty(searchParam.getKeyword())) queryWrapper.like("goods_name", searchParam.getKeyword());
        if (ObjectUtil.isNotEmpty(searchParam.getBrandId())) queryWrapper.eq("brand_id", searchParam.getBrandId());
        if (ObjectUtil.isNotEmpty(searchParam.getGoodsCategory()))
            queryWrapper.like("goods_category", "\"" + searchParam.getGoodsCategory() + "\"");
        if (ObjectUtil.isNotEmpty(searchParam.getLabelIds()))
            queryWrapper.like("label_ids", "\"" + searchParam.getLabelIds() + "\"");
        QueryMapperUtils.buildByBetween(queryWrapper, "gs.price", new Double[]{searchParam.getStartPrice(), searchParam.getEndPrice()});

        if (ObjectUtil.isNotEmpty(searchParam.getOrder()) && (searchParam.getOrder().equals("sale_num") || searchParam.getOrder().equals("price"))) {
            queryWrapper.orderBy(true, searchParam.getSort().equals("asc"), searchParam.getOrder());
        } else {
            //查询配置数据
            QueryWrapper<SysConfig> sysConfigQueryWrapper = new QueryWrapper<>();
            sysConfigQueryWrapper.eq("site_id", RequestUtils.siteId());
            sysConfigQueryWrapper.eq("config_key","GOODS_SORT_CONFIG");
            SysConfig sysConfigInfo =  sysConfigMapper.selectOne(sysConfigQueryWrapper);

            //如果配置了商品编码唯一，则校验
            if(ObjectUtil.isNotNull(sysConfigInfo)){
                JSONObject configInfo = JSONUtil.parseObj(sysConfigInfo.getValue());
                if(configInfo.getStr("sortColumn").equals("sale_price")){
                    queryWrapper.orderBy(true, configInfo.getStr("sortType").equals("asc")?true:false,"g.sort");
                    queryWrapper.orderBy(true, configInfo.getStr("sortType").equals("asc")?true:false, "gs."+ configInfo.getStr("sortColumn"));
                } else {
                    queryWrapper.orderBy(true, configInfo.getStr("sortType").equals("asc")?true:false,"g.sort");
                    queryWrapper.orderBy(true, configInfo.getStr("sortType").equals("asc")?true:false, "g." + configInfo.getStr("sortColumn"));
                }
            }
        }

        if (searchParam.getCouponId() != null && searchParam.getCouponId() > 0) {
            List<ShopCouponGoods> couponGoodsList = shopCouponGoodsMapper.selectList(new QueryWrapper<ShopCouponGoods>()
                    .eq("coupon_id", searchParam.getCouponId())
                    .select("category_id,goods_id"));
            List<Integer> goodsIds = couponGoodsList.stream().filter(i -> i.getGoodsId() > 0).map(i -> i.getGoodsId()).collect(Collectors.toList());
            List<Integer> categoryIds = couponGoodsList.stream().filter(i -> i.getCategoryId() > 0).map(i -> i.getCategoryId()).collect(Collectors.toList());
            if (goodsIds.size() > 0) {
                queryWrapper.in("g.goods_id", goodsIds);
            } else if (categoryIds.size() > 0) {
                queryWrapper.and(i -> {
                    for (int index = 0; index < categoryIds.size(); index++) {
                        Integer categoryId = categoryIds.get(index);
                        if (index == 0) {
                            i.like("goods_category", "\"" + categoryId + "\"");
                        } else {
                            i.or().like("goods_category", "\"" + categoryId + "\"");
                        }
                    }
                });
            }
        }

        IPage<ShopGoodsListVo> iPage = shopGoodsMapper.selectJoinPage(new Page<>(page, limit), ShopGoodsListVo.class, queryWrapper);
        List<ShopGoodsListVo> list = new LinkedList<>();
        for (ShopGoodsListVo vo : iPage.getRecords()) {
            ShopGoodsSku goodsSku = shopGoodsSkuMapper.selectOne(new QueryWrapper<ShopGoodsSku>()
                    .eq("goods_id", vo.getGoodsId()).eq("is_default", 1));
            ShopGoodsListVo.GoodsSku goodsSkuVo = new ShopGoodsListVo.GoodsSku();
            BeanUtils.copyProperties(goodsSku, goodsSkuVo);

            ShowPriceVo skuShowPrice = coreGoodsActivePriceService.getShowPrice(GetShowPriceParam.builder()
                .skuId(goodsSku.getSkuId())
                .price(goodsSku.getPrice())
                .memberDiscount(vo.getMemberDiscount())
                .memberPrice(goodsSku.getMemberPrice())
                .memberId(RequestUtils.memberId())
                .build());
            goodsSkuVo.setShowPrice(skuShowPrice.getShowPrice());
            goodsSkuVo.setShowType(skuShowPrice.getShowType());

            vo.setGoodsSku(goodsSkuVo);
            vo.setGoodsLabelName(getGoodsLabelName(vo.getLabelIds()));
            if (vo.getBrandId() > 0) {
                ShopGoodsListVo.Brand brand = new ShopGoodsListVo.Brand();
                BeanUtils.copyProperties(vo, brand);
                vo.setGoodsBrand(brand);
            }
            // 限购查询当前会员已购数量
            if (RequestUtils.memberId() != null) {
                vo.setHasBuy(coreGoodsLimitBuyService.getGoodsHasBuyNumber(RequestUtils.siteId(), RequestUtils.memberId(), vo.getGoodsId()));
            }else{
                vo.setHasBuy(0);
            }

            list.add(vo);
        }
        return PageResult.build(page, limit, iPage.getTotal()).setData(list);
    }

    /**
     * 计算会员价
     *
     * @param memberLevel   会员等级
     * @param calculateType 计算方式
     * @param config        会员价配置
     * @param price         商品价格
     * @return
     */
    public BigDecimal calculateMemberPrice(MemberLevelInfoVo memberLevel, String calculateType, JSONObject config, BigDecimal price) {
        return coreGoodsActivePriceService.calculateMemberPrice(memberLevel, calculateType, config, price);
    }

    /**
     * 查询商品详情
     *
     * @param param
     * @return
     */
    @Override
    public ShopGoodsDetailVo detail(ShopGoodsDetailParam param) {
        ShopGoodsDetailVo vo = new ShopGoodsDetailVo();

        QueryWrapper queryWrapper = new QueryWrapper<ShopGoodsSku>();
        queryWrapper.eq("site_id", RequestUtils.siteId());

        if (ObjectUtil.isEmpty(param.getSkuId()) && ObjectUtil.isNotEmpty(param.getGoodsId())) {
            queryWrapper.eq("goods_id", param.getGoodsId());
            queryWrapper.eq("is_default", 1);
        } else if (ObjectUtil.isNotEmpty(param.getSkuId())) {
            queryWrapper.eq("sku_id", param.getSkuId());
        } else {
            return null;
        }

        ShopGoodsSku sku = shopGoodsSkuMapper.selectOne(queryWrapper);
        if (sku == null) return null;
        BeanUtils.copyProperties(sku, vo);
        vo.setType(ObjectUtil.defaultIfNull(param.getType(), ""));

        // 查询商品
        ShopGoods goods = shopGoodsMapper.selectOne(new QueryWrapper<ShopGoods>().eq("goods_id", sku.getGoodsId()).eq("delete_time", 0));
        if (goods == null) return null;
        ShopGoodsDetailVo.Goods goodsVo = new ShopGoodsDetailVo.Goods();
        BeanUtils.copyProperties(goods, goodsVo);

        // 获取展示价格
        ShowPriceVo detailShowPrice = coreGoodsActivePriceService.getShowPrice(GetShowPriceParam.builder()
                .skuId(sku.getSkuId())
                .price(sku.getPrice())
                .memberDiscount(goods.getMemberDiscount())
                .memberPrice(sku.getMemberPrice())
                .memberId(RequestUtils.memberId())
                .build());
        vo.setShowPrice(detailShowPrice.getShowPrice());
        vo.setShowType(detailShowPrice.getShowType());
        vo.setMemberDiscount(goods.getMemberDiscount());
        goodsVo.setSaleNum(goods.getSaleNum() + goods.getVirtualSaleNum());
        //商品属性值
        goodsVo.setAttrFormat(JSONUtil.parseArray(goods.getAttrFormat()));

        // 是否已收藏
        if (RequestUtils.memberId() != null) {
            Long goodsCollect = shopGoodsCollectMapper.selectCount(new QueryWrapper<ShopGoodsCollect>().eq("goods_id", goodsVo.getGoodsId()).eq("member_id", RequestUtils.memberId()));
            goodsVo.setIsCollect(goodsCollect == 0 ? 0 : 1);
            goodsVo.setHasBuy(coreGoodsLimitBuyService.getGoodsHasBuyNumber(RequestUtils.siteId(), RequestUtils.memberId(), goodsVo.getGoodsId()));
        }
        vo.setGoods(goodsVo);

        // 查询商品SKU列表
        List<ShopGoodsDetailVo.Sku> skuListVo = new LinkedList<>();
        List<ShopGoodsSku> skuList = shopGoodsSkuMapper.selectList(new QueryWrapper<ShopGoodsSku>().eq("goods_id", sku.getGoodsId()));
        for (ShopGoodsSku item : skuList) {
            ShopGoodsDetailVo.Sku skuVo = new ShopGoodsDetailVo.Sku();
            BeanUtils.copyProperties(item, skuVo);
            skuVo.setType(ObjectUtil.defaultIfNull(param.getType(), ""));

            ShowPriceVo skuShowPrice = coreGoodsActivePriceService.getShowPrice(GetShowPriceParam.builder()
                    .skuId(item.getSkuId())
                    .price(item.getPrice())
                    .memberDiscount(goods.getMemberDiscount())
                    .memberPrice(item.getMemberPrice())
                    .memberId(RequestUtils.memberId())
                    .build());
            skuVo.setShowPrice(skuShowPrice.getShowPrice());
            skuVo.setShowType(skuShowPrice.getShowType());
            skuListVo.add(skuVo);
        }
        vo.setSkuList(skuListVo);

        // 商品规格
        List<ShopGoodsDetailVo.Spec> specList = CollectUtils.convert(shopGoodsSpecMapper.selectList(new QueryWrapper<ShopGoodsSpec>().eq("goods_id", sku.getGoodsId())), ShopGoodsDetailVo.Spec.class);
        vo.setGoodsSpec(specList);

        // 商品服务
        if (!goodsVo.getServiceIds().isEmpty()) {
            JSONArray serviceIds = JSONUtil.parseArray(goodsVo.getServiceIds());
            if (!serviceIds.isEmpty()) {
                List<ShopGoodsDetailVo.Service> service = CollectUtils.convert(shopGoodsServiceMapper.selectList(new QueryWrapper<ShopGoodsService>().in("service_id", serviceIds)), ShopGoodsDetailVo.Service.class);
                vo.setService(service);
            }
        }

        // 商品标签
        vo.setLabelInfo(getGoodsLabelName(goods.getLabelIds()));

        // 商品品牌
        if (goodsVo.getBrandId() > 0) {
            ShopGoodsBrand brand = shopGoodsBrandMapper.selectOne(new QueryWrapper<ShopGoodsBrand>().eq("brand_id", goodsVo.getBrandId()));
            ShopGoodsDetailVo.Brand brandVo = new ShopGoodsDetailVo.Brand();
            BeanUtils.copyProperties(brand, brandVo);
            vo.setBrandInfo(brandVo);
        }

        //查询评价设置是否显示
        ShopOrderEvaluate orderEvaluate = coreOrderConfigService.getEvaluateConfig(RequestUtils.siteId());
        vo.setEvaluateIsShow(orderEvaluate.getEvaluateIsShow());

        // 限时折扣
        if (goods.getIsDiscount().equals(1)) {
            vo.setDiscountInfo(discountService.getInfoByGoods(goodsVo.getGoodsId()));
        } else {
            vo.setType("");
        }

        //新人专享
        if (ActiveClassEnum.NEWCOMER_DISCOUNT.getType().equals(param.getType())) {
            if (shopNewcomerService.checkIfNewcomer()) {
                ShopNewcomerInfoVo newcomerInfo = shopNewcomerService.getNewcomerInfo(vo.getGoodsId(), vo.getSkuId());
                if (ObjectUtil.isNotEmpty(newcomerInfo)) {
                    vo.setNewcomerPrice(newcomerInfo.getNewcomerPrice().toString());
                    vo.setNewcomerDesc(newcomerInfo.getNewcomerDesc());
                    vo.setIsNewcomer(1);
                    vo.setType(ActiveClassEnum.NEWCOMER_DISCOUNT.getType());
                }
                vo.setSkuList(shopNewcomerService.getNewcomerPriceByList(vo.getSkuList()));
            } else {
                vo.setIsNewcomer(0);
                vo.setType("");
                vo.setTypeName("");
            }
        }

        GoodsAddStatParam addStatParam = new GoodsAddStatParam();
        addStatParam.setGoodsId(goods.getGoodsId());
        addStatParam.setSiteId(vo.getSiteId());
        addStatParam.setAccessNum(1);
        coreGoodsStatService.addStat(addStatParam);

        CommonEventDefiner.CommonEvent event = new CommonEventDefiner.CommonEvent();
        Map<String, Object> eventMap = new HashMap<>();
        eventMap.put("relate_id", vo.getGoodsId());
        eventMap.put("relate_type", MarketingActivityTypeEnum.SHOP.getType());
        eventMap.put("limit", 3);
        event.setEventName("SowShowData");
        event.setData(eventMap);
        event.setSiteId(RequestUtils.siteId());
        List<CommonEventDefiner.CommonEventResult> sowShowDataResult = EventAndSubscribeOfPublisher.publishAndCallback(event);
        vo.setSowShowList(List.of());
        if (CollectionUtils.isNotEmpty(sowShowDataResult) && ObjectUtils.isNotEmpty(sowShowDataResult.get(0).getResult())){
            vo.setSowShowList(sowShowDataResult.get(0).getResult());
        }

        // 自定义详情
        DiyInfoParam diyInfoParam = new DiyInfoParam();
        diyInfoParam.setName("DIY_SHOP_GOODS_DETAIL");
        diyInfoParam.setId(goods.getDiyDetailId());
        vo.setDiyDetailInfo(diyService.info(diyInfoParam));

        // 获取商品主图第一张图片宽高
        ImageUtils.ImageSize firstImageSize = ImageUtils.getFirstImageSize(goods.getGoodsImage());
        vo.getImageSize().setWidth(firstImageSize.getWidth().toString());
        vo.getImageSize().setHeight(firstImageSize.getHeight().toString());

        return vo;
    }
    @Override
    public List<GoodsLabelListVo> getGoodsLabelName(String labelIds) {
        if (ObjectUtil.isEmpty(labelIds) || JSONUtil.parseArray(labelIds).isEmpty()) return new LinkedList<>();
        return CollectUtils.convert(shopGoodsLabelMapper.selectList(new QueryWrapper<ShopGoodsLabel>().eq("status", 1).in("label_id", JSONUtil.parseArray(labelIds)).orderByDesc("sort")), GoodsLabelListVo.class);
    }

    @Override
    public CoreSysConfigVo searchConfig() {
        return coreConfigService.getConfig(RequestUtils.siteId(), "GOODS_SEARCH_CONFIG");
    }


    /**
     * @param skuId
     * @return
     */
    @Override
    public ShopGoodsSkuVo getSku(Integer skuId) {
        ShopGoodsSkuVo vo = new ShopGoodsSkuVo();

        QueryWrapper queryWrapper = new QueryWrapper<ShopGoodsSku>();
        queryWrapper.eq("site_id", RequestUtils.siteId());
        queryWrapper.eq("sku_id", skuId);

        ShopGoodsSku sku = shopGoodsSkuMapper.selectOne(queryWrapper);
        if (sku == null) return null;
        BeanUtils.copyProperties(sku, vo);

        // 查询商品
        ShopGoods goods = shopGoodsMapper.selectOne(new QueryWrapper<ShopGoods>().eq("goods_id", sku.getGoodsId()).eq("delete_time", 0));
        if (goods == null) return null;
        ShopGoodsSkuVo.Goods goodsVo = new ShopGoodsSkuVo.Goods();
        BeanUtils.copyProperties(goods, goodsVo);
        vo.setGoods(goodsVo);

        // 获取展示价格
        ShowPriceVo detailShowPrice = coreGoodsActivePriceService.getShowPrice(GetShowPriceParam.builder()
            .skuId(sku.getSkuId())
            .price(sku.getPrice())
            .memberDiscount(goods.getMemberDiscount())
            .memberPrice(sku.getMemberPrice())
            .memberId(RequestUtils.memberId())
            .build());
        vo.setShowPrice(detailShowPrice.getShowPrice());
        vo.setShowType(detailShowPrice.getShowType());
        //商品限购
        if(ObjectUtil.isNotEmpty(RequestUtils.memberId()))
        {
            vo.setHasBuy(coreGoodsLimitBuyService.getGoodsHasBuyNumber(RequestUtils.siteId(),RequestUtils.memberId(), goods.getGoodsId()));
        }

        // 查询商品SKU列表
        List<ShopGoodsSkuVo.Sku> skuListVo = new LinkedList<>();
        List<ShopGoodsSku> skuList = shopGoodsSkuMapper.selectList(new QueryWrapper<ShopGoodsSku>().eq("goods_id", sku.getGoodsId()));
        for (ShopGoodsSku item : skuList) {
            ShopGoodsSkuVo.Sku skuVo = new ShopGoodsSkuVo.Sku();
            BeanUtils.copyProperties(item, skuVo);
            ShowPriceVo skuShowPrice = coreGoodsActivePriceService.getShowPrice(GetShowPriceParam.builder()
                .skuId(item.getSkuId())
                .price(item.getPrice())
                .memberDiscount(goods.getMemberDiscount())
                .memberPrice(item.getMemberPrice())
                .memberId(RequestUtils.memberId())
                .build());
            skuVo.setShowPrice(skuShowPrice.getShowPrice());
            skuVo.setShowType(skuShowPrice.getShowType());
            skuListVo.add(skuVo);
        }
        vo.setSkuList(skuListVo);

        // 商品规格
        List<ShopGoodsSkuVo.Spec> specList = CollectUtils.convert(shopGoodsSpecMapper.selectList(new QueryWrapper<ShopGoodsSpec>().eq("goods_id", sku.getGoodsId())), ShopGoodsSkuVo.Spec.class);
        vo.setGoodsSpec(specList);

        return vo;
    }

    /**
     * 获取商品列表供组件调用
     *
     * @param param
     */
    @Override
    public List<ShopGoodsListVo> getGoodsComponents(ShopGoodsComponentsSearchParam param) {
        MPJQueryWrapper<ShopGoods> queryWrapper = new MPJQueryWrapper<>();
        queryWrapper.setAlias("g")
            .innerJoin("?_shop_goods_sku gs ON g.goods_id = gs.goods_id AND gs.is_default = 1".replace("?_", GlobalConfig.tablePrefix))
            .leftJoin("?_shop_goods_brand sgb ON g.brand_id = sgb.brand_id".replace("?_", GlobalConfig.tablePrefix));
        queryWrapper.select("g.goods_id,g.site_id,goods_name,sub_title,goods_type,goods_cover,g.stock,g.sale_num + g.virtual_sale_num as sale_num,status,g.sort,g.create_time,member_discount,virtual_verify_type,unit,is_discount,label_ids,g.stock,g.brand_id,sgb.brand_name,sgb.logo");
        queryWrapper.eq("g.site_id", RequestUtils.siteId());
        queryWrapper.eq("g.status", 1);
        queryWrapper.eq("g.is_gift", 0);
        queryWrapper.eq("g.delete_time", 0);
        if (ObjectUtil.isNotEmpty(param.getGoodsIds())) queryWrapper.in("g.goods_id", param.getGoodsIds());
        if (ObjectUtil.isNotEmpty(param.getNum()) && param.getNum() > 0) queryWrapper.last("limit " + param.getNum());
        if (ObjectUtil.isNotEmpty(param.getGoodsCategory()) && param.getGoodsCategory() > 0)
            queryWrapper.like("goods_category", "\"" + param.getGoodsCategory() + "\"");
        if (ObjectUtil.isNotEmpty(param.getOrder())) {
            if (param.getOrder().equals("sale_num")) {
                queryWrapper.orderByDesc("g.sale_num");
            } else if (param.getOrder().equals("price")) {
                queryWrapper.orderByDesc("gs.price");
            }
        } else {
            queryWrapper.orderBy(true, false, "g.sort");
            queryWrapper.orderBy(true, false, "g.create_time");
        }

        List<ShopGoodsListVo> list = shopGoodsMapper.selectJoinList(ShopGoodsListVo.class, queryWrapper);
        for (ShopGoodsListVo vo : list) {
            ShopGoodsSku goodsSku = shopGoodsSkuMapper.selectOne(new QueryWrapper<ShopGoodsSku>()
                    .eq("goods_id", vo.getGoodsId()).eq("is_default", 1));
            ShopGoodsListVo.GoodsSku goodsSkuVo = new ShopGoodsListVo.GoodsSku();
            BeanUtils.copyProperties(goodsSku, goodsSkuVo);

            ShowPriceVo skuShowPrice = coreGoodsActivePriceService.getShowPrice(GetShowPriceParam.builder()
                    .skuId(goodsSku.getSkuId())
                    .price(goodsSku.getPrice())
                    .memberDiscount(vo.getMemberDiscount())
                    .memberPrice(goodsSku.getMemberPrice())
                    .memberId(RequestUtils.memberId())
                    .build());
            goodsSkuVo.setShowPrice(skuShowPrice.getShowPrice());
            goodsSkuVo.setShowType(skuShowPrice.getShowType());

            vo.setGoodsSku(goodsSkuVo);
            vo.setGoodsLabelName(getGoodsLabelName(vo.getLabelIds()));
            if (vo.getBrandId() > 0) {
                ShopGoodsListVo.Brand brand = new ShopGoodsListVo.Brand();
                BeanUtils.copyProperties(vo, brand);
                vo.setGoodsBrand(brand);
            }
        }
        return list;
    }

}
