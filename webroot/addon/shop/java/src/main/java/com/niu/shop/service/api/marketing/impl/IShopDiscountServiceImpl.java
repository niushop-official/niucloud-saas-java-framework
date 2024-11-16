package com.niu.shop.service.api.marketing.impl;

import cn.binarywang.wx.miniapp.constant.WxMaApiUrlConstants;
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
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.object.ObjectUtils;
import com.niu.core.service.core.sys.ICoreConfigService;
import com.niu.shop.entity.active.ShopActive;
import com.niu.shop.entity.active.ShopActiveGoods;
import com.niu.shop.entity.goods.ShopGoodsLabel;
import com.niu.shop.entity.order.ShopOrder;
import com.niu.shop.enums.active.ActiveStatusEnum;
import com.niu.shop.mapper.active.ShopActiveGoodsMapper;
import com.niu.shop.mapper.active.ShopActiveMapper;
import com.niu.shop.service.admin.goods.vo.ShopGoodsLabelListVo;
import com.niu.shop.service.admin.marketing.vo.ShopDiscountOrderListVo;
import com.niu.shop.service.api.marketing.IShopDiscountService;
import com.niu.shop.service.api.marketing.param.DiscountGoodsSearchParam;
import com.niu.shop.service.api.marketing.param.ShopDiscountListParam;
import com.niu.shop.service.api.marketing.vo.ShopDiscountGoodsInfoVo;
import com.niu.shop.service.api.marketing.vo.ShopDiscountGoodsListVo;
import com.niu.shop.service.api.marketing.vo.ShopDiscountListVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class IShopDiscountServiceImpl implements IShopDiscountService {

    @Resource
    ShopActiveMapper shopActiveMapper;

    @Resource
    ShopActiveGoodsMapper shopActiveGoodsMapper;

    @Resource
    ICoreConfigService coreConfigService;

    @Override
    public ShopDiscountGoodsInfoVo getInfoByGoods(Integer goodsId) {
        ShopActiveGoods shopActiveGoods = shopActiveGoodsMapper.selectOne(new QueryWrapper<ShopActiveGoods>()
                .eq("site_id", RequestUtils.siteId())
                .eq("active_goods_status", "active")
                .eq("goods_id", goodsId)
                .eq("active_class", "discount"));
        ShopDiscountGoodsInfoVo vo = new ShopDiscountGoodsInfoVo();
        if (shopActiveGoods != null) {
            BeanUtils.copyProperties(shopActiveGoods, vo);
            vo.setActive(shopActiveMapper.selectById(shopActiveGoods.getActiveId()));
            return vo;
        }
        return null;
    }

    /**
     * 限时折扣配置
     *
     * @return
     */
    @Override
    public JSONArray getDiscountBannerConfig() {
        return coreConfigService.getConfigArrayValue(RequestUtils.siteId(), "SHOP_DISCOUNT_BANNER_CONFIG");
    }

    /**
     * 获取活动商品列表
     *
     * @param pageParam
     * @param searchParam
     */
    @Override
    public PageResult<ShopDiscountGoodsListVo> getGoodsPage(PageParam pageParam, DiscountGoodsSearchParam searchParam) {
        MPJQueryWrapper<ShopActiveGoods> wrapper = new MPJQueryWrapper<>();
        wrapper.setAlias("ag")
                .innerJoin("?_shop_goods g ON ag.goods_id = g.goods_id".replace("?_", GlobalConfig.tablePrefix));
        wrapper.select("g.*,ag.active_goods_value,ag.active_goods_status");
        wrapper.eq("ag.site_id", RequestUtils.siteId());
        wrapper.in("ag.active_goods_status", new String[]{ ActiveStatusEnum.ACTIVE.getStatus(), ActiveStatusEnum.NOT_ACTIVE.getStatus() });
        wrapper.eq("ag.active_class", "discount");
        wrapper.eq("ag.active_id", searchParam.getActiveId());

        IPage<ShopDiscountGoodsListVo> iPage = shopActiveGoodsMapper.selectJoinPage(new Page<>(pageParam.getPage(), pageParam.getLimit()), ShopDiscountGoodsListVo.class, wrapper);
        for (ShopDiscountGoodsListVo vo: iPage.getRecords()) {
            JSONArray activeGoodsValue = JSONUtil.parseArray(vo.getActiveGoodsValue());
            activeGoodsValue.toList(JSONObject.class).stream().filter(i -> i.getInt("is_enabled") == 1).collect(Collectors.toList()).sort(Comparator.comparingDouble(o -> ((JSONObject)o).getDouble("discount_price")));
            if (activeGoodsValue.size() > 0) {
                JSONObject sku = activeGoodsValue.getJSONObject(0);
                ShopDiscountGoodsListVo.GoodsSku skuVo = JSONUtil.toBean(sku, ShopDiscountGoodsListVo.GoodsSku.class);
                skuVo.setActiveDiscountPrice(sku.getBigDecimal("discount_price"));
                skuVo.setActiveDiscountRate(sku.getBigDecimal("discount_rate"));
                skuVo.setActiveDiscountType(sku.getStr("discount_type"));
                skuVo.setActiveReduceMoney(sku.getBigDecimal("reduce_money"));
                skuVo.setActiveSpecifyPrice(sku.getBigDecimal("specify_price"));
                vo.setGoodsSku(skuVo);
            }

            ShopDiscountGoodsListVo.ActiveGoods activeGoodsVo = new ShopDiscountGoodsListVo.ActiveGoods();
            activeGoodsVo.setActiveGoodsStatus(vo.getActiveGoodsStatus());
            vo.setActiveGoods(activeGoodsVo);
        }
        return PageResult.build(iPage);
    }

    /**
     * 限时折扣列表
     *
     * @param shopDiscountListParam
     * @return
     */
    @Override
    public List<ShopDiscountListVo> getList(ShopDiscountListParam shopDiscountListParam) {
        QueryWrapper<ShopActive> queryWrapper = new QueryWrapper();
        queryWrapper.eq("site_id", RequestUtils.siteId())
                .in("active_status", Arrays.asList("active", "not_active"))
                .eq("active_class", "discount");
        if(ObjectUtil.isNotEmpty(shopDiscountListParam.getActiveName()))
        {
            queryWrapper.like("active_name", shopDiscountListParam.getActiveName());
        }
        if(ObjectUtil.isNotEmpty(shopDiscountListParam.getLimit()))
        {
            queryWrapper.last("limit " + shopDiscountListParam.getLimit());
        }
        queryWrapper.orderByAsc("start_time");
        List<ShopActive> shopActiveList = shopActiveMapper.selectList(queryWrapper);
        List<ShopDiscountListVo> list = new LinkedList<>();
        for (ShopActive item : shopActiveList) {
            ShopDiscountListVo vo = new ShopDiscountListVo();
            BeanUtils.copyProperties(item, vo);
            list.add(vo);
        }
        return list;
    }
}
