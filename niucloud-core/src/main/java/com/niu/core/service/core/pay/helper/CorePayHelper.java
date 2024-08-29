package com.niu.core.service.core.pay.helper;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.component.context.SpringContext;
import com.niu.core.component.pay.IPayProvider;
import com.niu.core.component.pay.factory.PayProviderFactory;
import com.niu.core.component.pay.model.PayModel;
import com.niu.core.entity.member.Member;
import com.niu.core.entity.pay.PayChannel;
import com.niu.core.enums.common.ChannelEnum;
import com.niu.core.service.core.member.ICoreMemberService;
import com.niu.core.service.core.pay.ICorePayChannelService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public class CorePayHelper {

    /**
     * @param siteId  ====> 站点ID
     * @param channel ====> 支付渠道
     * @param payType ====> 支付类型
     * @param dataMap
     * @return
     */
    public static Object pay(Integer siteId, String channel, String payType, Map<String, Object> dataMap) {
        PayChannel payChannel = getPayChannel(siteId, payType, channel);
        JSONObject jsonObject = getPayChannelConfig(payChannel);
        IPayProvider payProvider = PayProviderFactory.create(payType);
        if (channel.equals(ChannelEnum.WECHAT.getCode())) {
            Integer memberId = (Integer) dataMap.get("memberId");
            String openid = (String) dataMap.get("openid");
            if (StrUtil.isEmpty(openid)) {
                ICoreMemberService coreMemberService = SpringContext.bean(ICoreMemberService.class);
                Member memberInfo = coreMemberService.getMember(siteId, memberId);
            }
        }
        PayModel payModel = new PayModel();
        payProvider.pay(jsonObject, payModel);

        return null;
    }

    /**
     * 构建支付通知URL
     *
     * @param siteId
     * @param channel
     * @param type
     * @param action
     * @return
     */
    private String buildNotifyUrl(Integer siteId, String channel, String type, String action) {
        String[] param = {siteId.toString(), channel, type, action};
        return RequestUtils.handler().getScheme() + "://" + RequestUtils.getRequestDomain() + "/api/pay/notify/" + String.join("/", param);
    }

    /**
     * @param request
     * @param response
     * @return
     */
    public static Object asyncNotify(HttpServletRequest request, HttpServletResponse response) {

        return null;
    }

    /**
     * @param payChannel
     * @return
     */
    public static JSONObject getPayChannelConfig(PayChannel payChannel) {
        String config = payChannel.getConfig();
        return JSONUtil.parseObj(config);
    }

    /**
     * 获取支付渠道
     *
     * @param siteId
     * @param type
     * @param channel
     * @return
     */
    public static PayChannel getPayChannel(Integer siteId, String type, String channel) {
        // 查询支付渠道
        ICorePayChannelService corePayChannelService = SpringContext.bean(ICorePayChannelService.class);
        return corePayChannelService.find(siteId, type, channel);
    }

}
