package com.niu.shop.service.admin.marketing.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.exception.AdminException;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.date.DateUtils;
import com.niu.core.common.config.GlobalConfig;
import com.niu.core.common.utils.mapper.QueryMapperUtils;
import com.niu.core.service.admin.member.vo.MemberBriefInfoVo;
import com.niu.core.service.core.sys.ICoreConfigService;
import com.niu.shop.entity.active.ShopActive;
import com.niu.shop.entity.active.ShopActiveGoods;
import com.niu.shop.entity.goods.ShopGoods;
import com.niu.shop.entity.goods.ShopGoodsSku;
import com.niu.shop.entity.order.ShopOrder;
import com.niu.shop.entity.order.ShopOrderDiscount;
import com.niu.shop.entity.order.ShopOrderGoods;
import com.niu.shop.enums.active.ActiveClassEnum;
import com.niu.shop.enums.active.ActiveGoodsTypeEnum;
import com.niu.shop.enums.active.ActiveStatusEnum;
import com.niu.shop.enums.active.ActiveTypeEnum;
import com.niu.shop.mapper.active.ShopActiveGoodsMapper;
import com.niu.shop.mapper.active.ShopActiveMapper;
import com.niu.shop.mapper.goods.ShopGoodsMapper;
import com.niu.shop.mapper.goods.ShopGoodsSkuMapper;
import com.niu.shop.mapper.order.ShopOrderDiscountMapper;
import com.niu.shop.mapper.order.ShopOrderGoodsMapper;
import com.niu.shop.mapper.order.ShopOrderMapper;
import com.niu.shop.service.admin.marketing.IShopDiscountService;
import com.niu.shop.service.admin.marketing.param.*;
import com.niu.shop.service.admin.marketing.vo.*;
import com.niu.shop.service.core.marketing.ICoreActiveService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;


/**
 * 限时折扣实现
 */
@Service
public class ShopDiscountServiceImpl implements IShopDiscountService {
        
    @Resource
    ShopActiveMapper shopActiveMapper;

    @Resource
    ShopActiveGoodsMapper shopActiveGoodsMapper;

    @Resource
    ShopGoodsMapper shopGoodsMapper;

    @Resource
    ShopGoodsSkuMapper shopGoodsSkuMapper;

    @Resource
    ICoreActiveService coreActiveService;

    @Resource
    ICoreConfigService coreConfigService;

    @Resource
    ShopOrderMapper shopOrderMapper;

    @Resource
    ShopOrderDiscountMapper shopOrderDiscountMapper;

    @Resource
    ShopOrderGoodsMapper shopOrderGoodsMapper;

    /**
     * 限时折扣列表
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<ShopActiveListVo>
     */
    @Override
    public PageResult<ShopDiscountListVo> getPage(PageParam pageParam, ShopDiscountSearchParam searchParam) {
        Integer page  = pageParam.getPage();
        Integer limit =pageParam.getLimit();

        QueryWrapper<ShopActive> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("site_id", RequestUtils.siteId());
        queryWrapper.eq("active_class", "discount");
        if (ObjectUtil.isNotEmpty(searchParam.getActiveName())) queryWrapper.like("active_name", searchParam.getActiveName());
        if (ObjectUtil.isNotEmpty(searchParam.getActiveStatus())) queryWrapper.eq("active_status", searchParam.getActiveStatus());
        queryWrapper.orderByDesc("active_id");

        IPage<ShopActive> iPage = shopActiveMapper.selectPage(new Page<>(page, limit), queryWrapper);
        List<ShopDiscountListVo> list = new LinkedList<>();
        for (ShopActive item : iPage.getRecords()) {
            ShopDiscountListVo vo = new ShopDiscountListVo();
            BeanUtils.copyProperties(item, vo);
            list.add(vo);
        }
        return PageResult.build(page, limit, iPage.getTotal()).setData(list);
    }

    /**
     * 限时折扣详情
     * @param id 主键
     * @return ShopActiveInfoVo
     */
    @Override
    public ShopDiscountInfoVo info(Integer id) {
        ShopActive model = shopActiveMapper.selectOne(
                new QueryWrapper<ShopActive>()
                    .eq("active_id", id).eq("site_id", RequestUtils.siteId()).eq("active_class", "discount")
                    .last("limit 1"));

        Assert.notNull(model, "数据不存在");

        ShopDiscountInfoVo vo = new ShopDiscountInfoVo();
        BeanUtils.copyProperties(model, vo);
        return vo;
    }
    /**
     * 限时折扣详情
     * @param id 主键
     * @return ShopActiveInfoVo
     */
    @Override
    public ShopDiscountDetailVo detail(Integer id) {
        ShopActive model = shopActiveMapper.selectOne(
                new QueryWrapper<ShopActive>()
                        .eq("active_id", id)
                        .eq("site_id", RequestUtils.siteId())
                        .eq("active_class", "discount"));

        Assert.notNull(model, "数据不存在");

        ShopDiscountDetailVo vo = new ShopDiscountDetailVo();
        BeanUtils.copyProperties(model, vo);
        vo.setActiveGoods(shopActiveGoodsMapper.selectList(new QueryWrapper<ShopActiveGoods>().eq("active_id", id).eq("site_id", RequestUtils.siteId())));
        return vo;
    }


    @Override
    @Transactional
    public void add(ShopDiscountParam shopDiscountParam) {

        JSONArray goodsList = JSONUtil.parseArray(shopDiscountParam.getGoodsData());
        this.checkGoods(goodsList);

        List<Integer> goodsIds = new ArrayList<>();
        for (int i = 0; i < goodsList.size(); i++) {
            JSONObject goods = goodsList.getJSONObject(i);
            goodsIds.add(goods.getInt("goods_id"));
        }

        MPJQueryWrapper<ShopActiveGoods> wrapper = new MPJQueryWrapper<>();
        wrapper.setAlias("ag"). innerJoin("?_shop_active a ON ag.active_id = a.active_id".replace("?_", GlobalConfig.tablePrefix));
        wrapper.eq("a.site_id", RequestUtils.siteId());
        wrapper.in("a.active_status", Arrays.asList(ActiveStatusEnum.NOT_ACTIVE.getStatus(), ActiveStatusEnum.ACTIVE.getStatus()));
        wrapper.in("ag.goods_id", goodsIds.toArray());
        wrapper.eq("ag.active_class", "discount");
        Long startTime = DateUtils.StringToTimestamp(shopDiscountParam.getStartTime());
        Long endTime = DateUtils.StringToTimestamp(shopDiscountParam.getEndTime());
        wrapper.and(i -> i.and(j -> j.between("a.start_time", startTime, endTime))
                .or(j -> j.between("a.end_time",  startTime, endTime))
                .or(j-> j.le("a.start_time", startTime).
                        ge("a.end_time", endTime))
                .or(j-> j.le("start_time", startTime).
                        ge("a.end_time", endTime))
        );
        long count = shopActiveGoodsMapper.selectJoinCount(wrapper);
        if (count > 0) {
            throw new AdminException("同一商品在一个时间段内只能参加一个限时折扣活动");
        }
        //数据添加
        ShopActive activeModel = new ShopActive();
        activeModel.setSiteId(RequestUtils.siteId());
        activeModel.setActiveName(shopDiscountParam.getActiveName());
        activeModel.setActiveDesc(shopDiscountParam.getActiveDesc());
        activeModel.setActiveType(ActiveTypeEnum.GOODS.getType());
        activeModel.setActiveGoodsType(ActiveGoodsTypeEnum.GOODS_SINGLE.getType());
        activeModel.setActiveGoodsInfo(shopDiscountParam.getGoodsData());
        activeModel.setActiveClass("discount");
        activeModel.setActiveClassCategory("discount");
        activeModel.setActiveStatus(ActiveStatusEnum.NOT_ACTIVE.getStatus());
        activeModel.setStartTime(DateUtils.StringToTimestamp(shopDiscountParam.getStartTime()));
        activeModel.setEndTime(DateUtils.StringToTimestamp(shopDiscountParam.getEndTime()));
        activeModel.setCreateTime(System.currentTimeMillis() / 1000);
        //商品添加

        List<ShopActiveGoods> activeGoodsList = new ArrayList<>();
        for (int i = 0; i < goodsList.size(); i++) {
            JSONObject goods = goodsList.getJSONObject(i);
            ShopActiveGoods activeGoods = new ShopActiveGoods();
            activeGoods.setGoodsId(goods.getInt("goods_id"));
            activeGoods.setSiteId(RequestUtils.siteId());
            activeGoods.setActiveClass("discount");
            activeGoods.setActiveGoodsType(ActiveGoodsTypeEnum.GOODS_SINGLE.getType());
            activeGoods.setActiveGoodsValue(goods.getStr("sku_list"));
            activeGoods.setActiveGoodsStatus(ActiveStatusEnum.NOT_ACTIVE.getStatus());

            //计算最低价格
            JSONArray skuList = goods.getJSONArray("sku_list");
            if(skuList.size() > 0) {
                List<BigDecimal> priceList = new ArrayList<>();
                for (int j = 0; j < skuList.size(); j++) {
                    JSONObject sku = skuList.getJSONObject(j);
                    if(sku.getInt("is_enabled") == 1){
                        priceList.add(sku.getBigDecimal("price"));
                    }
                }
                activeGoods.setActiveGoodsPrice(Collections.min(priceList));
            }
            activeGoodsList.add(activeGoods);
        }
        coreActiveService.add(activeModel, activeGoodsList);
    }


    @Override
    public void checkGoods(JSONArray goodsList) {

        if(ObjectUtil.isEmpty(goodsList)) throw new AdminException("活动商品不能为空");
        for (int i = 0; i < goodsList.size(); i++) {
            JSONObject goods = goodsList.getJSONObject(i);
            if(ObjectUtil.isEmpty(goods.get("sku_list"))) throw new AdminException("活动商品不能为空");
            for (int j = 0; j < goods.getJSONArray("sku_list").size(); j++) {
                JSONObject sku = goods.getJSONArray("sku_list").getJSONObject(j);
                if(ObjectUtil.isNull(sku.get("is_enabled"))) throw new AdminException("is_enabled必传");
                if(sku.getInt("is_enabled") == 1){
                    if(ObjectUtil.isEmpty(sku.get("discount_type"))) throw new AdminException("折扣类型不能为空");
                    String[] discountTypeArray = {"discount", "reduce", "specify"};
                    if(!Arrays.asList(discountTypeArray).contains(sku.get("discount_type").toString())) throw new AdminException("折扣类型错误");
                    if (ObjectUtil.isEmpty(sku.get("discount_price"))) throw new AdminException("折扣价格discount_price不能为空");
                    if (ObjectUtil.isEmpty(sku.get("specify_price"))) throw new AdminException("促销价不能为空");
                    if (ObjectUtil.isEmpty(sku.get("discount_rate"))) throw new AdminException("打折折扣不能为空");
                    if (ObjectUtil.isEmpty(sku.get("reduce_money"))) throw new AdminException("减钱金额不能为空");
                }
            }
        }
    }

    @Override
    public void edit(Integer id, ShopDiscountParam shopDiscountParam) {

        JSONArray goodsList = JSONUtil.parseArray(shopDiscountParam.getGoodsData());
        this.checkGoods(goodsList);

        List<Integer> goodsIds = new ArrayList<>();
        for (int i = 0; i < goodsList.size(); i++) {
            JSONObject goods = goodsList.getJSONObject(i);
            goodsIds.add(goods.getInt("goods_id"));
        }

        MPJQueryWrapper<ShopActiveGoods> wrapper = new MPJQueryWrapper<>();
        wrapper.setAlias("ag"). innerJoin("?_shop_active a ON ag.active_id = a.active_id".replace("?_", GlobalConfig.tablePrefix));
        wrapper.eq("a.site_id", RequestUtils.siteId());
        wrapper.ne("a.active_id", id);
        wrapper.in("a.active_status", Arrays.asList(ActiveStatusEnum.NOT_ACTIVE.getStatus(), ActiveStatusEnum.ACTIVE.getStatus()));
        wrapper.in("ag.goods_id", goodsIds.toArray());
        wrapper.eq("ag.active_class", "discount");
        Long startTime = DateUtils.StringToTimestamp(shopDiscountParam.getStartTime());
        Long endTime = DateUtils.StringToTimestamp(shopDiscountParam.getEndTime());
        wrapper.and(i -> i.and(j -> j.between("a.start_time", startTime, endTime))
                .or(j -> j.between("a.end_time",  startTime, endTime))
                .or(j-> j.le("a.start_time", startTime).
                        ge("a.end_time", endTime))
                .or(j-> j.le("start_time", startTime).
                        ge("a.end_time", endTime))
        );
        long count = shopActiveGoodsMapper.selectJoinCount(wrapper);
        if (count > 0) {
            throw new AdminException("同一商品在一个时间段内只能参加一个限时折扣活动");
        }
        //数据添加
        ShopActive activeModel = shopActiveMapper.selectOne(new QueryWrapper<ShopActive>().eq("active_id", id).eq("site_id", RequestUtils.siteId()));
        Assert.notNull(activeModel, "活动不存在");
        activeModel.setActiveName(shopDiscountParam.getActiveName());
        activeModel.setActiveDesc(shopDiscountParam.getActiveDesc());
        activeModel.setActiveGoodsInfo(shopDiscountParam.getGoodsData());
        activeModel.setActiveStatus(ActiveStatusEnum.NOT_ACTIVE.getStatus());
        activeModel.setStartTime(DateUtils.StringToTimestamp(shopDiscountParam.getStartTime()));
        activeModel.setEndTime(DateUtils.StringToTimestamp(shopDiscountParam.getEndTime()));
        activeModel.setUpdateTime(System.currentTimeMillis() / 1000);

        List<ShopActiveGoods> activeGoodsList = new ArrayList<>();
        for (int i = 0; i < goodsList.size(); i++) {
            JSONObject goods = goodsList.getJSONObject(i);
            ShopActiveGoods activeGoods = new ShopActiveGoods();
            activeGoods.setActiveId(id);
            activeGoods.setGoodsId(goods.getInt("goods_id"));
            activeGoods.setSiteId(RequestUtils.siteId());
            activeGoods.setActiveClass("discount");
            activeGoods.setActiveGoodsType(ActiveGoodsTypeEnum.GOODS_SINGLE.getType());
            activeGoods.setActiveGoodsValue(goods.getStr("sku_list"));
            activeGoods.setActiveGoodsStatus(ActiveStatusEnum.NOT_ACTIVE.getStatus());

            //计算最低价格
            JSONArray skuList = goods.getJSONArray("sku_list");
            if(skuList.size() > 0) {
                List<BigDecimal> priceList = new ArrayList<>();
                for (int j = 0; j < skuList.size(); j++) {
                    JSONObject sku = skuList.getJSONObject(j);
                    if(sku.getInt("is_enabled") == 1){
                        priceList.add(sku.getBigDecimal("price"));
                    }
                }
                activeGoods.setActiveGoodsPrice(Collections.min(priceList));
            }
            activeGoodsList.add(activeGoods);
        }
        coreActiveService.edit(activeModel, activeGoodsList);
    }

    /**
     * 限时折扣删除
     * @param id 主键ID
     */
    @Override
    public void del(Integer id) {
        coreActiveService.del(id, RequestUtils.siteId(), 0);
    }


    @Override
    public boolean setGoodsDiscount(Integer activeId) {

        ShopActive shopActive = shopActiveMapper.selectOne(new QueryWrapper<ShopActive>().eq("active_id", activeId));
        if(ObjectUtil.isEmpty(shopActive)) return true;
        if(!shopActive.getActiveStatus().equals(ActiveStatusEnum.ACTIVE.getStatus())) return true;

        List<ShopActiveGoods> shopActiveGoodsList = shopActiveGoodsMapper.selectList(new QueryWrapper<ShopActiveGoods>().eq("active_id", activeId));
        List<Integer> activeGoodsIds = shopActiveGoodsList.stream().map(ShopActiveGoods::getGoodsId).collect(Collectors.toList());

        ShopGoods discountShopGoods = new ShopGoods();
        discountShopGoods.setIsDiscount(1);
        shopGoodsMapper.update(discountShopGoods, new QueryWrapper<ShopGoods>().in("goods_id", activeGoodsIds));

        for(ShopActiveGoods shopActiveGoods : shopActiveGoodsList)
        {
            JSONArray activeGoodsValue = JSONUtil.parseArray(shopActiveGoods.getActiveGoodsValue());
            for(int i = 0; i < activeGoodsValue.size(); i++) {
                JSONObject sku = activeGoodsValue.getJSONObject(i);
                if(sku.getStr("is_enabled").equals("1"))
                {
                    ShopGoodsSku updateShopSku = new ShopGoodsSku();
                    updateShopSku.setSalePrice(sku.getBigDecimal("discount_price"));
                    updateShopSku.setSkuId(sku.getInt("sku_id"));
                    shopGoodsSkuMapper.updateById(updateShopSku);
                }
            }
        }

        return true;
    }

    @Override
    public boolean cancelGoodsDiscount(Integer activeId) {

        ShopActive shopActive = shopActiveMapper.selectOne(new QueryWrapper<ShopActive>().eq("active_id", activeId));
        if(ObjectUtil.isEmpty(shopActive)) return true;
        if(shopActive.getActiveStatus().equals(ActiveStatusEnum.ACTIVE.getStatus())) return true;

        List<ShopActiveGoods> shopActiveGoodsList = shopActiveGoodsMapper.selectList(new QueryWrapper<ShopActiveGoods>().eq("active_id", activeId));
        List<Integer> activeGoodsIds = shopActiveGoodsList.stream().map(i -> i.getGoodsId()).collect(Collectors.toList());

        ShopGoods discountShopGoods = new ShopGoods();
        discountShopGoods.setIsDiscount(0);
        shopGoodsMapper.update(discountShopGoods, new QueryWrapper<ShopGoods>().in("goods_id", activeGoodsIds));
        shopGoodsSkuMapper.update(null, new UpdateWrapper<ShopGoodsSku>().in("goods_id", activeGoodsIds).setSql("sale_price = price"));
        return true;
    }

    @Override
    public void discountClose(Integer activeId) {

        coreActiveService.close(activeId, RequestUtils.siteId());
        cancelGoodsDiscount(activeId);
    }

    @Override
    public void discountStartAfter(Integer activeId) {
        setGoodsDiscount(activeId);
    }

    @Override
    public void discountEndAfter(Integer activeId) {
        cancelGoodsDiscount(activeId);
    }

    @Override
    public PageResult<ShopDiscountOrderListVo> order(Integer activeId, PageParam pageParam, ShopDiscountOrderSearchParam searchParam) {
        MPJQueryWrapper<ShopOrderDiscount> wrapper = new MPJQueryWrapper<>();
        wrapper.setAlias("od")
                .innerJoin("?_shop_order o ON o.order_id = od.order_id".replace("?_", GlobalConfig.tablePrefix))
                .leftJoin("?_member m ON o.member_id = m.member_id".replace("?_", GlobalConfig.tablePrefix))
                .leftJoin("?_pay p ON o.out_trade_no = p.out_trade_no".replace("?_", GlobalConfig.tablePrefix));
        wrapper.select("o.*,m.nickname,p.type");
        wrapper.eq("o.site_id", RequestUtils.siteId());
        wrapper.gt("o.pay_time", 0);
        wrapper.eq("od.discount_type", "discount");
        wrapper.eq("od.discount_type_id", activeId);
        if (ObjectUtil.isNotEmpty(searchParam.getSearchName())) wrapper.like("order_no", searchParam.getSearchName());
        if (ObjectUtil.isNotEmpty(searchParam.getCreateTime())) QueryMapperUtils.buildByTime(wrapper, "o.create_time", searchParam.getCreateTime());
        if (ObjectUtil.isNotEmpty(searchParam.getPayTime())) QueryMapperUtils.buildByTime(wrapper, "o.pay_time", searchParam.getPayTime());

        IPage<ShopDiscountOrderListVo> iPage = shopOrderDiscountMapper.selectJoinPage(new Page<>(pageParam.getPage(), pageParam.getLimit()),ShopDiscountOrderListVo.class, wrapper);
        for (ShopDiscountOrderListVo vo : iPage.getRecords()) {
            ShopDiscountOrderListVo.Member memberVo = new ShopDiscountOrderListVo.Member();
            BeanUtils.copyProperties(vo, memberVo);

            ShopDiscountOrderListVo.Pay payVo = new ShopDiscountOrderListVo.Pay();
            BeanUtils.copyProperties(vo, payVo);

            vo.setMember(memberVo);
            vo.setPay(payVo);
        }
        return PageResult.build(iPage);
    }

    @Override
    public PageResult<ShopDiscountMemberListVo> member(PageParam pageParam, Integer activeId, ShopDiscountMemberSearchParam shopDiscountMemberSearchParam) {
        MPJQueryWrapper<ShopOrderDiscount> wrapper = new MPJQueryWrapper<>();
        wrapper.setAlias("od")
                .innerJoin("?_shop_order o ON o.order_id = od.order_id".replace("?_", GlobalConfig.tablePrefix))
                .innerJoin("?_member m ON o.member_id = m.member_id".replace("?_", GlobalConfig.tablePrefix));
        wrapper.select("count(o.order_id) as member_count, sum(order_money) as active_order_money, o.pay_time as create_time, m.member_id, m.nickname, m.mobile, m.headimg");
        wrapper.eq("od.discount_type", "discount");
        wrapper.eq("od.discount_type_id", activeId);
        wrapper.gt("o.pay_time", 0);
        wrapper.groupBy("o.member_id");
        wrapper.orderByDesc("o.pay_time");

        IPage<ShopDiscountMemberListVo> iPage = shopOrderDiscountMapper.selectJoinPage(new Page<>(pageParam.getPage(), pageParam.getLimit()), ShopDiscountMemberListVo.class, wrapper);
        for (ShopDiscountMemberListVo vo : iPage.getRecords()) {
            MemberBriefInfoVo memberVo = new MemberBriefInfoVo();
            BeanUtils.copyProperties(vo, memberVo);
            vo.setMember(memberVo);
        }
        return PageResult.build(iPage);
    }

    @Override
    public PageResult<ShopDiscountGoodsListVo> goods(PageParam pageParam, Integer activeId, ShopDiscountGoodsSearchParam searchParam) {
        Integer page  = pageParam.getPage();
        Integer limit = pageParam.getLimit();

        MPJQueryWrapper<ShopActiveGoods> wrapper = new MPJQueryWrapper<>();
        wrapper.setAlias("nsag")
                .innerJoin("?_shop_goods nsg ON nsag.goods_id = nsg.goods_id".replace("?_", GlobalConfig.tablePrefix));
        wrapper.select("nsag.*");
        wrapper.eq("nsag.site_id", RequestUtils.siteId());
        wrapper.eq("nsag.active_id", activeId);
        if(ObjectUtil.isNotEmpty(searchParam.getKeyword()))wrapper.like("nsg.goods_name", searchParam.getKeyword());

        IPage<ShopDiscountGoodsListVo> iPage = shopActiveGoodsMapper.selectJoinPage(new Page<>(page, limit), ShopDiscountGoodsListVo.class, wrapper);
        for (ShopDiscountGoodsListVo vo : iPage.getRecords()) {
            ShopDiscountGoodsListVo.Goods goodsVo = shopGoodsMapper.selectJoinOne(ShopDiscountGoodsListVo.Goods.class, new MPJQueryWrapper<ShopGoods>()
                    .setAlias("g")
                    .innerJoin("?_shop_goods_sku gs ON gs.goods_id = g.goods_id and gs.is_default = 1".replace("?_", GlobalConfig.tablePrefix))
                    .select("g.goods_id,g.site_id,g.goods_name,g.goods_cover,gs.price").eq("g.goods_id", vo.getGoodsId()));
            ShopDiscountGoodsListVo.GoodsSku skuVo = new ShopDiscountGoodsListVo.GoodsSku();
            skuVo.setPrice(goodsVo.getPrice());
            vo.setGoods(goodsVo);
            vo.setGoodsSku(skuVo);
        }

        return PageResult.build(iPage);
    }

    @Override
    public void setConfig(String key, JSONArray data) {
        coreConfigService.setConfig(RequestUtils.siteId(), key, data);
    }

    @Override
    public JSONArray getDiscountBannerConfig() {
        return coreConfigService.getConfigArrayValue(RequestUtils.siteId(), "SHOP_DISCOUNT_BANNER_CONFIG");
    }


    @Override
    @Transactional
    public void orderPayAfter(ShopOrder order) {
        List<ShopOrderDiscount> orderRiscount = shopOrderDiscountMapper.selectList(new QueryWrapper<ShopOrderDiscount>().select("discount_type_id").eq("order_id", order.getOrderId()).eq("discount_type", ActiveClassEnum.DISCOUNT.getType()));
        if (ObjectUtil.isEmpty(orderRiscount)) return;

        // 查询订单项
        List<ShopOrderGoods> orderGoods = shopOrderGoodsMapper.selectList(new QueryWrapper<ShopOrderGoods>().select("goods_id,SUM(goods_money) as goods_money,SUM(num) as num").eq("order_id", order.getOrderId()).groupBy("goods_id"));
        Map<Integer, ShopOrderGoods> orderGoodsMap = orderGoods.stream().collect(Collectors.toMap(ShopOrderGoods::getGoodsId, i -> i));

        for (ShopOrderDiscount item: orderRiscount) {
            long participationNum = this.getMemberActiveParticipationNum(item.getDiscountTypeId(), order.getMemberId());
            BigDecimal activeOrderMoney = new BigDecimal(0);
            Integer activeSuccessNum = 0;

            UpdateWrapper activeUpdate = new UpdateWrapper<ShopActive>();
            activeUpdate.setSql("active_order_num = active_order_num + 1");
            if (participationNum == 1) activeUpdate.setSql("active_member_num = active_member_num + 1");

            List<ShopActiveGoods> activeGoodsList = shopActiveGoodsMapper.selectList(new QueryWrapper<ShopActiveGoods>().eq("active_id", item.getDiscountTypeId()).in("goods_id", orderGoodsMap.keySet()));
            for (ShopActiveGoods activeGoodsItem: activeGoodsList) {
                ShopOrderGoods orderGoodsItem = orderGoodsMap.get(activeGoodsItem.getGoodsId());
                activeOrderMoney = activeOrderMoney.add(orderGoodsItem.getGoodsMoney());
                activeSuccessNum += orderGoodsItem.getNum();

                UpdateWrapper activeGoodsUpdate = new UpdateWrapper<>();
                activeGoodsUpdate.setSql("active_goods_order_money = active_goods_order_num + " + orderGoodsItem.getGoodsMoney());
                activeGoodsUpdate.setSql("active_goods_order_num = active_goods_order_num + 1");
                activeGoodsUpdate.setSql("active_goods_success_num = active_goods_success_num + " + orderGoodsItem.getNum());
                if (participationNum == 1) activeGoodsUpdate.setSql("active_goods_member_num = active_goods_member_num + 1");
                activeGoodsUpdate.eq("active_goods_id", activeGoodsItem.getActiveGoodsId());
                shopActiveGoodsMapper.update(null, activeGoodsUpdate);
            }

            activeUpdate.setSql("active_order_money = active_order_money + " + activeOrderMoney);
            activeUpdate.setSql("active_success_num = active_success_num + " + activeSuccessNum);
            activeUpdate.eq("active_id", item.getDiscountTypeId());
            shopActiveMapper.update(null, activeUpdate);
        }
    }


    /**
     * 获取会员参与活动的次数
     *
     * @param activeId
     * @param memberId
     * @return
     */
    private Long getMemberActiveParticipationNum(Integer activeId, Integer memberId) {
        MPJQueryWrapper<ShopOrderDiscount> wrapper = new MPJQueryWrapper<>();
        wrapper.setAlias("od")
                .innerJoin("?_shop_order o ON o.order_id = od.order_id".replace("?_", GlobalConfig.tablePrefix));
        wrapper.eq("od.discount_type", "discount");
        wrapper.eq("od.discount_type_id", activeId);
        wrapper.gt("o.pay_time", 0);
        wrapper.eq("o.member_id", memberId);
        wrapper.groupBy("od.order_id");

        return shopOrderDiscountMapper.selectJoinCount(wrapper);
    }
}
