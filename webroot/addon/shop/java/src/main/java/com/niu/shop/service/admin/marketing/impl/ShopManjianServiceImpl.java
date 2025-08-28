package com.niu.shop.service.admin.marketing.impl;

import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.niu.core.common.config.GlobalConfig;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.exception.AdminException;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.CommonUtils;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.date.DateUtils;
import com.niu.core.common.utils.mapper.QueryMapperUtils;
import com.niu.core.entity.member.Member;
import com.niu.core.enums.upload.UploadThumbTypeEnum;
import com.niu.core.mapper.member.MemberMapper;
import com.niu.core.service.core.sys.ICoreUploadService;
import com.niu.shop.entity.coupon.ShopCoupon;
import com.niu.shop.entity.goods.ShopGoods;
import com.niu.shop.entity.goods.ShopGoodsSku;
import com.niu.shop.entity.manjian.ShopManjian;
import com.niu.shop.entity.manjian.ShopManjianGiveRecords;
import com.niu.shop.entity.manjian.ShopManjianGoods;
import com.niu.shop.entity.order.ShopOrder;
import com.niu.shop.enums.marketing.ShopManjianCouponType;
import com.niu.shop.enums.marketing.ShopManjianGoodsTypeEnum;
import com.niu.shop.enums.marketing.ShopManjianStatusEnum;
import com.niu.shop.mapper.coupon.ShopCouponMapper;
import com.niu.shop.mapper.goods.ShopGoodsMapper;
import com.niu.shop.mapper.goods.ShopGoodsSkuMapper;
import com.niu.shop.mapper.manjian.ShopManjianGiveRecordsMapper;
import com.niu.shop.mapper.manjian.ShopManjianGoodsMapper;
import com.niu.shop.mapper.manjian.ShopManjianMapper;
import com.niu.shop.mapper.order.ShopOrderMapper;
import com.niu.shop.service.admin.marketing.IShopManjianService;
import com.niu.shop.service.admin.marketing.param.ShopManjianBatchParam;
import com.niu.shop.service.admin.marketing.param.ShopManjianCheckGoodsParam;
import com.niu.shop.service.admin.marketing.param.ShopManjianInitParam;
import com.niu.shop.service.admin.marketing.vo.ShopManjianInitVo;
import com.niu.shop.service.admin.marketing.vo.ShopManjianMemberVo;
import com.niu.shop.service.core.marketing.ICoreManjianService;
import com.niu.shop.service.core.marketing.ICoreShopManjianGoodsService;
import com.niu.shop.service.core.marketing.model.ShopManjianCoupon;
import com.niu.shop.service.core.marketing.model.ShopManjianGoodsData;
import com.niu.shop.service.core.marketing.model.ShopManjianRule;
import com.niu.shop.service.core.marketing.param.ShopManjianParam;
import com.niu.shop.service.core.marketing.param.ShopManjianSearchParam;
import com.niu.shop.service.core.marketing.vo.ShopManjianInfoVo;
import com.niu.shop.service.core.marketing.vo.ShopManjianListVo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 满减活动实现
 */
@Service
@RequiredArgsConstructor
public class ShopManjianServiceImpl implements IShopManjianService {
    private final MemberMapper memberMapper;
    private final ShopGoodsMapper shopGoodsMapper;
    private final ShopOrderMapper shopOrderMapper;
    private final ShopCouponMapper shopCouponMapper;
    private final ShopManjianMapper shopManjianMapper;
    private final ICoreUploadService coreUploadService;
    private final ShopGoodsSkuMapper shopGoodsSkuMapper;
    private final ShopManjianGoodsMapper shopManjianGoodsMapper;
    private final ICoreManjianService coreShopManjianService;
    private final ICoreShopManjianGoodsService coreShopManjianGoodsService;
    private final ShopManjianGiveRecordsMapper shopManjianGiveRecordsMapper;

    @Override
    public PageResult<ShopManjianListVo> page(PageParam pageParam, ShopManjianSearchParam searchParam) {
        int page = pageParam.getPage();
        int limit = pageParam.getLimit();

        QueryWrapper<ShopManjian> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("site_id", RequestUtils.siteId())
                .orderByDesc("create_time");

        if (ObjectUtil.isNotEmpty(searchParam.getManjianName())) {
            queryWrapper.like("manjian_name", searchParam.getManjianName());
        }
        if (ObjectUtil.isNotEmpty(searchParam.getStatus())) {
            queryWrapper.eq("status", searchParam.getStatus());
        }
        if (ObjectUtil.isNotEmpty(searchParam.getCreateTime())) {
            QueryMapperUtils.buildByTime(queryWrapper, "create_time", searchParam.getCreateTime());
        }

        Page<ShopManjian> iPage = shopManjianMapper.selectPage(new Page<>(page, limit), queryWrapper);
        List<ShopManjianListVo> list = new ArrayList<>(iPage.getRecords().size());
        for (ShopManjian item : iPage.getRecords()) {
            ShopManjianListVo vo = new ShopManjianListVo();
            BeanUtils.copyProperties(item, vo);
            vo.setStatusName(ShopManjianStatusEnum.getNameByValue(item.getStatus()));
            list.add(vo);
        }
        return PageResult.build(iPage, list);
    }

    @Transactional
    @Override
    public void closeManjian(Integer manjianId) {
        ShopManjian manjian = shopManjianMapper.selectById(manjianId);
        Assert.notNull(manjian, "满减送活动未找到");

        if (manjian.getEndTime() < System.currentTimeMillis() / 1000 || !ObjectUtil.equal(ShopManjianStatusEnum.ACTIVE.getValue(), manjian.getStatus())) {
            throw new RuntimeException("满减送活动已关闭");
        }

        manjian.setStatus(ShopManjianStatusEnum.CLOSE.getValue());
        shopManjianMapper.updateById(manjian);

        QueryWrapper<ShopManjianGoods> shopManjianGoodsQueryWrapper = new QueryWrapper<>();
        shopManjianGoodsQueryWrapper.eq("site_id", RequestUtils.siteId())
                .eq("manjian_id", manjianId);
        ShopManjianGoods shopManjianGoods = new ShopManjianGoods();
        shopManjianGoods.setStatus(ShopManjianStatusEnum.CLOSE.getValue());
        shopManjianGoodsMapper.update(shopManjianGoods, shopManjianGoodsQueryWrapper);
    }

    @Transactional
    @Override
    public void del(Integer id) {
        ShopManjian manjian = shopManjianMapper.selectById(id);
        Assert.notNull(manjian, "满减送活动未找到");

        if (ObjectUtil.equal(ShopManjianStatusEnum.ACTIVE.getValue(), manjian.getStatus())) {
            throw new RuntimeException("进行中活动不能直接删除");
        }

        shopManjianMapper.deleteById(id);
        shopManjianGoodsMapper.delete(new QueryWrapper<ShopManjianGoods>().eq("manjian_id", id).eq("site_id", RequestUtils.siteId()));
    }

    @Override
    public ShopManjianInfoVo info(Integer id) {
        ShopManjian model = shopManjianMapper.selectOne(
                new QueryWrapper<ShopManjian>()
                        .eq("manjian_id", id)
                        .eq("site_id", RequestUtils.siteId()));

        Assert.notNull(model, "满减活动不存在");

        ShopManjianInfoVo vo = new ShopManjianInfoVo();
        BeanUtils.copyProperties(model, vo);
        return vo;
    }

    @Override
    public PageResult<ShopManjianMemberVo> member(PageParam pageParam, Integer manjianId) {
        int page = pageParam.getPage();
        int limit = pageParam.getLimit();

        QueryWrapper<ShopManjianGiveRecords> shopManjianGiveRecordsWrapper = new QueryWrapper<>();
        shopManjianGiveRecordsWrapper.eq("site_id", RequestUtils.siteId())
                .eq("manjian_id", manjianId);
        List<ShopManjianGiveRecords> records = shopManjianGiveRecordsMapper.selectList(shopManjianGiveRecordsWrapper);
        if(ObjectUtil.isEmpty(records)) return PageResult.build(page, limit, 0, new ArrayList<>());
        Set<Integer> orderIds = CollStreamUtil.toSet(records, ShopManjianGiveRecords::getOrderId);
        Map<Integer, ShopOrder> orderMap = shopOrderMapper.selectBatchIds(orderIds).stream().collect(Collectors.toMap(ShopOrder::getOrderId, e -> e));
        Set<Integer> memberIds = CollStreamUtil.toSet(records, ShopManjianGiveRecords::getMemberId);
        Map<Integer, List<ShopManjianGiveRecords>> memberRecordMap = records.stream().collect(Collectors.groupingBy(ShopManjianGiveRecords::getMemberId));

        QueryWrapper<Member> memberQueryWrapper = new QueryWrapper<>();
        memberQueryWrapper.in("member_id", memberIds);
        Page<Member> iPage = memberMapper.selectPage(new Page<>(page, limit), memberQueryWrapper);
        if (ObjectUtil.isEmpty(iPage.getRecords())) {
            return PageResult.build(iPage, Collections.emptyList());
        }

        List<ShopManjianMemberVo> list = new ArrayList<>();
        for (Member record : iPage.getRecords()) {
            ShopManjianMemberVo vo = new ShopManjianMemberVo();
            BeanUtils.copyProperties(record, vo);
            vo.setTotalOrderMoney(BigDecimal.ZERO);
            vo.setTotalNum(0);
            vo.setFinallyOrderTime(0L);

            List<ShopManjianGiveRecords> memberRecords = memberRecordMap.get(record.getMemberId());
            if (ObjectUtil.isEmpty(memberRecords)) {
                list.add(vo);
                continue;
            }

            BigDecimal totalOrderMoney = BigDecimal.ZERO;
            for (BigDecimal bigDecimal : memberRecords.stream()
                    .map(memberRecord -> {
                        ShopOrder order = orderMap.get(memberRecord.getOrderId());
                        return order.getOrderMoney();
                    })
                    .toList()) {
                totalOrderMoney = totalOrderMoney.add(bigDecimal);
            }

            vo.setTotalOrderMoney(totalOrderMoney);
            vo.setTotalNum(memberRecords.size());
            Optional<ShopManjianGiveRecords> reduce = memberRecords.stream()
                    .filter(r -> r.getCreateTime() > vo.getFinallyOrderTime())
                    .reduce((o, t) -> t);
            reduce.ifPresent(r -> vo.setFinallyOrderTime(r.getCreateTime()));
            list.add(vo);
        }
        return PageResult.build(iPage, list);
    }

    @Override
    @Transactional
    public Map<String, Object>  add(ShopManjianParam addParam) {
        ShopManjian model = new ShopManjian();
        BeanUtils.copyProperties(addParam, model);
        model.setSiteId(RequestUtils.siteId());
        if(ObjectUtil.isNotEmpty(addParam.getGoodsIds())){
            model.setGoodsIds(addParam.getGoodsIds().toString());
        }

        if(ObjectUtil.isNotEmpty(addParam.getLevelIds())){
            model.setLevelIds(addParam.getLevelIds().toString());
        }

        if(ObjectUtil.isNotEmpty(addParam.getLabelIds())){
            model.setLabelIds(addParam.getLabelIds().toString());
        }

        if(ObjectUtil.isNotEmpty(addParam.getRuleJson())){
            model.setRuleJson(addParam.getRuleJson().toString());
        }

        model.setStatus(ShopManjianStatusEnum.NOT_ACTIVE.getValue());
        model.setStartTime(DateUtils.StringToTimestamp(addParam.getStartTime()));
        model.setEndTime(DateUtils.StringToTimestamp(addParam.getEndTime()));
        model.setCreateTime(System.currentTimeMillis() / 1000);
        if (ObjectUtil.isNotEmpty(addParam.getEndTime()) && model.getEndTime() < System.currentTimeMillis() / 1000) {
            throw new RuntimeException("活动结束时间不能小于当前时间");
        }

        List<ShopManjianGoodsData> goodsData = JSONUtil.toList(addParam.getGoodsData(), ShopManjianGoodsData.class);
        check(addParam, goodsData);

        model.setStatus(getStatus(model));
        shopManjianMapper.insert(model);

        if (!ObjectUtil.equal(model.getGoodsType(), ShopManjianGoodsTypeEnum.ALL_GOODS.getValue()) && ObjectUtil.isNotEmpty(goodsData)) {
            List<ShopManjianGoods> goods = goodsData2Goods(model, goodsData);
            coreShopManjianGoodsService.saveBatch(goods);
        }

        Map<String, Object> map = new HashMap<>();
        map.put("code", 1);
        map.put("data", model);
        return map;

    }

    @Override
    public ShopManjianInitVo getInit(ShopManjianInitParam param) {
        if (param.getManjianId() == null) {
            return null;
        }

        ShopManjian manjian = shopManjianMapper.selectById(param.getManjianId());
        if (manjian == null) {
            return null;
        }

        ShopManjianInitVo.ShopManjianInfo vo = new ShopManjianInitVo.ShopManjianInfo();
        BeanUtils.copyProperties(manjian, vo);
        vo.setStatusName(ShopManjianStatusEnum.getNameByValue(manjian.getStatus()));

        if (ObjectUtil.isNotEmpty(manjian.getRuleJson())) {
            List<ShopManjianRule> rules = JSONUtil.toList(manjian.getRuleJson(), ShopManjianRule.class);

            List<Integer> couponIds = rules.stream()
                    .map(ShopManjianRule::getCoupon)
                    .flatMap(coupon -> coupon.stream().map(ShopManjianCoupon::getCouponId))
                    .toList();
            Map<Integer, ShopCoupon> couponMap = ObjectUtil.isNotEmpty(couponIds) ? shopCouponMapper.selectBatchIds(couponIds).stream().collect(Collectors.toMap(ShopCoupon::getId, e -> e)) : new HashMap<>();

            List<Integer> goodsIds = rules.stream()
                    .map(ShopManjianRule::getGoods)
                    .flatMap(goods -> goods.stream().map(ShopManjianRule.GoodsDTO::getGoodsId))
                    .toList();
            Map<Integer, ShopGoods> goodsMap = ObjectUtil.isNotEmpty(goodsIds) ? shopGoodsMapper.selectBatchIds(goodsIds).stream().collect(Collectors.toMap(ShopGoods::getGoodsId, e -> e)) : new HashMap<>();

            List<Integer> skuIds = rules.stream()
                    .map(ShopManjianRule::getGoods)
                    .flatMap(goods -> goods.stream().map(ShopManjianRule.GoodsDTO::getSkuId))
                    .toList();
            Map<Integer, ShopGoodsSku> skuMap = ObjectUtil.isNotEmpty(skuIds) ? shopGoodsSkuMapper.selectBatchIds(skuIds).stream().collect(Collectors.toMap(ShopGoodsSku::getSkuId, e -> e)) : new HashMap<>();

            for (ShopManjianRule rule : rules) {
                if (ObjectUtil.isEmpty(rule.getCoupon())) {
                    continue;
                }

                for (ShopManjianCoupon couponVo : rule.getCoupon()) {
                    ShopCoupon coupon = couponMap.get(couponVo.getCouponId());
                    if (coupon == null) {
                        continue;
                    }

                    couponVo.setTypeName(ShopManjianCouponType.getNameByValue(coupon.getType()));
                    couponVo.setTitle(Optional.ofNullable(coupon.getTitle()).orElse(""));
                    couponVo.setPrice(Optional.ofNullable(coupon.getPrice()).orElse(BigDecimal.ZERO).toString());
                }

                if (ObjectUtil.isEmpty(rule.getGoods())) {
                    continue;
                }

                List<ShopManjianRule.GoodsDTO> goods = new ArrayList<>();
                for (ShopManjianRule.GoodsDTO good : rule.getGoods()) {
                    ShopGoods shopGoods = goodsMap.get(good.getGoodsId());
                    ShopManjianRule.GoodsDTO goodsDTO = new ShopManjianRule.GoodsDTO();
                    if (ObjectUtil.isNotEmpty(shopGoods)) {
                        BeanUtils.copyProperties(shopGoods, goodsDTO);
                    }
                    BeanUtils.copyProperties(good, goodsDTO);
                    ShopGoodsSku sku = skuMap.get(good.getSkuId());
                    if (ObjectUtil.isNotEmpty(sku)) {

                        ShopManjianRule.GoodsDTO.GoodsSkuDTO skuDTO = new ShopManjianRule.GoodsDTO.GoodsSkuDTO();
                        BeanUtils.copyProperties(sku, skuDTO);
                        goodsDTO.setGoodsSku(skuDTO);
                        goodsDTO.setPrice(skuDTO.getPrice());
                    }

                    goods.add(goodsDTO);
                }

                rule.setGoods(goods);
            }

            vo.setRuleJson(rules);
        }

        List<ShopManjianInitVo.ManjianGoods> manjianGoods = Collections.emptyList();
        if (!ShopManjianGoodsTypeEnum.ALL_GOODS.getValue().equals(manjian.getGoodsType())) {

            List<ShopManjianGoods> goods = shopManjianGoodsMapper.selectList(new QueryWrapper<ShopManjianGoods>().eq("manjian_id", param.getManjianId()).eq("site_id", RequestUtils.siteId()));
            manjianGoods = new ArrayList<>(CollectionUtil.size(goods));

            Set<Integer> goodsIds = CollStreamUtil.toSet(goods, ShopManjianGoods::getGoodsId);
            QueryWrapper<ShopGoods> shopGoodsQueryWrapper = new QueryWrapper<>();
            shopGoodsQueryWrapper.eq("site_id", RequestUtils.siteId()).in("goods_id", goodsIds);
            Map<Integer, ShopGoods> goodsMap = shopGoodsMapper.selectList(shopGoodsQueryWrapper).stream().collect(Collectors.toMap(ShopGoods::getGoodsId, e -> e));

            QueryWrapper<ShopGoodsSku> shopGoodsSkuQueryWrapper = new QueryWrapper<>();
            shopGoodsSkuQueryWrapper.in("goods_id", goodsIds);
            Map<Integer, ShopGoodsSku> skuMap = shopGoodsSkuMapper.selectList(shopGoodsSkuQueryWrapper).stream().collect(Collectors.toMap(ShopGoodsSku::getSkuId, e -> e));

            for (ShopManjianGoods good : goods) {
                ShopGoods shopGoods = goodsMap.get(good.getGoodsId());
                ShopGoodsSku sku = skuMap.get(good.getSkuId());

                ShopManjianInitVo.ManjianGoods manjianVo = new ShopManjianInitVo.ManjianGoods();
                BeanUtils.copyProperties(shopGoods, manjianVo);
                manjianVo.setPrice(sku.getPrice());
                manjianVo.setGoodsSku(sku);
                manjianVo.setSkuImage(sku.getSkuImage());
                manjianVo.setSkuName(sku.getSkuName());
                String thumb = CommonUtils.thumbImageSmall(RequestUtils.siteId(), shopGoods.getGoodsCover());
                manjianVo.setGoodsCoverThumbSmall(thumb);
                manjianGoods.add(manjianVo);
            }
        }

        ShopManjianInitVo initVo = new ShopManjianInitVo();
        initVo.setManjianInfo(vo);
        initVo.setManjianGoods(manjianGoods);
        return initVo;
    }

    @Override
    @Transactional
    public Map<String, Object> edit(Integer id, ShopManjianParam editParam) {
        editParam.setManjianId(id);
        ShopManjian model = shopManjianMapper.selectById(id);
        BeanUtils.copyProperties(editParam, model);

        if(ObjectUtil.isNotEmpty(editParam.getGoodsIds())){
            model.setGoodsIds(editParam.getGoodsIds().toString());
        }

        if(ObjectUtil.isNotEmpty(editParam.getLevelIds())){
            model.setLevelIds(editParam.getLevelIds().toString());
        }

        if(ObjectUtil.isNotEmpty(editParam.getLabelIds())){
            model.setLabelIds(editParam.getLabelIds().toString());
        }

        if(ObjectUtil.isNotEmpty(editParam.getRuleJson())){
            model.setRuleJson(editParam.getRuleJson().toString());
        }


        model.setStartTime(DateUtils.StringToTimestamp(editParam.getStartTime()));
        model.setEndTime(DateUtils.StringToTimestamp(editParam.getEndTime()));
        if (model.getEndTime() < System.currentTimeMillis() / 1000) {
            throw new RuntimeException("活动结束时间不能小于当前时间");
        }
        //添加商品
        List<ShopManjianGoodsData> goodsData = new LinkedList<>();
        if (ObjectUtil.isNotEmpty(editParam.getGoodsData())) {
            for (Object goodsDatum : editParam.getGoodsData()) {
                JSONObject goodsDataItemJson = JSONUtil.parseObj(goodsDatum);
                ShopManjianGoodsData goodsDataItem = new ShopManjianGoodsData();
                goodsDataItem.setGoodsId(Integer.parseInt(goodsDataItemJson.getStr("goods_id")));
                JSONObject skuDataJson = JSONUtil.parseObj(goodsDataItemJson.getStr("goodsSku"));
                if(ObjectUtil.isNotEmpty(skuDataJson))
                {
                    goodsDataItem.setSkuId(Integer.parseInt(skuDataJson.getStr("sku_id")));
                }else{
                    goodsDataItem.setSkuId(Integer.parseInt(goodsDataItemJson.getStr("sku_id")));
                }

                goodsData.add(goodsDataItem);
            }
        }
        check(editParam, goodsData);

        model.setStatus(getStatus(model));
        shopManjianMapper.updateById(model);

        if (!ObjectUtil.equal(model.getGoodsType(), ShopManjianGoodsTypeEnum.ALL_GOODS.getValue()) && ObjectUtil.isNotEmpty(goodsData)) {

            shopManjianGoodsMapper.delete(new QueryWrapper<ShopManjianGoods>().eq("site_id", RequestUtils.siteId()).eq("manjian_id", id));
            coreShopManjianGoodsService.saveBatch(goodsData2Goods(model, goodsData));
        }
        Map<String, Object> map = new HashMap<>();
        map.put("code", 1);
        map.put("data", model);
        return map;
    }

    @Override
    public void checkGoods(ShopManjianCheckGoodsParam param) {

        MPJQueryWrapper<ShopManjianGoods> shopManjianGoodsQueryWrapper = new MPJQueryWrapper<>();
        shopManjianGoodsQueryWrapper.setAlias("nsmg")
                .leftJoin("?_shop_manjian nsm ON nsmg.manjian_id = nsm.manjian_id".replace("?_", GlobalConfig.tablePrefix))
                .eq("nsmg.site_id", RequestUtils.siteId())
                .in("nsm.status", List.of(ShopManjianStatusEnum.ACTIVE.getValue(), ShopManjianStatusEnum.NOT_ACTIVE.getValue()));
        Long startTime = DateUtils.StringToTimestamp(param.getStartTime());
        Long endTime = DateUtils.StringToTimestamp(param.getEndTime());
        shopManjianGoodsQueryWrapper.and(i -> i.and(j -> j.between("nsm.start_time", startTime, endTime))
                .or(j -> j.between("nsm.end_time",  startTime, endTime))
                .or(j-> j.le("nsm.start_time", startTime).
                        ge("nsm.end_time", endTime))
                .or(j-> j.le("nsm.start_time", startTime)).
                        ge("nsm.end_time", endTime));
        shopManjianGoodsQueryWrapper.select("nsmg.goods_id");

        //不能直接Integer.class,java版本不兼容
        List<ShopManjianGoods> list = shopManjianGoodsMapper.selectJoinList(ShopManjianGoods.class, shopManjianGoodsQueryWrapper);
        if(ObjectUtil.isEmpty(list)|| ShopManjianGoodsTypeEnum.ALL_GOODS.getValue().equals(param.getGoodsType())){
           return;
        }
        List<Integer> selectGoodsIds = list.stream().map(ShopManjianGoods::getGoodsId).collect(Collectors.toList());

        List<Integer> goodsIds = param.getGoodsIds();
        if (ObjectUtil.isNotEmpty(param.getManjianId())) {
            QueryWrapper<ShopManjianGoods> manjianGoodsQueryWrapper = new QueryWrapper<>();
            manjianGoodsQueryWrapper.eq("site_id", RequestUtils.siteId())
                    .eq("manjian_id", param.getManjianId());
            List<ShopManjianGoods> goods = shopManjianGoodsMapper.selectList(manjianGoodsQueryWrapper);
            List<Integer> ids = CollStreamUtil.toList(goods, ShopManjianGoods::getGoodsId);
            goodsIds.removeAll(ids);
        }

        List<Integer> intersect = new ArrayList<>();
       if(ObjectUtil.isNotEmpty(goodsIds)) intersect.addAll(goodsIds);
       if(ObjectUtil.isNotEmpty(selectGoodsIds))  intersect.retainAll(selectGoodsIds);

        if (ObjectUtil.isEmpty(intersect)) {
            return;
        }

        //连表查询与单表查询使用query不同
        QueryWrapper<ShopManjianGoods> shopManjianGoodsMapQueryWrapper = new QueryWrapper<>();
        shopManjianGoodsMapQueryWrapper.in("goods_id", intersect)
                .eq("site_id", RequestUtils.siteId())
                .in("status", Arrays.asList(ShopManjianStatusEnum.ACTIVE.getValue(), ShopManjianStatusEnum.NOT_ACTIVE.getValue()))
                .eq("goods_type", param.getGoodsType())
                .groupBy("goods_id");
        Map<Integer, ShopManjianGoods> goodsMap = shopManjianGoodsMapper.selectList(shopManjianGoodsMapQueryWrapper).stream().collect(Collectors.toMap(ShopManjianGoods::getGoodsId, e -> e));
        Set<Integer> manjianIds = goodsMap.values().stream().map(ShopManjianGoods::getManjianId).collect(Collectors.toSet());
        if(ObjectUtil.isEmpty(manjianIds)) return;
        Map<Integer, ShopManjian> manjianMap = shopManjianMapper.selectBatchIds(manjianIds).stream().collect(Collectors.toMap(ShopManjian::getManjianId, e -> e));

        for (Integer goodsId : intersect) {
            ShopManjianGoods manjianGoods = goodsMap.get(goodsId);
            if (manjianGoods == null) {
                continue;
            }

            ShopManjian manjian = manjianMap.get(manjianGoods.getManjianId());
            if (manjian != null) {
                Map<String, Object> map = new HashMap<>();
                map.put("goods_id", goodsId);
                map.put("error_msg", "该商品已参加【" + manjian.getManjianName() + "】活动");
                throw new CommonException(-1, "该商品已参加【" + manjian.getManjianName() + "】活动", map);
            }
        }
    }

    @Override
    public void batchClose(ShopManjianBatchParam param) {
        if (ObjectUtil.isEmpty(param.getManjianId())) {
            throw new RuntimeException("满减送活动未找到");
        }

        List<ShopManjian> shopManjians = shopManjianMapper.selectBatchIds(param.getManjianId());
        shopManjians.forEach(manjian -> manjian.setStatus(ShopManjianStatusEnum.CLOSE.getValue()));
        coreShopManjianService.updateBatchById(shopManjians);
    }

    @Override
    public void batchDel(ShopManjianBatchParam param) {
        if (ObjectUtil.isEmpty(param.getManjianId())) {
            throw new RuntimeException("满减送活动未找到");
        }

        QueryWrapper<ShopManjian> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("manjian_id", param.getManjianId())
                .eq("site_id", RequestUtils.siteId());
        shopManjianMapper.delete(queryWrapper);
    }

    private int getStatus(ShopManjian model) {
        if (model.getStartTime() <= System.currentTimeMillis() / 1000) {
            return ShopManjianStatusEnum.ACTIVE.getValue();
        } else if (model.getEndTime() <= System.currentTimeMillis() / 1000) {
            return ShopManjianStatusEnum.END.getValue();
        }
        return model.getStatus();
    }

    private List<ShopManjianGoods> goodsData2Goods(ShopManjian model, List<ShopManjianGoodsData> goodsData) {
        return goodsData.stream()
                .map(data -> {
                    ShopManjianGoods manjianGoods = new ShopManjianGoods();
                    manjianGoods.setGoodsId(data.getGoodsId());
                    manjianGoods.setGoodsType(model.getGoodsType());
                    manjianGoods.setManjianId(model.getManjianId());
                    manjianGoods.setSiteId(RequestUtils.siteId());
                    manjianGoods.setSkuId(data.getSkuId());
                    manjianGoods.setStatus(model.getStatus());
                    return manjianGoods;
                })
                .toList();
    }

    /**
     * 检测是否存在活动
     * @param param
     * @param goodsData
     */
    private void check(ShopManjianParam param, List<ShopManjianGoodsData> goodsData) {
        checkGoodsActivity(param);
        List<Integer> selectGoodsIds = selectOrNotGoodsIds(param, false);
        List<Integer> selectGoodsNotIds = selectOrNotGoodsIds(param, true);
        Set<Integer> goodsIds = getGoodsIds(param.getGoodsType(), goodsData);

        switch (ShopManjianGoodsTypeEnum.getEnumByValue(param.getGoodsType())) {
            case ALL_GOODS -> {
                if (ObjectUtil.isNotEmpty(selectGoodsIds) || ObjectUtil.isNotEmpty(selectGoodsNotIds)) {
                    throw new RuntimeException("同一商品在一个时间段内只能参加一个满减送活动");
                }
            }
            case SELECTED_GOODS_NOT -> {
                if (ObjectUtil.isNotEmpty(selectGoodsIds)) {
                    List<Integer> copy = new ArrayList<>(selectGoodsIds);
                    copy.removeAll(goodsIds);
                    if (ObjectUtil.isNotEmpty(copy)) {
                        throw new RuntimeException("同一商品在一个时间段内只能参加一个满减送活动");
                    }
                }

                if (ObjectUtil.isNotEmpty(selectGoodsNotIds)) {
                    throw new RuntimeException("同一商品在一个时间段内只能参加一个满减送活动");
                }
            }
            case SELECTED_GOODS -> {
                List<Integer> copy = new ArrayList<>(goodsIds);
                if (ObjectUtil.isNotEmpty(selectGoodsIds)) {
                    copy.retainAll(selectGoodsIds);
                }else{
                    copy.clear();
                }
                if (ObjectUtil.isNotEmpty(selectGoodsNotIds)) {
                    copy.removeAll(selectGoodsNotIds);
                }
                if (ObjectUtil.isNotEmpty(copy)) {
                    throw new RuntimeException("同一商品在一个时间段内只能参加一个满减送活动");
                }
            }
        }
    }

    private Set<Integer> getGoodsIds(String goodsType, List<ShopManjianGoodsData> goodsData) {
        if (!ShopManjianGoodsTypeEnum.ALL_GOODS.getValue().equals(goodsType) &&
                ObjectUtil.isNotEmpty(goodsData)) {
            return CollStreamUtil.toSet(goodsData, ShopManjianGoodsData::getGoodsId);
        }

        return Collections.emptySet();
    }

    /**
     * 检测商品id是否在一段时间内参与活动
     * @param param
     * @param isNot  false:检测选中商品 true:检测不选中商品
     * @return
     */
    private List<Integer> selectOrNotGoodsIds(ShopManjianParam param, boolean isNot)  {
        MPJQueryWrapper<ShopManjianGoods> shopManjianGoodsQueryWrapper = new MPJQueryWrapper<>();
        shopManjianGoodsQueryWrapper.setAlias("nsmg")
                .leftJoin("?_shop_manjian nsm ON nsmg.manjian_id = nsm.manjian_id".replace("?_", GlobalConfig.tablePrefix))
                .eq("nsmg.site_id", RequestUtils.siteId())
                .in("nsm.status", List.of(ShopManjianStatusEnum.ACTIVE.getValue(), ShopManjianStatusEnum.NOT_ACTIVE.getValue()));
        if(ObjectUtil.isNotEmpty(param.getManjianId()))
        {
            shopManjianGoodsQueryWrapper.ne("nsm.manjian_id", param.getManjianId());
        }
        if (isNot) {
            shopManjianGoodsQueryWrapper .eq("nsm.goods_type", ShopManjianGoodsTypeEnum.SELECTED_GOODS_NOT.getValue());
        }else{
            shopManjianGoodsQueryWrapper .eq("nsm.goods_type", ShopManjianGoodsTypeEnum.SELECTED_GOODS.getValue());
        }

        Long startTime = DateUtils.StringToTimestamp(param.getStartTime());
        Long endTime = DateUtils.StringToTimestamp(param.getEndTime());
        shopManjianGoodsQueryWrapper.and(i -> i.and(j -> j.between("nsm.start_time", startTime, endTime))
                .or(j -> j.between("nsm.end_time",  startTime, endTime))
                .or(j-> j.le("nsm.start_time", startTime).
                        ge("nsm.end_time", endTime))
                .or(j-> j.le("nsm.start_time", startTime).
                        ge("nsm.end_time", endTime)));
        shopManjianGoodsQueryWrapper.select("nsmg.goods_id, nsmg.manjian_id");

        //不能直接Integer.class,java版本不兼容
        List<ShopManjianGoods> list = shopManjianGoodsMapper.selectJoinList(ShopManjianGoods.class, shopManjianGoodsQueryWrapper);
        if(ObjectUtil.isNotEmpty(list)){
            return list.stream().map(ShopManjianGoods::getGoodsId).collect(Collectors.toList());
        }else{
            return new ArrayList<>();
        }
    }

    private void checkGoodsActivity(ShopManjianParam param) {
        QueryWrapper<ShopManjian> shopManjianQueryWrapper = new QueryWrapper<>();
        shopManjianQueryWrapper.eq("site_id", RequestUtils.siteId())
                .eq("goods_type", ShopManjianGoodsTypeEnum.ALL_GOODS.getValue())
                .in("status", List.of(ShopManjianStatusEnum.NOT_ACTIVE.getValue(), ShopManjianStatusEnum.ACTIVE.getValue()));

        if(ObjectUtil.isNotEmpty(param.getManjianId()))
        {
            shopManjianQueryWrapper.ne("manjian_id", param.getManjianId());
        }
        Long startTime = DateUtils.StringToTimestamp(param.getStartTime());
        Long endTime = DateUtils.StringToTimestamp(param.getEndTime());
        shopManjianQueryWrapper.and(i -> i.and(j -> j.between("start_time", startTime, endTime))
                .or(j -> j.between("end_time",  startTime, endTime))
                .or(j-> j.le("start_time", startTime).
                        ge("end_time", endTime))
                .or(j-> j.le("start_time", startTime).
                        ge("end_time", endTime)));
        long allGoodsCount = shopManjianMapper.selectCount(shopManjianQueryWrapper);
        if (allGoodsCount > 0) {
            throw new AdminException("当前时间段已存在类型为全部商品参与的满减送活动，不可再添加其它满减送活动");
        }
    }

}
