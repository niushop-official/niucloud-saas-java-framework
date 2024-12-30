package com.niu.shop.service.core.member.driver;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.component.context.SpringContext;
import com.niu.core.enums.member.AccountTypeEnum;
import com.niu.core.service.core.member.ICoreMemberAccountService;
import com.niu.shop.entity.coupon.ShopCoupon;
import com.niu.shop.mapper.coupon.ShopCouponMapper;
import com.niu.shop.service.core.marketing.ICoreCouponMemberService;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Component
public class GiftCouponDriver {

    public ShopCouponMapper shopCouponMapper() {
        return SpringContext.bean(ShopCouponMapper.class);
    }

    public ICoreCouponMemberService coreCouponMemberService() {
        return SpringContext.bean(ICoreCouponMemberService.class);
    }


    /**
     * 获取礼包内容
     * @param siteId
     * @param config
     * @param scene
     * @return
     */
    public Object content(Integer siteId, JSONObject config, String scene) {
        List<ShopCoupon> couponList = shopCouponMapper().selectList(new QueryWrapper<ShopCoupon>().in("id", config.getJSONArray("coupon_id")));
        if (ObjectUtil.isEmpty(couponList)) return "";

        switch (scene) {
            case "admin":
                List<String> content = new LinkedList<>();
                for (ShopCoupon item : couponList) {
                    content.add(item.getTitle() + "*" + config.getByPath("coupon_list.id_" + item.getId(), String.class));
                }
                return String.join(",", content);
            case "member_level":
                JSONArray levelContent = new JSONArray();
                for (ShopCoupon item : couponList) {
                    JSONObject levelContentItem = new JSONObject();
                    levelContentItem.set("text", item.getPrice() + "元优惠券");
                    levelContentItem.set("background", "addon/shop/gift/gift_coupon_bg.png");
                    levelContent.add(levelContentItem);
                }
                return levelContent;
            case "member_sign":
                JSONArray signContent = new JSONArray();
                for (ShopCoupon item : couponList) {
                    JSONObject contentItem = new JSONObject();
                    contentItem.set("text", item.getPrice() + "元优惠券");
                    contentItem.set("background", "addon/shop/sign/coupon.png");
                    signContent.add(contentItem);
                }
                return signContent;
            case "member_sign_continue":
                JSONArray signContinueContent = new JSONArray();
                for (ShopCoupon item : couponList) {
                    JSONObject contentItem = new JSONObject();
                    contentItem.set("text", item.getPrice() + "元优惠券");
                    contentItem.set("background", "addon/shop/sign/coupon01.png");
                    signContinueContent.add(contentItem);
                }
                return signContinueContent;
            default:
                return "";
        }
    }

    /**
     * 发放礼包
     */
    public void grant(Integer siteId, Integer memberId, JSONObject config, Map<String, Object> vars) {
        try {
            JSONArray couponIds = config.getJSONArray("coupon_id");
            for (int i = 0; i < couponIds.size(); i++) {
                Integer couponId = couponIds.getInt(i);
                coreCouponMemberService().sendCoupon(siteId, memberId, couponId, config.getByPath("coupon_list.id_" + couponId, Integer.class));
            }
        } catch (Exception e) {
            System.out.println("优惠券发放失败");
            e.printStackTrace();
        }
    }
}
