package com.niu.shop.listener.sys;

import com.niu.core.common.annotation.EventCallback;
import com.niu.core.enums.site.AddonChildMenuEnum;
import com.niu.core.event.sys.ShowCustomerEventDefiner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@EventCallback("shop")
@Component
public class ShopShowCustomerListener extends ShowCustomerEventDefiner {

    @Override
    public ShowCustomerEventResult handleCallback(ShowCustomerEvent event) {
        ShowCustomerEventResult result = new ShowCustomerEventResult();
        Map<String, List<MenuItem>> data = new HashMap<>();

        // 系统工具菜单组（空）
        List<MenuItem> systemToolMenus = new ArrayList<>();

        // 营销工具菜单组
        List<MenuItem> marketingToolMenus = new ArrayList<>();
        marketingToolMenus.add(new MenuItem(
            "商品榜单",
            "展示热销商品排行榜",
            "static/resource/images/marketing/goods_rank.png",
            "shop_goods_rank",
            "/shop/marketing/goods_rank/config"
        ));

        // 营销活动菜单组
        List<MenuItem> marketingActiveMenus = new ArrayList<>();
        marketingActiveMenus.add(new MenuItem(
            "优惠券",
            "设置商家优惠券",
            "static/resource/images/marketing/coupon.png",
            "shop_goods_coupon",
            "/shop/marketing/coupon/list"
        ));
        marketingActiveMenus.add(new MenuItem(
            "满减送",
            "购满指定金额享受优惠",
            "static/resource/images/marketing/manjian.png",
            "shop_goods_manjian",
            "/shop/marketing/manjian/list"
        ));
        marketingActiveMenus.add(new MenuItem(
            "限时折扣",
            "商品限时促销打折",
            "static/resource/images/marketing/discount.png",
            "shop_goods_discount",
            "/shop/marketing/discount/list"
        ));
        marketingActiveMenus.add(new MenuItem(
            "积分商城",
            "客户积分兑换更多好物",
            "static/resource/images/marketing/exchange.png",
            "shop_point_goods",
            "/shop/marketing/exchange/goods_list"
        ));
        marketingActiveMenus.add(new MenuItem(
            "新人专享",
            "新人专属优惠活动",
            "static/resource/images/marketing/newcomer.png",
            "shop_goods_newcomer_discount",
            "/shop/marketing/newcomer/config"
        ));

        // 放入结果Map
        data.put(AddonChildMenuEnum.SYSTEM_TOOL.getKey(), systemToolMenus);
        data.put(AddonChildMenuEnum.MARKETING_TOOL.getKey(), marketingToolMenus);
        data.put(AddonChildMenuEnum.MARKETING_ACTIVE.getKey(), marketingActiveMenus);
        
        result.setData(data);
        return result;
    }
}
    