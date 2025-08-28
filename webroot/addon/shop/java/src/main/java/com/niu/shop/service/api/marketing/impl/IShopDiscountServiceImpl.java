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
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.niu.core.common.config.GlobalConfig;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.object.ObjectUtils;
import com.niu.core.service.core.sys.ICoreConfigService;
import com.niu.shop.entity.active.ShopActive;
import com.niu.shop.entity.active.ShopActiveGoods;
import com.niu.shop.entity.discount.ShopDiscount;
import com.niu.shop.entity.discount.ShopDiscountGoods;
import com.niu.shop.entity.goods.ShopGoods;
import com.niu.shop.entity.goods.ShopGoodsLabel;
import com.niu.shop.entity.goods.ShopGoodsSku;
import com.niu.shop.entity.order.ShopOrder;
import com.niu.shop.enums.active.ActiveStatusEnum;
import com.niu.shop.mapper.active.ShopActiveGoodsMapper;
import com.niu.shop.mapper.active.ShopActiveMapper;
import com.niu.shop.mapper.discount.ShopDiscountGoodsMapper;
import com.niu.shop.mapper.discount.ShopDiscountMapper;
import com.niu.shop.mapper.goods.ShopGoodsSkuMapper;
import com.niu.shop.service.admin.goods.vo.ShopGoodsLabelListVo;
import com.niu.shop.service.admin.marketing.vo.ShopDiscountOrderListVo;
import com.niu.shop.service.api.marketing.IShopDiscountService;
import com.niu.shop.service.api.marketing.param.DiscountGoodsSearchParam;
import com.niu.shop.service.api.marketing.param.ShopDiscountListParam;
import com.niu.shop.service.api.marketing.vo.*;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class IShopDiscountServiceImpl implements IShopDiscountService {

    @Resource
    ShopActiveMapper shopActiveMapper;

    @Resource
    ShopDiscountMapper shopDiscountMapper;

    @Resource
    ShopActiveGoodsMapper shopActiveGoodsMapper;

    @Resource
    ICoreConfigService coreConfigService;

    @Resource
    ShopDiscountGoodsMapper shopDiscountGoodsMapper;

    @Resource
    ShopGoodsSkuMapper shopGoodsSkuMapper;

    @Override
    public ShopDiscountGoodsInfoVo getInfoByGoods(Integer goodsId) {
        ShopDiscountGoods shopActiveGoods = shopDiscountGoodsMapper.selectOne(new QueryWrapper<ShopDiscountGoods>()
                .eq("site_id", RequestUtils.siteId())
                .eq("status", "active")
                .eq("goods_id", goodsId)
                .last("limit 1")
        );
        ShopDiscountGoodsInfoVo vo = new ShopDiscountGoodsInfoVo();
        if (shopActiveGoods != null) {
            BeanUtils.copyProperties(shopActiveGoods, vo);
            vo.setActive(shopDiscountMapper.selectById(shopActiveGoods.getDiscountId()));
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
    public PageResult<ShopDiscountGoodsListNewVo> getGoodsPage(PageParam pageParam, DiscountGoodsSearchParam searchParam) {
        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();

        // 创建MPJQueryWrapper实例
        MPJQueryWrapper<ShopDiscountGoods> queryWrapper = new MPJQueryWrapper();

        // 设置查询条件
        queryWrapper.setAlias("sdg")
                .select("nsg.site_id,nsg.goods_category,nsg.goods_cover,nsg.goods_id,nsg.goods_name,nsg.goods_type,nsg.is_discount,nsg.member_discount,nsg.unit,nsg.sub_title,nsg.sale_num + nsg.virtual_sale_num as sale_num," +
                        "sdg.sku_id,sdg.discount_id,sdg.status as discount_status,sdg.type,sdg.rate,sdg.rate as discount_rate,sdg.discount_price,sdg.reduce_money,sdg.type as discount_type")  // 选择商品表的所有字段
                .innerJoin("nc_shop_goods as nsg on sdg.goods_id = nsg.goods_id")  // 设置关联表
                .eq("sdg.site_id", RequestUtils.siteId())
                .in("sdg.status", Arrays.asList("active", "not_active"));
        if(ObjectUtil.isNotEmpty(searchParam.getDiscountId())){
            queryWrapper.eq("sdg.discount_id", searchParam.getDiscountId());
        }
        queryWrapper.groupBy("sdg.goods_id");

        // 执行查询
        IPage<ShopDiscountGoodsListNewVo> iPage = shopDiscountGoodsMapper.selectJoinPage(new Page<>(pageParam.getPage(), pageParam.getLimit()), ShopDiscountGoodsListNewVo.class, queryWrapper);

        List<ShopDiscountGoodsListNewVo> list = iPage.getRecords();
        for (int i=0; i<list.size(); i++) {
            List<ShopGoodsSku> shopGoodsSkuList = shopGoodsSkuMapper.selectList(new QueryWrapper<ShopGoodsSku>().eq("goods_id", list.get(i).getGoodsId()).eq("sku_id", list.get(i).getSkuId()));
            ShopDiscountGoodsListNewVo.SkuInfo goodsSku = new ShopDiscountGoodsListNewVo.SkuInfo();
            BeanUtils.copyProperties(shopGoodsSkuList.get(0), goodsSku);
            goodsSku.setDiscountType(list.get(i).getType());
            goodsSku.setDiscountPrice(list.get(i).getDiscountPrice());
            goodsSku.setDiscountRate(list.get(i).getDiscountRate());
            goodsSku.setReduceMoney(list.get(i).getReduceMoney());
            list.get(i).setGoodsSku(goodsSku);
        }
        return PageResult.build(page, limit, iPage.getTotal()).setData(list);
    }

    /**
     * 限时折扣列表
     *
     * @param shopDiscountListParam
     * @return
     */
    @Override
    public List<ShopDiscountVo> getList(ShopDiscountListParam shopDiscountListParam) {
        QueryWrapper<ShopDiscount> queryWrapper = new QueryWrapper();
        queryWrapper.eq("site_id", RequestUtils.siteId())
                .in("status", Arrays.asList("active", "not_active"));
        if(ObjectUtil.isNotEmpty(shopDiscountListParam.getActiveName()))
        {
            queryWrapper.like("name", shopDiscountListParam.getActiveName());
        }
        if(ObjectUtil.isNotEmpty(shopDiscountListParam.getLimit()))
        {
            queryWrapper.last("limit " + shopDiscountListParam.getLimit());
        }
        queryWrapper.orderByAsc("start_time");
        List<ShopDiscount> shopActiveList = shopDiscountMapper.selectList(queryWrapper);
        List<ShopDiscountVo> list = new LinkedList<>();
        for (ShopDiscount item : shopActiveList) {
            ShopDiscountVo vo = new ShopDiscountVo();
            BeanUtils.copyProperties(item, vo);
            list.add(vo);
        }
        return list;
    }
}
