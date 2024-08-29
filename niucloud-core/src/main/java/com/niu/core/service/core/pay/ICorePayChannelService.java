package com.niu.core.service.core.pay;

import cn.hutool.json.JSONObject;
import com.niu.core.entity.pay.PayChannel;
import com.niu.core.service.core.pay.vo.PayTypeVo;
import com.niu.core.service.core.pay.param.PayChannelSearchParam;

import java.util.List;

/**
 * 支付渠道配置服务接口
 */
public interface ICorePayChannelService {

    /**
     * 支付渠道
     *
     * @param siteId
     * @param type
     * @param channel
     * @return
     */
    PayChannel find(Integer siteId, String type, String channel);

    PayChannel find(Integer siteId, PayChannelSearchParam payChannelSearchParam);

    List<PayTypeVo> getAllowPayTypeByChannel(Integer siteId, String channel, String tradeType);

    List<PayTypeVo> getAllowPayTypeByChannel(Integer siteId, String channel);

    JSONObject getConfigByChannelAndType(Integer siteId, String channel, String tradeType);


}
