package com.niu.shop.listener.treasure;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.niu.core.common.annotation.EventCallback;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.utils.CommonUtils;
import com.niu.core.common.utils.StringUtils;
import com.niu.core.event.common.CommonEventDefiner;
import com.niu.shop.entity.goods.ShopGoods;
import com.niu.shop.entity.goods.ShopGoodsSku;
import com.niu.shop.enums.active.MarketingActivityTypeEnum;
import com.niu.shop.mapper.goods.ShopGoodsMapper;
import jakarta.annotation.Resource;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@EventCallback("shop")
@Component
public class TreasureDataListener extends CommonEventDefiner {
    @Resource
    private ShopGoodsMapper shopGoodsMapper;

    @Override
    public CommonEventResult handleCallback(CommonEvent commonEvent) {
        if (commonEvent.getEventName().equals("TreasureDataList")) {
            JSONObject data = JSONUtil.parseObj(commonEvent.getData());
            String relateType = data.getStr("relate_type", "");
            if (!MarketingActivityTypeEnum.SHOP.getType().equals(relateType)) {
                return null;
            }

            MPJLambdaWrapper<ShopGoods> queryWrapper = new MPJLambdaWrapper<ShopGoods>()
                .selectAs(ShopGoods::getSiteId, TreasureDataResult::getSiteId)
                .selectAs(ShopGoods::getGoodsName, TreasureDataResult::getTreasureName)
                .selectAs(ShopGoods::getSubTitle, TreasureDataResult::getTreasureSubName)
                .selectAs(ShopGoods::getGoodsCover, TreasureDataResult::getTreasureImage)
                .selectAs(ShopGoodsSku::getPrice, TreasureDataResult::getTreasurePrice)
                .selectAs(ShopGoods::getGoodsId, TreasureDataResult::getRelateId)
                .leftJoin(ShopGoodsSku.class, ShopGoodsSku::getGoodsId, ShopGoods::getGoodsId)
                .eq(ShopGoodsSku::getSiteId, commonEvent.getSiteId())
                .eq(ShopGoodsSku::getIsDefault, 1)
                .eq(ShopGoods::getStatus, 1)
                .eq(ShopGoods::getIsGift, 0)
                .orderByAsc(ShopGoods::getSort)
                .orderByDesc(ShopGoods::getCreateTime);
            JSONObject where = data.getJSONObject("where");
            if (ObjectUtil.isNotEmpty(where)) {
                if (where.containsKey("keyword") && ObjectUtil.isNotEmpty(where.get("keyword"))) {
                    String keyword = where.getStr("keyword");
                    queryWrapper.and(wrapper -> {
                        wrapper.like(ShopGoods::getGoodsName, keyword).or().like(ShopGoods::getSubTitle, keyword);
                    });
                }

                if (where.containsKey("relate_ids") && ObjectUtil.isNotEmpty(where.get("relate_ids"))) {
                    List<Integer> relateIds = JSONUtil.toList(where.getJSONArray("relate_ids"), Integer.class);
                    if (CollectionUtils.isNotEmpty(relateIds)) {
                        queryWrapper.in(ShopGoods::getGoodsId, relateIds);
                    }
                }

                if (where.containsKey("join_ids") && ObjectUtil.isNotEmpty(where.get("join_ids"))) {
                    List<Integer> joinIds = JSONUtil.toList(where.getJSONArray("join_ids"), Integer.class);
                    if (CollectionUtils.isNotEmpty(joinIds)) {
                        queryWrapper.notIn(ShopGoods::getGoodsId, joinIds);
                    }
                }
            }
            Integer page = 0;
            Integer limit = 0;
            if (data.containsKey("page")) {
                PageParam pageParam = data.getBean("page", PageParam.class);
                page = pageParam.getPage();
                limit = pageParam.getLimit();
            }
            CommonEventResult result = new CommonEventResult();
            List<TreasureDataResult> shopGoods = null;
            if (page == 0 || limit == 0) {
                shopGoods = shopGoodsMapper.selectJoinList(TreasureDataResult.class,queryWrapper);
                result.setResult(shopGoods);
            } else {
                Page<TreasureDataResult> shopGoodsPage = shopGoodsMapper.selectJoinPage(new Page<>(page, limit),TreasureDataResult.class, queryWrapper);
                shopGoods = shopGoodsPage.getRecords();
                result.setResult(shopGoodsPage);
            }
            if(CollectionUtils.isEmpty(shopGoods)){
                return null;
            }
            return result;
        }
        return null;
    }

    @Data
    public static class TreasureDataResult {
        private Integer siteId;
        private String treasureName;
        private String treasureSubName;
        private String treasureImage;
        private BigDecimal treasurePrice;
        private String treasureUrl;
        private Integer relateId;
        private String relateType = "shop";

        public String getTreasureImage() {
            if (StringUtils.isEmpty(this.treasureImage)) {
                return "";
            }
            return CommonUtils.thumbImageMiddle(this.siteId,this.treasureImage);
        }

        public String getTreasureUrl() {
            return "/addon/shop/pages/goods/detail?goods_id=" + this.relateId;
        }
    }
}

