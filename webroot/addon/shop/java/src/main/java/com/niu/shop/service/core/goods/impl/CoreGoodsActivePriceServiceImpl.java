package com.niu.shop.service.core.goods.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.service.api.member.IMemberLevelService;
import com.niu.core.service.api.member.vo.MemberLevelInfoVo;
import com.niu.shop.entity.discount.ShopDiscountGoods;
import com.niu.shop.enums.goods.GoodsPriceTypeEnum;
import com.niu.shop.mapper.discount.ShopDiscountGoodsMapper;
import com.niu.shop.service.core.goods.ICoreGoodsActivePriceService;
import com.niu.shop.service.core.goods.param.GetShowPriceParam;
import com.niu.shop.service.core.goods.vo.ShowPriceVo;
import jakarta.annotation.Resource;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class CoreGoodsActivePriceServiceImpl implements ICoreGoodsActivePriceService {

    @Resource
    private IMemberLevelService memberLevelService;

    @Resource
    private ShopDiscountGoodsMapper discountGoodsMapper;

    @Override
    public ShowPriceVo getShowPrice(GetShowPriceParam param) {
        List<ShowPriceVo> showPriceList =  new ArrayList<>();

        showPriceList.add(ShowPriceVo.builder()
            .showPrice(param.getPrice())
            .showType(GoodsPriceTypeEnum.ORIGINAL_PRICE.getType())
            .build());

        // 会员价
        if (param.getMemberId() != null && param.getMemberId() > 0) {
            MemberLevelInfoVo memberLevel = memberLevelService.findMemberLevel(param.getMemberId());
            JSONObject memberPriceConfig = ObjectUtil.isNotEmpty(param.getMemberPrice()) ? JSONUtil.parseObj(param.getMemberPrice()) : new JSONObject();

            showPriceList.add(ShowPriceVo.builder()
                .showPrice(calculateMemberPrice(memberLevel, param.getMemberDiscount(), memberPriceConfig, param.getPrice()))
                .showType(GoodsPriceTypeEnum.MEMBER_PRICE.getType())
                .build());
        }

        // 限时折扣价
        ShopDiscountGoods discount = discountGoodsMapper.selectOne(new QueryWrapper<ShopDiscountGoods>()
            .eq("sku_id", param.getSkuId())
            .eq("status", "active")
            .eq("is_enabled", 1)
            .last("limit 1")
        );
        if (discount != null) {
            showPriceList.add(ShowPriceVo.builder()
                .showPrice(discount.getDiscountPrice())
                .showType(GoodsPriceTypeEnum.DISCOUNT_PRICE.getType())
                .activeId(discount.getDiscountId())
                .build());
        }

        return getMinShowPrice(showPriceList, param.getPrice());
    }

    /**
     * 提取最低价格（优先考虑原价）
     * @param priceData 价格数据列表
     * @param originalPrice 原价
     * @return 最低价格信息
     */
    private ShowPriceVo getMinShowPrice(List<ShowPriceVo> priceData, BigDecimal originalPrice) {
        // 按价格和优先级排序
        priceData.sort(Comparator.comparing(ShowPriceVo::getShowPrice)
            .thenComparing(vo -> {
                // 根据价格类型获取排序值
                for (GoodsPriceTypeEnum typeEnum : GoodsPriceTypeEnum.values()) {
                    if (typeEnum.getType().equals(vo.getShowType())) {
                        return typeEnum.getSort();
                    }
                }
                return Integer.MAX_VALUE; // 未知类型放在最后
            }));

        // 获取排序后的第一个元素（最低价格）
        Optional<ShowPriceVo> minPriceOpt = priceData.stream().findFirst();
        if (minPriceOpt.isPresent()) {
            ShowPriceVo minPrice = minPriceOpt.get();

            // 如果最低价格与原价相同但类型不是原价，则修正为原价类型
            if (!GoodsPriceTypeEnum.ORIGINAL_PRICE.getType().equals(minPrice.getShowType())
                && originalPrice.compareTo(minPrice.getShowPrice()) == 0) {
                minPrice.setShowType(GoodsPriceTypeEnum.ORIGINAL_PRICE.getType());
            }

            return minPrice;
        }

        // 如果没有价格数据，返回原价
        return ShowPriceVo.builder()
            .showPrice(originalPrice)
            .showType(GoodsPriceTypeEnum.ORIGINAL_PRICE.getType())
            .build();
    }

    @Override
    public BigDecimal calculateMemberPrice(MemberLevelInfoVo memberLevel, String calculateType, JSONObject config, BigDecimal price) {
        if (ObjectUtil.hasEmpty(memberLevel, calculateType)) {
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
        } else if (calculateType.equals("fixed_price") && config != null) {
            // 按指定价格
            if (config.getStr("level_" + memberLevel.getLevelId()) != null) {
                return new BigDecimal(Double.valueOf(config.getStr("level_" + memberLevel.getLevelId())));
            }
        }
        return price;
    }


}
