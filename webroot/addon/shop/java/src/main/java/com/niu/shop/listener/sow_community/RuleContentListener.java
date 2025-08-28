package com.niu.shop.listener.sow_community;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.niu.core.common.annotation.EventCallback;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.event.common.CommonEventDefiner;
import com.niu.shop.service.core.marketing.ICoreCouponMemberService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@EventCallback("shop")
@Component
public class RuleContentListener extends CommonEventDefiner {

    @Resource
    private ICoreCouponMemberService coreCouponMemberService;

    @Override
    public CommonEventResult handleCallback(CommonEvent commonEvent) {
        if(commonEvent.getEventName().equals("RuleContent")){
            CommonEventDefiner.CommonEventResult commonResult = new CommonEventDefiner.CommonEventResult();

            //获取传过来的条件数据data
            JSONObject dataParam = JSONUtil.parseObj(commonEvent.getData());
            if (ObjectUtil.isNotEmpty(dataParam.getStr("key")) && dataParam.getStr("key").equals("coupon")) {
                JSONArray valueList = dataParam.getJSONArray("value");
                //判断valueList是否为空
                if(ObjectUtil.isNotNull(valueList) && valueList.size()>0){
                    // 初始化总和
                    int count = 0;

                    // 循环遍历数组
                    List<String> couponList = new ArrayList<>();
                    List<Object> result = new ArrayList<>();
                    for (int i = 0; i < valueList.size(); i++) {
                        // 获取每个元素
                        JSONObject jsonObject = valueList.getJSONObject(i);

                        // 获取num值并累加
                        count += jsonObject.getInt("num");
                        String detail = jsonObject.getInt("num").toString() + "张 " + jsonObject.getStr("title");
                        couponList.add(detail);
                    }

                    Map<String, Object> data = new HashMap<>();
                    data.put("label", "优惠券"); // 标题
                    data.put("info", "优惠券*" + count); // 大概信息
                    data.put("detail", couponList); // 详细信息
                    result.add(data);
                    commonResult.setResult(result);
                }
            }

            return commonResult;
        }else if(commonEvent.getEventName().equals("SettleRewardListener")){
            JSONObject dataParam = JSONUtil.parseObj(commonEvent.getData());
            if (ObjectUtil.isNotEmpty(dataParam.getStr("key")) && dataParam.getStr("key").equals("coupon")) {
                Integer couponId = dataParam.getInt("coupon_id");
                Integer num = dataParam.getInt("num");
                Integer memberId = dataParam.getInt("member_id");
                Integer siteId = dataParam.getInt("site_id");
                coreCouponMemberService.sendCoupon(siteId,memberId,couponId,num);
            }
        }
        return null;
    }

}
