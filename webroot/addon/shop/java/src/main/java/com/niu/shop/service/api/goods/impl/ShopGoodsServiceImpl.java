package com.niu.shop.service.api.goods.impl;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.entity.member.Member;
import com.niu.core.entity.member.MemberLevel;
import com.niu.core.mapper.member.MemberLevelMapper;
import com.niu.core.mapper.member.MemberMapper;
import com.niu.shop.service.api.goods.IShopGoodsService;
import com.niu.shop.service.api.goods.vo.ShopGoodsMemberInfoVo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class ShopGoodsServiceImpl implements IShopGoodsService {
    private final MemberMapper memberMapper;
    private final MemberLevelMapper memberLevelMapper;

    @Override
    public ShopGoodsMemberInfoVo getMemberInfo() {
        LambdaQueryWrapper<Member> memberWrapper = new LambdaQueryWrapper<>();
        memberWrapper.eq(Member::getSiteId, RequestUtils.siteId())
                .eq(Member::getMemberId, RequestUtils.memberId());

        Member member = memberMapper.selectOne(memberWrapper);
        ShopGoodsMemberInfoVo vo = new ShopGoodsMemberInfoVo();
        BeanUtils.copyProperties(member, vo);

        MemberLevel memberLevel = memberLevelMapper.selectById(member.getMemberLevel());
        if (ObjectUtil.isNotEmpty(memberLevel)) {
            ShopGoodsMemberInfoVo.MemberLevel level = new ShopGoodsMemberInfoVo.MemberLevel();
            BeanUtils.copyProperties(memberLevel, level);
            vo.setLevel(level);
        }
        return vo;
    }

    @Override
    public BigDecimal getMemberPrice(ShopGoodsMemberInfoVo memberInfo, String memberDiscount, String memberPrice, BigDecimal price) {
        if (ObjectUtil.hasEmpty(memberDiscount, memberInfo)) {
            return NumberUtil.toBigDecimal(price);
        }

        BigDecimal priceNumber = NumberUtil.toBigDecimal(price).setScale(2, RoundingMode.HALF_UP);
        if ("discount".equals(memberDiscount) && ObjectUtil.isNotEmpty(memberInfo.getLevel().getLevelBenefits())) {
            JSONObject jsonObject = JSONObject.from(memberInfo.getLevel().getLevelBenefits());

            Object discount = jsonObject.get("discount");
            JSONObject discountJsonObject = JSONObject.from(discount);
            if (ObjectUtil.isAllNotEmpty(discount, discountJsonObject.get("is_use"))) {
                BigDecimal discountNumber = NumberUtil.toBigDecimal(discountJsonObject.get("discount").toString());
                priceNumber = priceNumber.multiply(discountNumber).divide(BigDecimal.TEN);
            }
        } else if ("fixed_price".equals(memberDiscount) && ObjectUtil.isNotEmpty(memberPrice)) {
            JSONObject jsonObject = JSONObject.from(memberPrice);
            Object memberLevelPrice = jsonObject.get("level_" + memberInfo.getMemberLevel());
            if (ObjectUtil.isNotEmpty(memberLevelPrice)) {
                priceNumber = NumberUtil.toBigDecimal(memberLevelPrice.toString()).setScale(2, RoundingMode.HALF_UP);
            }
        }

        return priceNumber;
    }
}
