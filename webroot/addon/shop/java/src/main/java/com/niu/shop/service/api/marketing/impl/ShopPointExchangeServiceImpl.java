package com.niu.shop.service.api.marketing.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.utils.CommonUtils;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.mapper.QueryMapperUtils;
import com.niu.core.entity.member.Member;
import com.niu.core.entity.member.MemberAccountLog;
import com.niu.core.enums.member.AccountTypeEnum;
import com.niu.core.mapper.member.MemberAccountLogMapper;
import com.niu.core.mapper.member.MemberMapper;
import com.niu.shop.entity.exchange.ShopPointExchange;
import com.niu.shop.enums.goods.GoodsTypeEnum;
import com.niu.shop.enums.marketing.ShopPointExchangeStatusEnum;
import com.niu.shop.enums.marketing.ShopPointExchangeTypeEnum;
import com.niu.shop.mapper.exchange.ShopPointExchangeMapper;
import com.niu.shop.service.api.goods.IGoodsService;
import com.niu.shop.service.api.goods.param.ShopGoodsDetailParam;
import com.niu.shop.service.api.goods.vo.ShopGoodsDetailVo;
import com.niu.shop.service.api.marketing.IShopPointExchangeService;
import com.niu.shop.service.api.marketing.param.ShopGoodsComponentSearchParam;
import com.niu.shop.service.api.marketing.param.ShopPointExchangeSearchParam;
import com.niu.shop.service.api.marketing.vo.ShopPointExchangeInfoVo;
import com.niu.shop.service.api.marketing.vo.ShopPointExchangeListVo;
import com.niu.shop.service.core.marketing.vo.ProductDetail;
import com.niu.shop.service.core.order.param.OrderCalculateParam;
import com.niu.shop.service.core.order.param.OrderCreateParam;
import com.niu.shop.service.core.order.vo.OrderCreateResultVo;
import com.niu.shop.service.core.pointexchange.ICoreOrderCreateService;
import com.niu.shop.service.core.pointexchange.vo.OrderCreateDataVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ShopPointExchangeServiceImpl implements IShopPointExchangeService {

    private static final List<String> PRESETS_ORDER_COLUMN = List.of("total_order_num", "total_exchange_num", "price");
    @Resource
    IGoodsService goodsService;
    @Resource
    MemberAccountLogMapper memberAccountLogMapper;
    @Resource
    ICoreOrderCreateService orderCreateService;
    @Resource
    MemberMapper memberMapper;
    @Resource
    private ShopPointExchangeMapper shopPointExchangeMapper;

    @Override
    public PageResult<ShopPointExchangeListVo> list(PageParam pageParam, ShopPointExchangeSearchParam searchParam) {
        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();

        QueryWrapper<ShopPointExchange> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("site_id", RequestUtils.siteId())
                .eq("status", 1);
        if (ObjectUtil.isNotEmpty(searchParam.getStatus())) queryWrapper.eq("status", searchParam.getStatus());
        if (ObjectUtil.isNotEmpty(searchParam.getNames()))
            queryWrapper.like("names", "%" + searchParam.getNames() + "%");

        if (CollectionUtil.isNotEmpty(searchParam.getCreateTime())) {
            QueryMapperUtils.buildByTime(queryWrapper, "create_time", searchParam.getCreateTime().toArray(new String[0]));
        }

        boolean isAsc = "asc".equals(searchParam.getSort());
        boolean isAcceptColumn = PRESETS_ORDER_COLUMN.contains(searchParam.getOrder());
        queryWrapper.orderBy(true, isAcceptColumn && isAsc, List.of(isAcceptColumn ? searchParam.getOrder() : "total_order_num", "sort"));

        IPage<ShopPointExchange> iPage = shopPointExchangeMapper.selectPage(new Page<>(page, limit), queryWrapper);
        List<ShopPointExchangeListVo> list = new ArrayList<>(limit);
        for (ShopPointExchange item : iPage.getRecords()) {
            ShopPointExchangeListVo vo = new ShopPointExchangeListVo();
            BeanUtils.copyProperties(item, vo);
            vo.setType(ShopPointExchangeTypeEnum.getNameByType(item.getType()));
            vo.setStatusName(ShopPointExchangeStatusEnum.getNameByStatus(item.getStatus()));
            if (ObjectUtil.isNotEmpty(item.getImage())) {
                String image = item.getImage().split(",")[0];
                String coverBig = CommonUtils.thumbImageBig(RequestUtils.siteId(), image);
                vo.setGoodsCoverThumbBig(coverBig);
                String coverMid = CommonUtils.thumbImageMiddle(RequestUtils.siteId(), image);
                vo.setGoodsCoverThumbMid(coverMid);

                String coverSmall = CommonUtils.thumbImageSmall(RequestUtils.siteId(), image);
                vo.setGoodsCoverThumbSmall(coverSmall);
            }
            list.add(vo);
        }

        return PageResult.build(iPage, list);
    }

    @Override
    public ShopPointExchangeInfoVo info(Integer id) {
        QueryWrapper<ShopPointExchange> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id);
        queryWrapper.eq("site_id", RequestUtils.siteId());
        queryWrapper.select("total_exchange_num,id,site_id,type,names,title,image,status,product_detail,point,price,limit_num,content,sort,total_point_num,total_price_num,total_order_num,total_member_num,update_time,create_time");

        ShopPointExchange shopPointExchange = shopPointExchangeMapper.selectOne(queryWrapper);
        if (shopPointExchange == null) {
            return null;
        }

        ShopGoodsDetailParam param = new ShopGoodsDetailParam();
        List<ProductDetail> productDetails = JSONArray.parseArray(shopPointExchange.getProductDetail(), ProductDetail.class);

        ProductDetail productDetail = productDetails.get(0);
        param.setGoodsId(productDetail.getGoodsId());
        param.setSkuId(productDetail.getSkuId());

        ShopGoodsDetailVo shopGoodsDetailVo = goodsService.detail(param);

        // 最简单的详情信息
        ShopPointExchangeInfoVo shopPointExchangeInfoVo = new ShopPointExchangeInfoVo();
        String small = CommonUtils.thumbImageSmall(RequestUtils.siteId(), shopGoodsDetailVo.getSkuImage());
        String mid = CommonUtils.thumbImageMiddle(RequestUtils.siteId(), shopGoodsDetailVo.getSkuImage());
        String big = CommonUtils.thumbImageBig(RequestUtils.siteId(), shopGoodsDetailVo.getSkuImage());
        shopPointExchangeInfoVo.setSkuImageThumbSmall(small);
        shopPointExchangeInfoVo.setSkuImageThumbMid(mid);
        shopPointExchangeInfoVo.setSkuImageThumbBig(big);
        shopPointExchangeInfoVo.setSkuId(shopGoodsDetailVo.getSkuId());
        shopPointExchangeInfoVo.setSkuName(shopGoodsDetailVo.getSkuName());
        shopPointExchangeInfoVo.setSkuImage(shopGoodsDetailVo.getSkuImage());
        shopPointExchangeInfoVo.setSkuNo(shopGoodsDetailVo.getSkuNo());
        shopPointExchangeInfoVo.setGoodsId(shopGoodsDetailVo.getGoodsId());
        shopPointExchangeInfoVo.setSiteId(shopGoodsDetailVo.getSiteId());
        shopPointExchangeInfoVo.setSkuSpecFormat(shopGoodsDetailVo.getSkuSpecFormat());
        shopPointExchangeInfoVo.setPrice(productDetail.getPrice());
        shopPointExchangeInfoVo.setMarketPrice(shopGoodsDetailVo.getMarketPrice().toString());
        shopPointExchangeInfoVo.setSalePrice(productDetail.getPrice());
        shopPointExchangeInfoVo.setStock(shopGoodsDetailVo.getStock().toString());
        shopPointExchangeInfoVo.setWeight(shopGoodsDetailVo.getWeight().toString());
        shopPointExchangeInfoVo.setVolume(shopGoodsDetailVo.getVolume().toString());
        shopPointExchangeInfoVo.setSaleNum(shopGoodsDetailVo.getSaleNum());
        shopPointExchangeInfoVo.setIsDefault(shopGoodsDetailVo.getIsDefault());
        shopPointExchangeInfoVo.setType(shopPointExchange.getType());
        shopPointExchangeInfoVo.setTypeName(ShopPointExchangeTypeEnum.getNameByType(shopPointExchange.getType()));
        shopPointExchangeInfoVo.setEvaluateIsShow(shopGoodsDetailVo.getEvaluateIsShow());
        shopPointExchangeInfoVo.setPoint(shopPointExchange.getPoint().toString());
        shopPointExchangeInfoVo.setExchangeId(id);
        shopPointExchangeInfoVo.setGoodsSpec(shopGoodsDetailVo.getGoodsSpec());

        // goods
        ShopPointExchangeInfoVo.GoodsDTO goodsDTO = new ShopPointExchangeInfoVo.GoodsDTO();
        goodsDTO.setGoodsTypeName(GoodsTypeEnum.getNameByType(shopGoodsDetailVo.getGoods().getGoodsType()));
        goodsDTO.setGoodsCoverThumbBig(shopGoodsDetailVo.getGoods().getGoodsCoverThumbBig());
        goodsDTO.setGoodsCoverThumbSmall(shopGoodsDetailVo.getGoods().getGoodsCoverThumbSmall());
        goodsDTO.setGoodsCoverThumbMid(shopGoodsDetailVo.getGoods().getGoodsCoverThumbMid());
        goodsDTO.setGoodsImage(shopGoodsDetailVo.getGoods().getGoodsImage());
        goodsDTO.setGoodsImageThumbSmall(shopGoodsDetailVo.getGoods().getGoodsImageThumbSmall());
        goodsDTO.setGoodsImageThumbMid(shopGoodsDetailVo.getGoods().getGoodsImageThumbMid());
        goodsDTO.setGoodsImageThumbBig(shopGoodsDetailVo.getGoods().getGoodsImageThumbBig());
        goodsDTO.setGoodsDesc(shopPointExchange.getContent());
        goodsDTO.setBrandId(shopGoodsDetailVo.getGoods().getBrandId());
        goodsDTO.setUnit(shopGoodsDetailVo.getGoods().getUnit());
        goodsDTO.setStock(shopGoodsDetailVo.getGoods().getStock());
        goodsDTO.setSaleNum(shopGoodsDetailVo.getGoods().getSaleNum());
        goodsDTO.setStatus(shopGoodsDetailVo.getGoods().getStatus());
        goodsDTO.setIsFreeShipping(shopGoodsDetailVo.getGoods().getIsFreeShipping());
        goodsDTO.setFeeType(shopGoodsDetailVo.getGoods().getFeeType());
        goodsDTO.setDeliveryMoney(shopGoodsDetailVo.getGoods().getDeliveryMoney().toString());
        goodsDTO.setDeliveryTemplateId(shopGoodsDetailVo.getGoods().getDeliveryTemplateId());
        goodsDTO.setAttrId(shopGoodsDetailVo.getGoods().getAttrId());
        goodsDTO.setAttrFormat(JSONUtil.toJsonStr(shopGoodsDetailVo.getGoods().getAttrFormat()));
        goodsDTO.setMemberDiscount(shopGoodsDetailVo.getGoods().getMemberDiscount());
        goodsDTO.setPosterId(shopGoodsDetailVo.getGoods().getPosterId());
        goodsDTO.setVirtualReceiveType(shopGoodsDetailVo.getGoods().getVirtualReceiveType());
        goodsDTO.setIsCollect(shopGoodsDetailVo.getGoods().getIsCollect());
        goodsDTO.setGoodsId(shopGoodsDetailVo.getGoodsId());
        goodsDTO.setIsDiscount(shopGoodsDetailVo.getGoods().getIsDiscount());
        goodsDTO.setSiteId(shopGoodsDetailVo.getGoods().getSiteId());
        goodsDTO.setGoodsName(shopGoodsDetailVo.getGoods().getGoodsName());
        goodsDTO.setGoodsType(shopGoodsDetailVo.getGoods().getGoodsType());
        goodsDTO.setSubTitle(shopGoodsDetailVo.getGoods().getSubTitle());
        goodsDTO.setGoodsCover(shopGoodsDetailVo.getGoods().getGoodsCover());

        List<String> labelIds = JSON.parseObject(shopGoodsDetailVo.getGoods().getLabelIds(), new TypeReference<List<String>>() {
        });
        goodsDTO.setLabelIds(labelIds);

        List<String> serviceIds = JSON.parseObject(shopGoodsDetailVo.getGoods().getServiceIds(), new TypeReference<List<String>>() {
        });
        goodsDTO.setServiceIds(serviceIds);

        List<String> deliveryTypes = JSON.parseObject(shopGoodsDetailVo.getGoods().getDeliveryType(), new TypeReference<List<String>>() {
        });
        goodsDTO.setDeliveryType(deliveryTypes);

        List<Integer> goodsCategories = JSON.parseObject(shopGoodsDetailVo.getGoods().getGoodsCategory(), new TypeReference<List<Integer>>() {
        });
        goodsDTO.setGoodsCategory(goodsCategories);

        // goods brand
        if (shopGoodsDetailVo.getBrandInfo() != null) {
            ShopPointExchangeInfoVo.GoodsDTO.GoodsBrandDTO goodsBrandDTO = new ShopPointExchangeInfoVo.GoodsDTO.GoodsBrandDTO();
            goodsBrandDTO.setBrandId(shopGoodsDetailVo.getBrandInfo().getBrandId());
            goodsBrandDTO.setBrandName(shopGoodsDetailVo.getBrandInfo().getBrandName());
            goodsBrandDTO.setLogo(shopGoodsDetailVo.getBrandInfo().getLogo());
            goodsDTO.setGoodsBrand(goodsBrandDTO);
        }

        shopPointExchangeInfoVo.setGoods(goodsDTO);

        // sku数据
        List<ShopPointExchangeInfoVo.SkuListDTO> skuData = shopGoodsDetailVo.getSkuList().stream().map(sku -> {
            ShopPointExchangeInfoVo.SkuListDTO skuListDTO = new ShopPointExchangeInfoVo.SkuListDTO();
            skuListDTO.setSkuId(sku.getSkuId());
            skuListDTO.setSiteId(sku.getSiteId());
            skuListDTO.setSkuName(sku.getSkuName());
            skuListDTO.setSkuImage(sku.getSkuImage());
            skuListDTO.setSkuNo(sku.getSkuNo());
            skuListDTO.setGoodsId(sku.getGoodsId());
            skuListDTO.setSkuSpecFormat(sku.getSkuSpecFormat());
            skuListDTO.setPrice(sku.getPrice().toString());
            skuListDTO.setMarketPrice(sku.getMarketPrice().toString());
            skuListDTO.setStock(sku.getStock().toString());
            skuListDTO.setWeight(sku.getWeight().toString());
            skuListDTO.setVolume(sku.getVolume().toString());
            skuListDTO.setIsDefault(sku.getIsDefault());
            skuListDTO.setLimitNum(ObjectUtil.isEmpty(sku.getLimitNum()) ? "0" : sku.getLimitNum().toString());
            skuListDTO.setPoint(sku.getPoint() == null ? "0" : sku.getPoint().toString());

            Optional<ProductDetail> productDetailOptional = productDetails.stream()
                    .filter(detail -> detail.getSkuId().equals(sku.getSkuId()))
                    .findFirst();
            productDetailOptional.ifPresent(detail -> {
                BeanUtils.copyProperties(detail, skuListDTO);
                skuListDTO.setSalePrice(detail.getPrice());
                skuListDTO.setIsJoinExchange(1);
            });

            return skuListDTO;
        }).collect(Collectors.toList());
        shopPointExchangeInfoVo.setSkuList(skuData);

        // service
        List<ShopPointExchangeInfoVo.ServiceDTO> services = shopGoodsDetailVo.getService().stream().map(service -> {
            ShopPointExchangeInfoVo.ServiceDTO serviceDTO = new ShopPointExchangeInfoVo.ServiceDTO();
            serviceDTO.setServiceId(service.getServiceId());
            serviceDTO.setServiceName(service.getServiceName());
            serviceDTO.setImage(service.getImage());
            serviceDTO.setDesc(service.getDesc());
            return serviceDTO;
        }).collect(Collectors.toList());
        shopPointExchangeInfoVo.setService(services);

        // label
        List<ShopPointExchangeInfoVo.LabelInfoDTO> labels = shopGoodsDetailVo.getLabelInfo().stream().map(label -> {
            ShopPointExchangeInfoVo.LabelInfoDTO labelInfoDTO = new ShopPointExchangeInfoVo.LabelInfoDTO();
            labelInfoDTO.setLabelId(label.getLabelId());
            labelInfoDTO.setLabelName(label.getLabelName());
            labelInfoDTO.setMemo(label.getMemo());
            labelInfoDTO.setStyleType(label.getStyleType());
            labelInfoDTO.setIcon(label.getIcon());
            if (ObjectUtil.isNotEmpty(label.getColorJson())) {
                labelInfoDTO.setColorJson(JSONUtil.toBean(JSONUtil.parseObj(label.getColorJson()), ShopPointExchangeInfoVo.LabelInfoDTO.ColorJsonDTO.class));
            }
            return labelInfoDTO;
        }).collect(Collectors.toList());
        shopPointExchangeInfoVo.setLabelInfo(labels);

        return shopPointExchangeInfoVo;
    }

    @Override
    public List<ShopPointExchangeListVo> component(ShopGoodsComponentSearchParam searchParam) {
        QueryWrapper<ShopPointExchange> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("site_id", RequestUtils.siteId());

        boolean isAsc = "asc".equals(searchParam.getSort());
        boolean isAcceptColumn = PRESETS_ORDER_COLUMN.contains(searchParam.getOrder());
        queryWrapper.orderBy(true, isAcceptColumn && isAsc, List.of(isAcceptColumn ? searchParam.getOrder() : "total_order_num", "sort"));

        if (ObjectUtil.isNotEmpty(searchParam.getNames())) {
            queryWrapper.like("names", searchParam.getNames());
        }
        if (ObjectUtil.isNotEmpty(searchParam.getIds())) {
            queryWrapper.in("id", searchParam.getIds());
        }
        if (ObjectUtil.isNotEmpty(searchParam.getNum())) {
            queryWrapper.last("limit " + searchParam.getNum());
        }

        queryWrapper.eq("status", 1);
        queryWrapper.select("status,total_exchange_num,stock,id,site_id,type,names,title,image,status,product_detail,point,price,limit_num,content,sort,total_point_num,total_price_num,total_order_num,total_member_num,update_time,create_time");

        return shopPointExchangeMapper.selectList(queryWrapper)
                .stream()
                .map(shopPointExchange -> {
                    ShopPointExchangeListVo shopPointExchangeListVo = new ShopPointExchangeListVo();
                    BeanUtils.copyProperties(shopPointExchange, shopPointExchangeListVo);

                    shopPointExchangeListVo.setTypeName(ShopPointExchangeTypeEnum.getNameByType(shopPointExchange.getType()));
                    shopPointExchangeListVo.setStatusName(ShopPointExchangeStatusEnum.getNameByStatus(shopPointExchange.getStatus()));

                    if (ObjectUtil.isNotEmpty(shopPointExchange.getImage())) {
                        String image = shopPointExchange.getImage().split(",")[0];
                        String coverBig = CommonUtils.thumbImageBig(RequestUtils.siteId(), image);
                        shopPointExchangeListVo.setGoodsCoverThumbBig(coverBig);

                        String coverMid = CommonUtils.thumbImageMiddle(RequestUtils.siteId(), image);
                        shopPointExchangeListVo.setGoodsCoverThumbMid(coverMid);

                        String coverSmall = CommonUtils.thumbImageSmall(RequestUtils.siteId(), image);
                        shopPointExchangeListVo.setGoodsCoverThumbSmall(coverSmall);
                    }

                    return shopPointExchangeListVo;
                }).collect(Collectors.toList());
    }

    @Override
    public Map<String, Integer> getPointInfo() {
        QueryWrapper<Member> memberQueryWrapper = new QueryWrapper<>();
        memberQueryWrapper.eq("site_id", RequestUtils.siteId());
        memberQueryWrapper.eq("member_id", RequestUtils.memberId());

        Map<String, Integer> map = new HashMap<>();
        Member member = memberMapper.selectOne(memberQueryWrapper);
        map.put("point", member == null ? 0 : member.getPoint());

        QueryWrapper<MemberAccountLog> memberAccountLogQueryWrapper = new QueryWrapper<>();
        memberAccountLogQueryWrapper.eq("member_id", RequestUtils.memberId());
        memberAccountLogQueryWrapper.eq("site_id", RequestUtils.siteId());
        memberAccountLogQueryWrapper.eq("account_type", AccountTypeEnum.POINT.getType());
        memberAccountLogQueryWrapper.gt("account_data", 0);

        LocalDateTime begin = LocalDateTimeUtil.beginOfDay(LocalDateTime.now());
        LocalDateTime end = LocalDateTimeUtil.endOfDay(LocalDateTime.now());
        memberAccountLogQueryWrapper.between("create_time", begin.toEpochSecond(ZoneOffset.UTC), end.toEpochSecond(ZoneOffset.UTC));

        List<MemberAccountLog> memberAccountLogs = memberAccountLogMapper.selectList(memberAccountLogQueryWrapper);
        map.put("today_point", ObjectUtil.isEmpty(memberAccountLogs) ? 0 : memberAccountLogs.stream().mapToInt(log -> log.getAccountData().intValue()).sum());
        return map;
    }

    /**
     * 订单计算
     *
     * @param param
     * @return
     */
    @Override
    public OrderCreateDataVo calculate(OrderCalculateParam param) {
        param.setSiteId(RequestUtils.siteId());
        param.setMemberId(RequestUtils.memberId());
        param.setMemberId(RequestUtils.memberId());
        param.setOrderFrom(RequestUtils.channel());
        return orderCreateService.calculate(param);
    }

    /**
     * 订单创建
     *
     * @param param
     */
    @Override
    public OrderCreateResultVo createOrder(OrderCreateParam param) {
        return orderCreateService.create(param);
    }
}
