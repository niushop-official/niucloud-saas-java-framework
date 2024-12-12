package com.niu.shop.service.api.goods.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.niu.core.common.config.GlobalConfig;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.utils.CollectUtils;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.mapper.QueryMapperUtils;
import com.niu.core.mapper.member.MemberLevelMapper;
import com.niu.core.mapper.member.MemberMapper;
import com.niu.core.service.api.member.IMemberLevelService;
import com.niu.core.service.api.member.vo.MemberLevelInfoVo;
import com.niu.core.service.core.sys.ICoreUploadService;
import com.niu.shop.entity.coupon.ShopCouponGoods;
import com.niu.shop.entity.goods.*;
import com.niu.shop.entity.order.config.ShopOrderEvaluate;
import com.niu.shop.enums.active.ActiveClassEnum;
import com.niu.shop.enums.active.ActiveTypeEnum;
import com.niu.shop.mapper.coupon.ShopCouponGoodsMapper;
import com.niu.shop.mapper.goods.*;
import com.niu.shop.service.api.goods.IGoodsService;
import com.niu.shop.service.api.goods.param.ShopGoodsComponentsSearchParam;
import com.niu.shop.service.api.goods.param.ShopGoodsDetailParam;
import com.niu.shop.service.api.goods.param.ShopGoodsSearchParam;
import com.niu.shop.service.api.goods.vo.ShopGoodsDetailVo;
import com.niu.shop.service.api.goods.vo.ShopGoodsListVo;
import com.niu.shop.service.api.goods.vo.ShopGoodsSkuVo;
import com.niu.shop.service.api.marketing.IShopDiscountService;
import com.niu.shop.service.api.marketing.IShopNewcomerService;
import com.niu.shop.service.api.marketing.vo.ShopNewcomerInfoVo;
import com.niu.shop.service.core.order.ICoreOrderConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
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
                .innerJoin("?_shop_goods_sku gs ON g.goods_id = gs.goods_id AND gs.is_default = 1".replace("?_", GlobalConfig.tablePrefix));
        queryWrapper.select("g.goods_id,g.site_id,goods_name,goods_type,goods_cover,g.stock,g.sale_num + g.virtual_sale_num as sale_num,status,sort,create_time,member_discount,virtual_verify_type,unit,is_discount");
        queryWrapper.eq("g.site_id", RequestUtils.siteId());
        queryWrapper.eq("g.status", 1);
        queryWrapper.eq("delete_time", 0);
        if (ObjectUtil.isNotEmpty(searchParam.getKeyword())) queryWrapper.like("goods_name", searchParam.getKeyword());
        if (ObjectUtil.isNotEmpty(searchParam.getBrandId())) queryWrapper.eq("brand_id", searchParam.getBrandId());
        if (ObjectUtil.isNotEmpty(searchParam.getGoodsCategory()))
            queryWrapper.like("goods_category", "\"" + searchParam.getGoodsCategory() + "\"");
        if (ObjectUtil.isNotEmpty(searchParam.getLabelIds()))
            queryWrapper.like("label_ids", "\"" + searchParam.getLabelIds() + "\"");
        QueryMapperUtils.buildByBetween(queryWrapper, "gs.price", new Double[]{searchParam.getStartPrice(), searchParam.getEndPrice()});

        if (ObjectUtil.isNotEmpty(searchParam.getOrder())) {
            queryWrapper.orderBy(true, searchParam.getSort().equals("asc"), searchParam.getOrder());
        } else {
            queryWrapper.orderBy(true, false, "sort");
            queryWrapper.orderBy(true, false, "create_time");
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

        MemberLevelInfoVo memberLevel = null;
        if (ObjectUtil.isNotEmpty(RequestUtils.memberId()))
            memberLevel = memberLevelService.findMemberLevel(RequestUtils.memberId());

        IPage<ShopGoodsListVo> iPage = shopGoodsMapper.selectJoinPage(new Page<>(page, limit), ShopGoodsListVo.class, queryWrapper);
        List<ShopGoodsListVo> list = new LinkedList<>();
        for (ShopGoodsListVo vo : iPage.getRecords()) {
            ShopGoodsSku goodsSku = shopGoodsSkuMapper.selectOne(new QueryWrapper<ShopGoodsSku>()
                    .eq("goods_id", vo.getGoodsId()).eq("is_default", 1));
            ShopGoodsListVo.GoodsSku goodsSkuVo = new ShopGoodsListVo.GoodsSku();
            BeanUtils.copyProperties(goodsSku, goodsSkuVo);
            if (memberLevel != null) {
                JSONObject memberPriceConfig = ObjectUtil.isNotEmpty(goodsSku.getMemberPrice()) ? JSONUtil.parseObj(goodsSku.getMemberPrice()) : new JSONObject();
                goodsSkuVo.setMemberPrice(calculateMemberPrice(memberLevel, vo.getMemberDiscount(), memberPriceConfig, goodsSku.getPrice()));
            } else {
                goodsSkuVo.setMemberPrice(goodsSku.getPrice());
            }
            vo.setGoodsSku(goodsSkuVo);
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
        if (ObjectUtil.hasEmpty(memberLevel, calculateType, config)) {
            return price;
        }

        // 按会员等级折扣
        if (calculateType.equals("discount")) {
            JSONObject benefits = memberLevel.getLevelBenefits();
            if (benefits != null && !benefits.isEmpty()) {
                Integer isUse = ObjectUtil.defaultIfNull(benefits.getByPath("discount.is_use", Integer.class), 0);
                Double discount = ObjectUtil.defaultIfNull(benefits.getByPath("discount.discount", Double.class), 0.0);
                if (isUse > 0 && discount > 0) {
                    return price.multiply(new BigDecimal(Double.valueOf(discount / 10)));
                }
            }
        } else if (calculateType.equals("fixed_price")) {
            // 按指定价格
            if (config.getStr("level_" + memberLevel.getLevelId()) != null) {
                return new BigDecimal(Double.valueOf(config.getStr("level_" + memberLevel.getLevelId())));
            }
        }
        return price;
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
                .innerJoin("?_shop_goods_sku gs ON g.goods_id = gs.goods_id AND gs.is_default = 1".replace("?_", GlobalConfig.tablePrefix));
        queryWrapper.select("g.goods_id,g.site_id,goods_name,goods_type,goods_cover,g.stock,g.sale_num + g.virtual_sale_num as sale_num,status,sort,create_time,member_discount,virtual_verify_type,unit,is_discount");
        queryWrapper.eq("g.site_id", RequestUtils.siteId());
        queryWrapper.eq("g.status", 1);
        queryWrapper.eq("delete_time", 0);
        queryWrapper.last("limit " + param.getNum());
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

        MemberLevelInfoVo memberLevel = null;
        if (ObjectUtil.isNotEmpty(RequestUtils.memberId()))
            memberLevel = memberLevelService.findMemberLevel(RequestUtils.memberId());

        List<ShopGoodsListVo> list = shopGoodsMapper.selectJoinList(ShopGoodsListVo.class, queryWrapper);
        for (ShopGoodsListVo vo : list) {
            ShopGoodsSku goodsSku = shopGoodsSkuMapper.selectOne(new QueryWrapper<ShopGoodsSku>()
                    .eq("goods_id", vo.getGoodsId()).eq("is_default", 1));
            ShopGoodsListVo.GoodsSku goodsSkuVo = new ShopGoodsListVo.GoodsSku();
            BeanUtils.copyProperties(goodsSku, goodsSkuVo);
            if (memberLevel != null) {
                JSONObject memberPriceConfig = ObjectUtil.isNotEmpty(goodsSku.getMemberPrice()) ? JSONUtil.parseObj(goodsSku.getMemberPrice()) : new JSONObject();
                goodsSkuVo.setMemberPrice(calculateMemberPrice(memberLevel, vo.getMemberDiscount(), memberPriceConfig, goodsSku.getPrice()));
            } else {
                goodsSkuVo.setMemberPrice(goodsSku.getPrice());
            }
            vo.setGoodsSku(goodsSkuVo);
        }
        return list;
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

        // 查询商品
        ShopGoods goods = shopGoodsMapper.selectOne(new QueryWrapper<ShopGoods>().eq("goods_id", sku.getGoodsId()).eq("delete_time", 0));
        if (goods == null) return null;
        ShopGoodsDetailVo.Goods goodsVo = new ShopGoodsDetailVo.Goods();
        BeanUtils.copyProperties(goods, goodsVo);
        goodsVo.setSaleNum(goods.getSaleNum() + goods.getVirtualSaleNum());

        vo.setType(param.getType());
        vo.setTypeName(ActiveTypeEnum.getNameByType(param.getType()));

        // 是否已收藏
        if (RequestUtils.memberId() != null) {
            Long goodsCollect = shopGoodsCollectMapper.selectCount(new QueryWrapper<ShopGoodsCollect>().eq("goods_id", goodsVo.getGoodsId()).eq("member_id", RequestUtils.memberId()));
            goodsVo.setIsCollect(goodsCollect == 0 ? 0 : 1);
        }

        vo.setGoods(goodsVo);

        // 查询会员的会员等级
        MemberLevelInfoVo memberLevel = null;
        if (ObjectUtil.isNotEmpty(RequestUtils.memberId()))
            memberLevel = memberLevelService.findMemberLevel(RequestUtils.memberId());

        if (memberLevel != null) {
            JSONObject memberPriceConfig = ObjectUtil.isNotEmpty(sku.getMemberPrice()) ? JSONUtil.parseObj(sku.getMemberPrice()) : new JSONObject();
            vo.setMemberPrice(calculateMemberPrice(memberLevel, goods.getMemberDiscount(), memberPriceConfig, sku.getPrice()));
        } else {
            vo.setMemberPrice(sku.getPrice());
        }

        // 查询商品SKU列表
        List<ShopGoodsDetailVo.Sku> skuListVo = new LinkedList<>();
        List<ShopGoodsSku> skuList = shopGoodsSkuMapper.selectList(new QueryWrapper<ShopGoodsSku>().eq("goods_id", sku.getGoodsId()));
        for (ShopGoodsSku item : skuList) {
            ShopGoodsDetailVo.Sku skuVo = new ShopGoodsDetailVo.Sku();
            BeanUtils.copyProperties(item, skuVo);
            if (memberLevel != null) {
                JSONObject memberPriceConfig = ObjectUtil.isNotEmpty(item.getMemberPrice()) ? JSONUtil.parseObj(item.getMemberPrice()) : new JSONObject();
                skuVo.setMemberPrice(calculateMemberPrice(memberLevel, goods.getMemberDiscount(), memberPriceConfig, item.getPrice()));
            } else {
                skuVo.setMemberPrice(sku.getPrice());
            }
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
        if (!goodsVo.getLabelIds().isEmpty()) {
            JSONArray labelIds = JSONUtil.parseArray(goodsVo.getLabelIds());
            if (!labelIds.isEmpty()) {
                List<ShopGoodsDetailVo.Label> labels = CollectUtils.convert(shopGoodsLabelMapper.selectList(new QueryWrapper<ShopGoodsLabel>().in("label_id", labelIds)), ShopGoodsDetailVo.Label.class);
                vo.setLabelInfo(labels);
            }
        }

        // 商品品牌
        if (goodsVo.getBrandId() > 0) {
            ShopGoodsBrand brand = shopGoodsBrandMapper.selectOne(new QueryWrapper<ShopGoodsBrand>().eq("brand_id", goodsVo.getBrandId()));
            ShopGoodsDetailVo.Brand brandVo = new ShopGoodsDetailVo.Brand();
            BeanUtils.copyProperties(brand, brandVo);
            vo.setBrandInfo(brandVo);
        }

        ShopOrderEvaluate orderEvaluate = coreOrderConfigService.getEvaluateConfig(RequestUtils.siteId());
        vo.setEvaluateIsShow(orderEvaluate.getEvaluateIsShow());

        // 限时折扣
        if (goods.getIsDiscount().equals(1)) {
            vo.setDiscountInfo(discountService.getInfoByGoods(goodsVo.getGoodsId()));
        }

        if (ActiveClassEnum.NEWCOMER_DISCOUNT.getType().equals(param.getType())) {
            if (shopNewcomerService.checkIfNewcomer()) {
                ShopNewcomerInfoVo newcomerInfo = shopNewcomerService.getNewcomerInfo(vo.getGoodsId(), vo.getSkuId());
                if (ObjectUtil.isNotEmpty(newcomerInfo)) {
                    vo.setNewcomerPrice(newcomerInfo.getNewcomerPrice().toString());
                    vo.setNewcomerDesc(newcomerInfo.getNewcomerDesc());
                    vo.setIsNewcomer(1);
                }

                vo.setSkuList(shopNewcomerService.getNewcomerPriceByList(vo.getSkuList()));
            } else {
                vo.setIsNewcomer(0);
                vo.setType("");
                vo.setTypeName("");
            }
        }

        return vo;
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

        // 查询会员的会员等级
        MemberLevelInfoVo memberLevel = null;
        if (ObjectUtil.isNotEmpty(RequestUtils.memberId()))
            memberLevel = memberLevelService.findMemberLevel(RequestUtils.memberId());

        if (memberLevel != null) {
            JSONObject memberPriceConfig = ObjectUtil.isNotEmpty(sku.getMemberPrice()) ? JSONUtil.parseObj(sku.getMemberPrice()) : new JSONObject();
            vo.setMemberPrice(calculateMemberPrice(memberLevel, goods.getMemberDiscount(), memberPriceConfig, sku.getPrice()));
        } else {
            vo.setMemberPrice(sku.getPrice());
        }

        // 查询商品SKU列表
        List<ShopGoodsSkuVo.Sku> skuListVo = new LinkedList<>();
        List<ShopGoodsSku> skuList = shopGoodsSkuMapper.selectList(new QueryWrapper<ShopGoodsSku>().eq("goods_id", sku.getGoodsId()));
        for (ShopGoodsSku item : skuList) {
            ShopGoodsSkuVo.Sku skuVo = new ShopGoodsSkuVo.Sku();
            BeanUtils.copyProperties(item, skuVo);
            if (memberLevel != null) {
                JSONObject memberPriceConfig = ObjectUtil.isNotEmpty(item.getMemberPrice()) ? JSONUtil.parseObj(item.getMemberPrice()) : new JSONObject();
                skuVo.setMemberPrice(calculateMemberPrice(memberLevel, goods.getMemberDiscount(), memberPriceConfig, item.getPrice()));
            } else {
                skuVo.setMemberPrice(sku.getPrice());
            }
            skuListVo.add(skuVo);
        }
        vo.setSkuList(skuListVo);

        // 商品规格
        List<ShopGoodsSkuVo.Spec> specList = CollectUtils.convert(shopGoodsSpecMapper.selectList(new QueryWrapper<ShopGoodsSpec>().eq("goods_id", sku.getGoodsId())), ShopGoodsSkuVo.Spec.class);
        vo.setGoodsSpec(specList);

        return vo;
    }
}
