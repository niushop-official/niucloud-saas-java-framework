package com.niu.shop.listener.third_addon.order;

import cn.hutool.core.util.ObjectUtil;
import com.niu.core.common.annotation.EventCallback;
import com.niu.core.entity.member.MemberAddress;
import com.niu.core.event.common.CommonEventDefiner;
import com.niu.shop.service.core.third_addon.order.ICoreOrderDeliveryService;
import com.niu.shop.service.core.third_addon.order.param.CalculateDeliveryMoneyParam;
import jakarta.annotation.Resource;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;

@Log4j2
@EventCallback("shop")
@Component
public class OrderAmountServiceListener extends CommonEventDefiner {

    @Resource
    private ICoreOrderDeliveryService coreOrderDeliveryService;

    @Override
    public CommonEventResult handleCallback(CommonEvent commonEvent) {
        if (commonEvent.getEventName().equals("ThirdAddonOrderAmount") && "shop".equals(commonEvent.getSource())) {
            Map<String, Object> data = (Map<String, Object>) commonEvent.getData();
            CommonEventDefiner.CommonEventResult commonResult = new CommonEventDefiner.CommonEventResult();
            commonResult.setDataMap(getDeliveryMoney(data));
            return commonResult;
        }
        return null;
    }

    private Map<String, Object> getDeliveryMoney(Map<String, Object> data) {
        CalculateDeliveryMoneyParam param = new CalculateDeliveryMoneyParam();
        param.setSiteId(Integer.parseInt(data.get("site_id").toString()));
        param.setMemberId(Integer.parseInt(data.get("member_id").toString()));
        if (data.containsKey("address_info") && ObjectUtil.isNotEmpty(data.get("address_info"))) {
            MemberAddress address = (MemberAddress) data.get("address_info");
            param.setProvinceId(address.getProvinceId());
            param.setCityId(address.getCityId());
            param.setDistrictId(address.getDistrictId());
        }
        param.setSourceSkuId(Integer.parseInt(data.get("source_sku_id").toString()));
        param.setNum(Integer.parseInt(data.get("num").toString()));
        BigDecimal deliveryMoney = coreOrderDeliveryService.calculate(param);
        return Map.of("name", "配送费", "key", "delivery_money", "amount", deliveryMoney);
    }
}
