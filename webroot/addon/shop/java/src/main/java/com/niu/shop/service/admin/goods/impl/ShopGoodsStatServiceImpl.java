package com.niu.shop.service.admin.goods.impl;

import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.niu.core.common.config.GlobalConfig;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.date.DateUtils;
import com.niu.core.enums.upload.UploadThumbTypeEnum;
import com.niu.core.service.core.sys.ICoreUploadService;
import com.niu.shop.entity.goods.ShopGoods;
import com.niu.shop.entity.goods.ShopGoodsStat;
import com.niu.shop.enums.goods.GoodsStatisticTypeEnum;
import com.niu.shop.mapper.goods.ShopGoodsMapper;
import com.niu.shop.mapper.goods.ShopGoodsStatMapper;
import com.niu.shop.service.admin.goods.IShopGoodsStatService;
import com.niu.shop.service.admin.goods.model.GoodsStatCount;
import com.niu.shop.service.admin.goods.param.GoodsStatGetRankParam;
import com.niu.shop.service.admin.goods.param.ShopGoodsStatDateParam;
import com.niu.shop.service.admin.goods.vo.GoodsStatRankInfo;
import com.niu.shop.service.admin.goods.vo.ShopGoodsStatBasicVo;
import com.niu.shop.service.admin.goods.vo.ShopGoodsStatTrendVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;


/**
 * 商品数据统计实现
 */
@Service
@RequiredArgsConstructor
public class ShopGoodsStatServiceImpl implements IShopGoodsStatService {
    private final ShopGoodsMapper shopGoodsMapper;
    private final ICoreUploadService coreUploadService;
    private final ShopGoodsStatMapper shopGoodsStatMapper;

    @Override
    public void syncStatGoods() {
        int page = 1;
        int pageSize = 1000;

        do {
            QueryWrapper<ShopGoods> queryWrapper = new QueryWrapper<>();
            queryWrapper.last("limit " + (page - 1) * pageSize + " " + pageSize);
            List<ShopGoods> shopGoods = shopGoodsMapper.selectList(queryWrapper);
            if (ObjectUtil.isEmpty(shopGoods)) {
                break;
            }

            Set<Integer> goodsIds = CollStreamUtil.toSet(shopGoods, ShopGoods::getGoodsId);
            String date = DateUtils.currInitDate();
            QueryWrapper<ShopGoodsStat> statQueryWrapper = new QueryWrapper<>();
            statQueryWrapper.eq("date", date)
                    .in("goods_id", goodsIds);
            Set<Integer> statGoodsIds = shopGoodsStatMapper.selectList(statQueryWrapper)
                    .stream()
                    .map(ShopGoodsStat::getGoodsId)
                    .collect(Collectors.toSet());

            List<ShopGoodsStat> records = shopGoods.stream()
                    .filter(goods -> !statGoodsIds.contains(goods.getGoodsId()))
                    .map(goods -> {
                        ShopGoodsStat stat = new ShopGoodsStat();
                        stat.setSiteId(goods.getSiteId());
                        stat.setDate(date);
                        stat.setDateTime(DateUtils.StringToTimestamp(date));
                        stat.setGoodsId(goods.getGoodsId());
                        return stat;
                    })
                    .toList();

            if (ObjectUtil.isNotEmpty(records)) {
                shopGoodsStatMapper.insertBatchSomeColumn(records);
            }

            page++;
        } while (true);
    }

    @Override
    public ShopGoodsStatBasicVo getBasic(ShopGoodsStatDateParam param) {
        List<String> dates = ObjectUtil.isEmpty(param.getDate()) ? new ArrayList<>(2) : Arrays.asList(param.getDate().split("-"));
        if (ObjectUtil.isEmpty(dates)) {
            String start = new DateTime().offset(DateField.MONTH, -1).toString("yyyy-MM-dd");
            String end = new DateTime().toString("yyyy-MM-dd");
            dates.add(start);
            dates.add(end);
        }

        QueryWrapper<ShopGoods> goodsQueryWrapper = new QueryWrapper<>();
        goodsQueryWrapper.eq("site_id", RequestUtils.siteId());
        List<Integer> goodsIds = shopGoodsMapper.selectList(goodsQueryWrapper).stream().map(ShopGoods::getGoodsId).toList();

        if (dates.size() == 1 || ObjectUtil.isEmpty(goodsIds)) {
            return new ShopGoodsStatBasicVo();
        }

        QueryWrapper<ShopGoodsStat> statQueryWrapper = new QueryWrapper<>();
        statQueryWrapper.in("goods_id", goodsIds)
        .eq("site_id", RequestUtils.siteId())
        .between("date_time", DateUtils.StringToTimestamp(dates.get(0)), DateUtils.StringToTimestamp(dates.get(1)))
        .select("goods_id,sum(cart_num) as cart_num,sum(sale_num) as sale_num,sum(pay_num) as pay_num,sum(pay_money) as pay_money,sum(refund_num) as refund_num,sum(refund_money) as refund_money,sum(collect_num) as collect_num,sum(evaluate_num) as evaluate_num,sum(access_num) as access_num,sum(goods_visit_member_count) as goods_visit_member_count")
        .groupBy("goods_id");
        List<ShopGoodsStat> shopGoodsStats = shopGoodsStatMapper.selectList(statQueryWrapper);

        ShopGoodsStatBasicVo vo = new ShopGoodsStatBasicVo();
        for (ShopGoodsStat stat : shopGoodsStats) {
            if (stat.getAccessNum() >= 1) {
                vo.setAccessGoodNum(vo.getAccessGoodNum() + 1);
            }

            if (stat.getSaleNum() >= 1) {
                vo.setSaleGoodNum(vo.getSaleGoodNum() + 1);
            }

            vo.setCartNum(vo.getCartNum() + stat.getCartNum());
            vo.setSaleNum(vo.getSaleNum() + stat.getSaleNum());
            vo.setPayNum(vo.getPayNum() + stat.getPayNum());
            vo.setRefundNum(vo.getRefundNum() + stat.getRefundNum());
            vo.setAccessNum(vo.getAccessNum() + stat.getAccessNum());
            vo.setCollectNum(vo.getCollectNum() + stat.getCollectNum());
            vo.setEvaluateNum(vo.getEvaluateNum() + stat.getEvaluateNum());
            vo.setGoodsVisitMemberCount(vo.getGoodsVisitMemberCount() + stat.getGoodsVisitMemberCount());
            vo.setPayMoney(stat.getPayMoney().add(NumberUtil.toBigDecimal(vo.getPayMoney())).intValue());
            vo.setRefundMoney(stat.getRefundMoney().add(NumberUtil.toBigDecimal(vo.getRefundMoney())).intValue());
        }

        return vo;
    }

    @Override
    public ShopGoodsStatTrendVo getTrend(ShopGoodsStatDateParam param) {
        String date = param.getDate();
        String dateType = "day";
        if (ObjectUtil.isEmpty(param.getDate())) {
            String start = new DateTime().offset(DateField.MONTH, -1).toString("yyyy-MM-dd");
            String end = new DateTime().toString("yyyy-MM-dd");
            date = start + "-" + end;
        }

        List<String> dateArr = Arrays.asList(date.split("-"));
        List<String> allDates = getDatesBetween(dateArr.get(0), dateArr.get(1));
        if (allDates.size() > 32) {
            dateType = "month";
            allDates = getMonthsBetween(dateArr.get(0), dateArr.get(1));
        }

        QueryWrapper<ShopGoods> goodsQueryWrapper = new QueryWrapper<>();
        goodsQueryWrapper.eq("site_id", RequestUtils.siteId());
        List<ShopGoods> shopGoods = shopGoodsMapper.selectList(goodsQueryWrapper);
        List<Integer> goodsIds = CollStreamUtil.toList(shopGoods, ShopGoods::getGoodsId);

        if (ObjectUtil.isEmpty(allDates) || goodsIds.size() == 0) {
            ShopGoodsStatTrendVo vo = new ShopGoodsStatTrendVo();
            vo.setData(Collections.emptyList());
            vo.setXAxis(allDates);
            return vo;
        }

        QueryWrapper<ShopGoodsStat> statQueryWrapper = new QueryWrapper<>();
        statQueryWrapper.between("date_time", DateUtils.StringToTimestamp(dateArr.get(0)), DateUtils.StringToTimestamp(dateArr.get(1)))
                .select("goods_id,date,date_time,sum(cart_num) as cart_num,sum(sale_num) as sale_num,sum(pay_num) as pay_num,sum(pay_money) as pay_money,sum(refund_num) as refund_num,sum(refund_money) as refund_money,sum(collect_num) as collect_num,sum(evaluate_num) as evaluate_num,sum(access_num) as access_num,sum(goods_visit_member_count) as goods_visit_member_count")
                .eq("site_id", RequestUtils.siteId())
                .in("goods_id", goodsIds)
                .groupBy("date_time")
                .orderByAsc("date_time");
        List<ShopGoodsStat> list = shopGoodsStatMapper.selectList(statQueryWrapper);

        GoodsStatCount statCount = new GoodsStatCount();
        for (String curDate : allDates) {
            statCount.getAccessCount().put(curDate, 0);
            statCount.getGoodsVisitMemberCount().put(curDate, 0);
            statCount.getPayMoney().put(curDate, BigDecimal.ZERO);
            statCount.getRefundMoney().put(curDate, BigDecimal.ZERO);
            statCount.getCartNum().put(curDate, 0);
            statCount.getSaleNum().put(curDate, 0);
            statCount.getPayNum().put(curDate, 0);
            statCount.getRefundNum().put(curDate, 0);
        }

        switch (dateType) {
            case "day" -> {
                for (ShopGoodsStat stat : list) {
                    String key = stat.getDate();
                    statCount.getAccessCount().computeIfPresent(key, (k, v) -> v + stat.getAccessNum());
                    statCount.getGoodsVisitMemberCount().computeIfPresent(key, (k, v) -> v + stat.getGoodsVisitMemberCount());
                    statCount.getCartNum().computeIfPresent(key, (k, v) -> v + stat.getCartNum());
                    statCount.getSaleNum().computeIfPresent(key, (k, v) -> v + stat.getSaleNum());
                    statCount.getPayNum().computeIfPresent(key, (k, v) -> v + stat.getPayNum());
                    statCount.getRefundNum().computeIfPresent(key, (k, v) -> v + stat.getRefundNum());
                    statCount.getPayMoney().computeIfPresent(key, (k, v) -> v.add(stat.getPayMoney()));
                    statCount.getRefundMoney().computeIfPresent(key, (k, v) -> v.add(stat.getRefundMoney()));
                }
            }
            case "month" -> {
                Map<String, List<String>> selectData = new HashMap<>();
                for (String v : allDates) {
                    String k = new DateTime(DateUtils.StringToTimestamp(v)).toString("yyyy-MM");
                    List<String> value = selectData.getOrDefault(k, new ArrayList<>());
                    value.add(v);
                    selectData.put(k, value);
                }

                for (Map.Entry<String, List<String>> entry : selectData.entrySet()) {
                    String key = entry.getKey();
                    List<String> value = entry.getValue();
                    list.stream()
                            .filter(stat -> value.contains(stat.getDate()))
                            .forEach(stat -> {
                                statCount.getAccessCount().computeIfPresent(key, (k, v) -> v + stat.getAccessNum());
                                statCount.getGoodsVisitMemberCount().computeIfPresent(key, (k, v) -> v + stat.getGoodsVisitMemberCount());
                                statCount.getCartNum().computeIfPresent(key, (k, v) -> v + stat.getCartNum());
                                statCount.getSaleNum().computeIfPresent(key, (k, v) -> v + stat.getSaleNum());
                                statCount.getPayNum().computeIfPresent(key, (k, v) -> v + stat.getPayNum());
                                statCount.getRefundNum().computeIfPresent(key, (k, v) -> v + stat.getRefundNum());
                                statCount.getPayMoney().computeIfPresent(key, (k, v) -> v.add(stat.getPayMoney()));
                                statCount.getRefundMoney().computeIfPresent(key, (k, v) -> v.add(stat.getRefundMoney()));
                            });
                }
            }
        }

        List<ShopGoodsStatTrendVo.DataItem> items = new ArrayList<>(8);
        ReflectionUtils.doWithFields(statCount.getClass(), field -> {
            field.setAccessible(true);
            ShopGoodsStatTrendVo.DataItem item = new ShopGoodsStatTrendVo.DataItem();
            item.setName(StrUtil.toUnderlineCase(field.getName()));
            if (field.get(statCount) instanceof Map<?, ?> map) {
                List<BigDecimal> data = CollStreamUtil.toList(map.values(), i -> NumberUtil.toBigDecimal(i.toString()));
                item.setData(data);
            }
            items.add(item);
        });

        return new ShopGoodsStatTrendVo(items, allDates);
    }

    @Override
    public PageResult<GoodsStatRankInfo> getRank(PageParam pageParam, GoodsStatGetRankParam param) {
        String date = param.getDate();
        if (ObjectUtil.isEmpty(param.getDate())) {
            String start = new DateTime().offset(DateField.MONTH, -1).toString("yyyy/MM/dd");
            String end = new DateTime().toString("yyyy/MM/dd");
            date = start + "-" + end;
        }

        String[] dateArr = date.split("-");
        MPJQueryWrapper<ShopGoodsStat> queryWrapper = new MPJQueryWrapper<>();
        queryWrapper.eq("stat.site_id", RequestUtils.siteId())
                .between("date_time", DateUtils.StringToTimestamp(dateArr[0]), DateUtils.StringToTimestamp(dateArr[1]))
                .eq("goods.delete_time", 0)
                .setAlias("stat")
                .leftJoin("?_shop_goods goods on stat.goods_id=goods.goods_id".replace("?_", GlobalConfig.tablePrefix))
                .groupBy("stat.goods_id")
                .select("stat.site_id,stat.goods_id,sum(stat.cart_num) as cart_num,sum(stat.sale_num) as sale_num,sum(stat.pay_num) as pay_num,sum(stat.pay_money) as pay_money,sum(stat.refund_num) as refund_num,sum(stat.refund_money) as refund_money,sum(stat.collect_num) as collect_num,sum(stat.evaluate_num) as evaluate_num,sum(stat.access_num) as access_num,sum(goods_visit_member_count) as goods_visit_member_count,goods_name,goods_cover,is_gift");

        if (ObjectUtil.isNotEmpty(param.getType())) {
            queryWrapper.orderByDesc(param.getType());
        }

        queryWrapper.orderByDesc("goods.sort")
                .orderByDesc("access_num")
                .orderByDesc("goods.create_time");

        if (ObjectUtil.isNotEmpty(param.getCategoryIds())) {
            queryWrapper.and(wrapper ->
                    param.getCategoryIds().forEach(id -> wrapper.or().like("category_id", id)));
        }

        if (ObjectUtil.isNotEmpty(param.getGoodsName())) {
            queryWrapper.like("goods_name", param.getGoodsName());
        }

        IPage<GoodsStatRankInfo> iPage = shopGoodsStatMapper.selectJoinPage(new Page<>(pageParam.getPage(), pageParam.getLimit()), GoodsStatRankInfo.class, queryWrapper);
        iPage.getRecords().forEach(record -> {
            String cover = coreUploadService.thumb(RequestUtils.siteId(), record.getGoodsCover(), UploadThumbTypeEnum.SMALL.getType())
                    .getDataMap().get(UploadThumbTypeEnum.SMALL.getType());
            record.setGoodsCoverThumbSmall(cover);
        });
        return PageResult.build(iPage);
    }

    @Override
    public Map<String, String> getType() {
        Map<String, String> map = new HashMap<>(8, 1);
        for (GoodsStatisticTypeEnum value : GoodsStatisticTypeEnum.values()) {
            map.put(value.getValue(), value.getName());
        }
        return map;
    }

    private List<String> getDatesBetween(String startDate, String endDate) {
        List<String> dates = new ArrayList<>(2);
        DateTime start = new DateTime(startDate);
        DateTime end = new DateTime(endDate);

        if (start.isAfter(end)) {
            return dates;
        }

        while (start.isBeforeOrEquals(end)) {
            dates.add(start.toString("yyyy-MM-dd"));
            start = start.offset(DateField.HOUR, 24);
        }

        return dates;
    }

    private List<String> getMonthsBetween(String startDate, String endDate) {
        List<String> months = new ArrayList<>(2);
        long start = DateUtils.StringToTimestamp(startDate);
        long end = DateUtils.StringToTimestamp(endDate);

        if (start > end) {
            return months;
        }

        while (start <= end) {
            months.add(DateUtils.timestampToString(start));
            start = new DateTime(start).offset(DateField.HOUR, 24).millisecond();
        }

        return months;
    }

}
