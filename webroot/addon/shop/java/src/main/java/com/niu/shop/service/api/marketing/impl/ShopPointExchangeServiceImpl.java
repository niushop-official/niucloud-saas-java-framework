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
import com.niu.core.common.utils.CollectUtils;
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
        Map<Integer, ProductDetail> productDetailsMap = productDetails.stream().collect(Collectors.toMap(ProductDetail::getSkuId, item -> item));
        List<Integer> skuIds = productDetails.stream().map(ProductDetail::getSkuId).collect(Collectors.toList());

        ProductDetail productDetail = productDetails.get(0);
        param.setGoodsId(productDetail.getGoodsId());
        param.setSkuId(productDetail.getSkuId());

        ShopGoodsDetailVo shopGoodsDetailVo = goodsService.detail(param);

        ShopPointExchangeInfoVo shopPointExchangeInfoVo = new ShopPointExchangeInfoVo();
        BeanUtils.copyProperties(shopGoodsDetailVo, shopPointExchangeInfoVo);

        ShopPointExchangeInfoVo.Goods goods = new ShopPointExchangeInfoVo.Goods();
        BeanUtils.copyProperties(shopGoodsDetailVo.getGoods(), goods);
        shopPointExchangeInfoVo.setGoods(goods);

        List<ShopPointExchangeInfoVo.Spec> spec = CollectUtils.convert(shopGoodsDetailVo.getGoodsSpec(), ShopPointExchangeInfoVo.Spec.class);
        shopPointExchangeInfoVo.setGoodsSpec(spec);

        List<ShopPointExchangeInfoVo.Service> service = CollectUtils.convert(shopGoodsDetailVo.getService(), ShopPointExchangeInfoVo.Service.class);
        shopPointExchangeInfoVo.setService(service);

        if (ObjectUtil.isNotEmpty(shopGoodsDetailVo.getBrandInfo())) {
            ShopPointExchangeInfoVo.Brand brandInfo = new ShopPointExchangeInfoVo.Brand();
            BeanUtils.copyProperties(shopGoodsDetailVo.getBrandInfo(), brandInfo);
            shopPointExchangeInfoVo.setBrandInfo(brandInfo);
        }

        shopPointExchangeInfoVo.setSaleNum(shopPointExchange.getTotalExchangeNum());
        shopPointExchangeInfoVo.setPoint(productDetails.get(0).getPoint());
        shopPointExchangeInfoVo.setPrice(productDetails.get(0).getPrice());
        shopPointExchangeInfoVo.setStock(productDetails.get(0).getStock());
        shopPointExchangeInfoVo.setExchangeId(shopPointExchange.getId());

        List<ShopPointExchangeInfoVo.Sku> skuList = new LinkedList<>();
        shopGoodsDetailVo.getSkuList().forEach(item -> {
            if (skuIds.contains(item.getSkuId())) {
                ShopPointExchangeInfoVo.Sku sku = new ShopPointExchangeInfoVo.Sku();
                BeanUtils.copyProperties(item, sku);
                sku.setPoint(productDetailsMap.get(item.getSkuId()).getPoint());
                sku.setPrice(productDetailsMap.get(item.getSkuId()).getPrice());
                sku.setStock(productDetailsMap.get(item.getSkuId()).getStock());
                sku.setIsDefault(item.getSkuId() == productDetails.get(0).getSkuId() ? 1 : 0);
                sku.setLimitNum(productDetailsMap.get(item.getSkuId()).getLimitNum());
                skuList.add(sku);
            }
        });
        shopPointExchangeInfoVo.setSkuList(skuList);

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
            queryWrapper.in("id", searchParam.getIds().split(","));
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
