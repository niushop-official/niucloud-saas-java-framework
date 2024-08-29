package com.niu.core.service.admin.pay;

import cn.hutool.json.JSONObject;
import com.niu.core.common.domain.PageResult;
import com.niu.core.service.admin.pay.param.PayChannelAllSetParam;
import com.niu.core.service.admin.pay.param.PayChannelParam;
import com.niu.core.service.admin.pay.param.PayChannelSearchParam;
import com.niu.core.common.domain.PageParam;
import com.niu.core.service.admin.pay.vo.PayChannelInfoVo;
import com.niu.core.service.admin.pay.vo.PayChannelListVo;
import com.niu.core.service.admin.pay.vo.PayChanneltemVo;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Map;

/**
 * 支付渠道配置服务接口
 */
public interface IPayChannelService {

    /**
     * 支付渠道配置列表
     */
    Map<String, PayChanneltemVo> list();

    /**
     * 多渠道设置
     * @param param
     */
    void setAll(PayChannelAllSetParam param);

    /**
     *
     * @param param
     */
    void set(String channel, String type, JSONObject param);

    List<PayChannelListVo> getListByChannel(String channel);

    /**
     * 转账设置
     */
    void setTransfer(JSONObject param);
}
